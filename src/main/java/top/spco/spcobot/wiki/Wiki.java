/*
 * Copyright 2024 SpCo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.spcobot.wiki;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.apache.logging.log4j.Logger;
import top.spco.spcobot.wiki.action.ActionType;
import top.spco.spcobot.wiki.action.ActionTypes;
import top.spco.spcobot.wiki.action.PermissionChecker;
import top.spco.spcobot.wiki.action.filter.AbuseFilter;
import top.spco.spcobot.wiki.action.filter.AbuseFilterLogEntry;
import top.spco.spcobot.wiki.action.filter.SimplifiedAbuseFilterLogEntry;
import top.spco.spcobot.wiki.action.parameter.*;
import top.spco.spcobot.wiki.action.query.AbuseFiltersListModule;
import top.spco.spcobot.wiki.action.query.AllPagesListModule;
import top.spco.spcobot.wiki.action.query.AllUsersListModule;
import top.spco.spcobot.wiki.action.request.BlockRequest;
import top.spco.spcobot.wiki.action.request.QueryRequest;
import top.spco.spcobot.wiki.action.request.UnblockRequest;
import top.spco.spcobot.wiki.exception.InsufficientPermissionsException;
import top.spco.spcobot.wiki.user.*;
import top.spco.spcobot.wiki.util.CollectionUtil;
import top.spco.spcobot.wiki.util.JsonUtil;
import top.spco.spcobot.wiki.util.MapUtil;
import top.spco.spcobot.wiki.util.ParamUtil;
import top.spco.spcobot.wiki.util.LogUtil;

import java.io.IOException;
import java.net.CookieManager;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static top.spco.spcobot.wiki.util.JsonUtil.GSON;
import static top.spco.spcobot.wiki.util.MapUtil.paramsMap;

/**
 * Wiki实例。
 *
 * @author SpCo
 * @version 1.0.1
 * @since 0.1.0
 */
@SuppressWarnings("unused")
public final class Wiki implements UserAction {
    private final static Logger LOGGER = LogUtil.getLogger();
    private final OkHttpClient client;
    private final HttpUrl actionApi;
    private final CookieManager cookieManager;
    private final String username;
    private final String password;
    private final Assert loginAssert;
    private final HashMap<String, String> basicRequestParams = paramsMap("format", "json");
    private String csrfToken;
    private String patrolToken;
    private String userRightToken;
    private String watchToken;
    private String accountCreateToken;
    private String rollbackToken;
    private final Supplier<String> otpSupplier;
    private int normalApiLimit = 50;
    private int higherApiLimit = 500;

    /*
     * 重写Api调用方式：
     * query等action参数支持同时查询多种不同参数，这样可以减少api调用次数，减少调用api所花费的时间
     * 可以把每个不同query查询的请求看成不同的模块，
     */

    Wiki(HttpUrl actionApi, CookieManager cookieManager, Proxy proxy, String username, String password, Assert loginAssert, Supplier<String> otpSupplier) {
        this.username = username;
        this.password = password;
        this.loginAssert = loginAssert;
        this.actionApi = actionApi;
        this.cookieManager = cookieManager == null ? new CookieManager() : cookieManager;
        this.otpSupplier = otpSupplier;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new JavaNetCookieJar(this.cookieManager));
        builder.readTimeout(2, TimeUnit.MINUTES);
        if (proxy != null) {
            builder.proxy(proxy);
        }
        this.client = builder.build();
        if (loginAssert == Assert.USER || loginAssert == Assert.BOT) {
            try {
                login();
            } catch (Exception e) {
                throw new RuntimeException("Failed to login: " + e.getMessage(), e);
            }
        }
    }

    Wiki(HttpUrl actionApi, CookieManager cookieManager, Proxy proxy) {
        this(actionApi, cookieManager, proxy, null, null, Assert.ANON, null);
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    /**
     * 设置用于消息翻译的语言。
     *
     * @param languageCode 语言代码
     * @throws IllegalArgumentException 如果语言代码不在 {@link #supportedLanguages()} 中
     * @since 0.1.0
     */
    public void useLanguage(String languageCode) {
        if (!supportedLanguages().contains(languageCode)) {
            throw new IllegalArgumentException("Unsupported language code: " + languageCode);
        }
        basicRequestParams.put("uselang", languageCode);
    }

    /**
     * 检查此Wiki实例和另一个Wiki实例是否是同一站点。
     *
     * @param wiki 另一个Wiki实例
     * @return 是同一站点返回 {@code true} ，否则返回 {@code false}
     * @since 0.1.0
     */
    public boolean isSameWiki(Wiki wiki) {
        return wiki.actionApi.equals(this.actionApi);
    }

    /**
     * 构建基本的 HTTP 请求。
     *
     * @param params 请求的查询参数，其中键为参数名，值为参数值
     * @return 返回一个 {@link Request.Builder} 对象，用于进一步构建请求
     * @since 0.1.0
     */
    public Request.Builder requestBase(Map<String, String> params) {
        HttpUrl.Builder urlBuilder = actionApi.newBuilder();
        params.forEach(urlBuilder::addQueryParameter);
        basicRequestParams.forEach(urlBuilder::addQueryParameter);
        return new Request.Builder().url(urlBuilder.build());
    }

    /**
     * 直接通过 {@link Request} 创建一次调用
     *
     * @param request 一次调用的请求
     * @return 返回一个 {@link Call} 对象
     * @since 1.0.1
     */
    public Call newCall(Request request) {
        return client.newCall(request);
    }

    /**
     * 获取Wiki API 的 URL 地址。
     *
     * @return 返回Wiki API 的 URL 地址
     * @since 1.0.1
     */
    public HttpUrl getActionApi() {
        return actionApi;
    }

    /**
     * 执行 GET 请求，使用指定的操作类型和附加参数。
     *
     * @param type       指定的操作类型，包含基本请求参数和是否需要令牌的信息
     * @param additional 附加的查询参数，其中键为参数名，值为参数值
     * @return 返回一个 {@link Response} 对象，表示服务器的响应
     * @throws IOException 如果请求过程中发生输入输出异常
     * @since 0.1.0
     */
    @Deprecated
    public Response get(ActionTypes type, Map<String, String> additional) throws IOException {
        HashMap<String, String> params = new HashMap<>();
        if (type.needToken()) {
            params.put("token", csrfToken);
        }
        params.putAll(type.getBaseParams());
        params.putAll(additional);
        return newCall(requestBase(params).get().build()).execute();
    }

    /**
     * 执行 GET 请求，使用指定的操作类型
     *
     * @param type 操作类型
     * @return 返回一个 {@link Response} 对象，表示服务器的响应
     * @throws IOException 如果请求过程中发生输入输出异常
     * @since 0.1.0
     */
    @Deprecated
    public Response get(ActionTypes type) throws IOException {
        HashMap<String, String> params = new HashMap<>();
        if (type.needToken()) {
            params.put("token", csrfToken);
        }
        params.putAll(type.getBaseParams());

        return newCall(requestBase(params).get().build()).execute();
    }

    /**
     * 执行 GET 请求，使用提供的参数和附加参数。
     *
     * @param params     请求的查询参数，其中键为参数名，值为参数值
     * @param additional 附加的查询参数，其中键为参数名，值为参数值
     * @return 返回一个 {@link Response} 对象，表示服务器的响应
     * @throws IOException 如果请求过程中发生输入输出异常
     * @since 0.1.0
     */
    public Response get(Map<String, String> params, Map<String, String> additional) throws IOException {
        params.putAll(additional);
        return newCall(requestBase(params).get().build()).execute();
    }

    /**
     * 执行 GET 请求，使用提供的查询参数。
     *
     * @param params 请求的查询参数，其中键为参数名，值为参数值
     * @return 返回一个 {@link Response} 对象，表示服务器的响应
     * @throws IOException 如果请求过程中发生输入输出异常
     * @since 0.1.0
     */
    public Response get(Map<String, String> params) throws IOException {
        return newCall(requestBase(params).get().build()).execute();
    }

    /**
     * 执行 POST 请求。
     *
     * @param type 操作类型
     * @param form 包含表单数据的映射表，其中键为表单字段名，值为表单字段值
     * @return 返回一个 {@link Response} 对象，表示服务器的响应
     * @throws IOException 如果请求过程中发生输入输出异常
     * @since 0.1.0
     * @deprecated 请使用 {@link #post(ActionType, Map)}
     */
    @Deprecated
    public Response post(ActionTypes type, Map<String, String> form) throws IOException {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        form.forEach(formBodyBuilder::add);
        if (type.needToken()) {
            switch (type.getTokenType()) {
                case PATROL -> formBodyBuilder.add(type.getTokenParameterName(), patrolToken);
                case CSRF -> formBodyBuilder.add(type.getTokenParameterName(), csrfToken);
                case LOGIN -> formBodyBuilder.add(type.getTokenParameterName(), getLoginToken());
                case USER_RIGHTS -> formBodyBuilder.add(type.getTokenParameterName(), userRightToken);
                case WATCH -> formBodyBuilder.add(type.getTokenParameterName(), watchToken);
                case ROLLBACK -> formBodyBuilder.add(type.getTokenParameterName(), rollbackToken);
                case CREATE_ACCOUNT -> formBodyBuilder.add(type.getTokenParameterName(), accountCreateToken);
            }
        }
        return newCall(requestBase(type.getBaseParams()).post(formBodyBuilder.build()).build()).execute();
    }

    /**
     * 执行 POST 请求。
     *
     * @param type 操作类型
     * @param form 包含表单数据的映射表，其中键为表单字段名，值为表单字段值
     * @return 返回一个 {@link Response} 对象，表示服务器的响应
     * @throws IOException 如果请求过程中发生输入输出异常
     * @since 1.0.1
     */
    public Response post(ActionType type, Map<String, String> form) throws IOException {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        form.forEach(formBodyBuilder::add);
        if (type.needToken()) {
            switch (type.getTokenType()) {
                case PATROL -> formBodyBuilder.add(type.getTokenParameterName(), patrolToken);
                case CSRF -> formBodyBuilder.add(type.getTokenParameterName(), csrfToken);
                case LOGIN -> formBodyBuilder.add(type.getTokenParameterName(), getLoginToken());
                case USER_RIGHTS -> formBodyBuilder.add(type.getTokenParameterName(), userRightToken);
                case WATCH -> formBodyBuilder.add(type.getTokenParameterName(), watchToken);
                case ROLLBACK -> formBodyBuilder.add(type.getTokenParameterName(), rollbackToken);
                case CREATE_ACCOUNT -> formBodyBuilder.add(type.getTokenParameterName(), accountCreateToken);
            }
        }
        return newCall(requestBase(type.getBaseParams()).post(formBodyBuilder.build()).build()).execute();
    }

    /**
     * 执行 POST 请求。
     *
     * @param params 包含请求参数的映射表，其中键为参数名，值为参数值
     * @param form   包含表单数据的映射表，其中键为表单字段名，值为表单字段值
     * @return 返回一个 {@link Response} 对象，表示服务器的响应
     * @throws IOException 如果请求过程中发生输入输出异常
     * @since 0.1.0
     */
    public Response post(Map<String, String> params, Map<String, String> form) throws IOException {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        form.forEach(formBodyBuilder::add);

        return newCall(requestBase(params).post(formBodyBuilder.build()).build()).execute();
    }

    /**
     * 可继续的操作。
     *
     * @param type        操作类型
     * @param baseParams  基础请求参数
     * @param action      操作描述
     * @param bodyHandler 处理返回体的函数
     * @since 0.1.0
     */
    @Deprecated
    public void continuableAction(ActionTypes type, Map<String, String> baseParams, String action, Consumer<JsonObject> bodyHandler) {
        boolean continuable = false;
        Map<String, String> continueParam = new HashMap<>();
        do {
            Map<String, String> paramsMap = new HashMap<>(baseParams);
            if (continuable) {
                paramsMap.putAll(continueParam);
            }
            try (Response response = get(type, paramsMap)) {
                String body = checkAndGetBody(response, action);
                JsonObject bodyJson = GSON.fromJson(body, JsonObject.class);
                continuable = bodyJson.has("continue");
                if (continuable) {
                    JsonObject continueJson = bodyJson.getAsJsonObject("continue");
                    continueParam = MapUtil.jsonToMap(continueJson);
                }
                bodyHandler.accept(bodyJson);
            } catch (Exception e) {
                throw new RuntimeException("Failed to " + action + " : " + e.getMessage(), e);
            }
        } while (continuable);
    }

    /**
     * 刷新令牌。
     *
     * <p>该方法会检查当前登录状态，并在确认登录信息有效后获取新的令牌。
     *
     * @throws IOException      如果在获取令牌过程中发生输入输出异常
     * @throws RuntimeException 如果登录信息不匹配，表示当前用户未登录或登录状态无效
     * @since 0.1.0
     */
    public void refreshToken() throws IOException {
        if (!assertLogged()) {
            throw new RuntimeException("Login information does not match");
        }
        csrfToken = getCSRFToken();
        patrolToken = getPatrolToken();
        watchToken = getToken(TokenType.WATCH);
        userRightToken = getToken(TokenType.USER_RIGHTS);
        rollbackToken = getToken(TokenType.ROLLBACK);
        accountCreateToken = getToken(TokenType.CREATE_ACCOUNT);
    }

    /**
     * 检查并获取响应体。
     *
     * @param response   响应
     * @param actionName 所执行的操作名称
     * @return 响应体
     * @throws RuntimeException 响应体为空时抛出
     * @since 0.1.0
     */
    public static String checkAndGetBody(Response response, String actionName) throws IOException {
        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            throw new RuntimeException("Failed to " + actionName);
        }
        return responseBody.string();
    }

    /**
     * 获取令牌
     *
     * @param tokenType 令牌种类
     * @return 令牌
     * @since 1.0.1
     */
    public String getToken(TokenType tokenType) throws IOException {
        try (Response response = get(paramsMap("action", "query", "meta", "tokens", "type", tokenType.toString()))) {
            String body = checkAndGetBody(response, "obtain toke");
            String token = JsonUtil.checkAndGetAsString(body, true, "query", "tokens", tokenType + "token");
            if (token == null) {
                throw new RuntimeException("Failed to obtain token");
            }
            return token;
        }
    }

    /**
     * 获取令牌中的缓存。
     *
     * @param tokenType 令牌种类
     * @return 缓存中的令牌。当令牌种类为 {@link TokenType#LOGIN} 时始终返回 {@code null} ，因为该令牌不会被缓存
     * @since 1.0.1
     */
    public String getTokenCache(TokenType tokenType) {
        switch (tokenType) {
            case PATROL -> {
                return patrolToken;
            }
            case CSRF -> {
                return csrfToken;
            }
            case USER_RIGHTS -> {
                return userRightToken;
            }
            case WATCH -> {
                return watchToken;
            }
            case ROLLBACK -> {
                return rollbackToken;
            }
            case CREATE_ACCOUNT -> {
                return accountCreateToken;
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * 验证登录是否符合登录的类型。
     *
     * @return {@code true} 符合返回 {@code true}，否则返回 {@code false}
     * @since 0.1.0
     */
    public boolean assertLogged() throws IOException {
        try (Response response = get(paramsMap("action", "query", " meta", "userinfo", "assert", this.loginAssert.getValue()))) {
            String code = JsonUtil.checkAndGetAsString(checkAndGetBody(response, "assert"), true, "error", "code");
            if (code == null) {
                return true;
            } else if (code.equals("assert" + this.loginAssert.getValue() + "failed")) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取登录令牌
     *
     * @return 登录令牌
     * @since 0.1.0
     */
    public String getLoginToken() throws IOException {
        return getToken(TokenType.LOGIN);
    }

    /**
     * 获取CSRF令牌
     *
     * @return CSRF令牌
     * @since 0.1.0
     */
    public String getCSRFToken() throws IOException {
        return getToken(TokenType.CSRF);
    }

    /**
     * 获取巡查令牌
     *
     * @return 巡查令牌
     * @since 0.1.0
     */
    public String getPatrolToken() throws IOException {
        return getToken(TokenType.PATROL);
    }

    /**
     * 返回有关网站的一般信息。
     *
     * @param prop 要获取的信息
     * @return 有关网站一般信息的 {@code JsonObject}
     * @since 0.1.0
     */
    public JsonObject siteInfo(String... prop) {
        try (Response response = get(ActionTypes.SITE_INFO, paramsMap("siprop", ParamUtil.toListParam(prop)))) {
            String body = checkAndGetBody(response, "get site info");
            return GSON.fromJson(body, JsonObject.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get site info: " + e.getMessage(), e);
        }
    }

    /**
     * MediaWiki 支持的语言代码。
     *
     * @return MediaWiki 支持的语言代码集合
     * @since 0.1.0
     */
    public HashSet<String> supportedLanguages() {
        HashSet<String> supportedLanguages = new HashSet<>();
        JsonObject languages = siteInfo("languages");
        JsonArray languagesArray = JsonUtil.checkAndGetNonNullElement(languages, "query", "languages").getAsJsonArray();
        for (JsonElement language : languagesArray) {
            supportedLanguages.add(language.getAsJsonObject().get("code").getAsString());
        }
        return supportedLanguages;
    }

    /**
     * 检查是否拥有所有所需权限。
     *
     * @param rights        所拥有的权限
     * @param action        检查权限的操作，用于抛出异常
     * @param condition     检查权限的条件
     * @param requiredRight 所需要的权限
     * @throws InsufficientPermissionsException 缺少权限时抛出
     * @see PermissionChecker
     * @since 0.1.1
     */
    public void checkPermission(Set<String> rights, String action, boolean condition, UserRight... requiredRight) {
        checkPermission(rights, action, condition, true, requiredRight);
    }

    /**
     * 检查是否拥有所需权限。
     *
     * @param rights        所拥有的权限
     * @param action        检查权限的操作，用于抛出异常
     * @param condition     检查权限的条件
     * @param allNeeded     是否需要所有权限，为 {@code false} 时只需其中一项
     * @param requiredRight 所需要的权限
     * @throws InsufficientPermissionsException 缺少权限时抛出
     * @see PermissionChecker
     * @since 0.1.1
     */
    public void checkPermission(Set<String> rights, String action, boolean condition, boolean allNeeded, UserRight... requiredRight) {
        ArrayList<UserRight> missing = new ArrayList<>();
        for (UserRight right : requiredRight) {
            if (!rights.contains(right.toString())) {
                missing.add(right);
            } else {
                if (!allNeeded) {
                    return;
                }
            }
        }
        if (condition && !missing.isEmpty()) {
            throw new InsufficientPermissionsException(action, allNeeded, missing.toArray(new UserRight[0]));
        }
    }

    /**
     * 获取所拥有的权限并检查是否拥有所有所需权限。<p>
     * 需要多次检查权限时建议使用 {@link #getRightsName()} 配合 {@link #checkPermission(Set, String, boolean, boolean, UserRight...)} 。
     *
     * @param action        检查权限的操作，用于抛出异常
     * @param condition     检查权限的条件
     * @param allNeeded     是否需要所有权限，为 {@code false} 时只需其中一项
     * @param requiredRight 所需要的权限
     * @throws InsufficientPermissionsException 缺少权限时抛出
     * @see PermissionChecker
     * @since 0.1.1
     */
    public void checkPermission(String action, boolean condition, boolean allNeeded, UserRight... requiredRight) {
        checkPermission(getRightsName(), action, condition, allNeeded, requiredRight);
    }

    /**
     * 获取所拥有的权限并检查是否拥有所需权限。<p>
     * 需要多次检查权限时建议使用 {@link #getRightsName()} 配合 {@link #checkPermission(Set, String, boolean, UserRight...)} 。
     *
     * @param action        检查权限的操作，用于抛出异常
     * @param condition     检查权限的条件
     * @param requiredRight 所需要的权限
     * @throws InsufficientPermissionsException 缺少权限时抛出
     * @see PermissionChecker
     * @since 0.1.1
     */
    public void checkPermission(String action, boolean condition, UserRight... requiredRight) {
        checkPermission(action, condition, false, requiredRight);
    }

    /**
     * 获取所有权限名。
     *
     * @return 所拥有的所有权限名
     * @since 0.1.1
     */
    public Set<String> getRightsName() {
        Set<String> rights = new HashSet<>();
        for (JsonElement e : userInfo().get("rights").getAsJsonArray()) {
            rights.add(e.getAsString());
        }
        return rights;
    }

    private void login() throws IOException {
        if (this.loginAssert == Assert.BOT) {
            login(username, password);
        } else if (this.loginAssert == Assert.USER) {
            try {
                login(username, password);
            } catch (Exception e) {
                clientLogin(username, password);
            }
        }
    }

    private void login(String username, String password) throws IOException {
        try (Response response = post(ActionType.LOGIN, paramsMap("lgname", username, "lgpassword", password))) {
            String body = checkAndGetBody(response, "login");
            String result = JsonUtil.checkAndGetNonNullString(body, "login", "result");
            switch (result) {
                case "Success" -> refreshToken();
                case "Failed" -> throw new RuntimeException("Failed to login: Incorrect username or password entered");
                case "Aborted" ->
                        throw new RuntimeException("Failed to login: Authentication requires user interaction，please use the 'normal()' instead of the 'bot()' to login");
                default -> {
                    String reason = JsonUtil.checkAndGetAsString(body, true, "login", "reason");
                    throw new RuntimeException("Failed to login: " + (reason == null ? body : reason));
                }
            }
        }
    }

    private void clientLogin(String username, String password) throws IOException {
        try (Response response = post(ActionType.CLIENT_LOGIN, paramsMap("username", username, "password", password, "loginreturnurl", "https://example.com"))) {
            String body = checkAndGetBody(response, "login");
            String status = JsonUtil.checkAndGetNonNullString(body, "clientlogin", "status");
            String message = JsonUtil.checkAndGetNonNullString(body, "clientlogin", "message");
            String messageToThrow = message.replace('\n', ' ');
            String messageCode = JsonUtil.checkAndGetNonNullString(body, "clientlogin", "messagecode");
            Supplier<RuntimeException> defaultException = () -> new RuntimeException("Failed to login: " + messageToThrow + " (" + messageCode + ")");
            switch (status) {
                case "PASS" -> refreshToken();
                case "FAIL" -> {
                    switch (messageCode) {
                        case "login-throttled" ->
                                throw new RuntimeException("Failed to login: You have tried to log in too many times recently. Please wait 5 minutes before trying again.");
                        case "wrongpassword" ->
                                throw new RuntimeException("Failed to login: Incorrect username or password entered");
                        default -> throw defaultException.get();
                    }
                }
                case "UI" -> {
                    if (messageCode.equals("oathauth-auth-ui")) {
                        String tfaCode;
                        if (otpSupplier != null) {
                            tfaCode = otpSupplier.get();
                        } else {
                            try (Scanner scanner = new Scanner(System.in)) {
                                tfaCode = scanner.nextLine();
                            }
                        }
                        try (Response tfaResponse = post(ActionType.CLIENT_LOGIN, paramsMap("OATHToken", tfaCode, "logincontinue", "true"))) {
                            String tfaBody = checkAndGetBody(tfaResponse, "client-login");
                            String tfaStatus = JsonUtil.checkAndGetNonNullString(tfaBody, "clientlogin", "status");
                            if (tfaStatus.equals("PASS")) {
                                refreshToken();
                            } else {
                                throw new RuntimeException(
                                        "Failed to login: " +
                                                JsonUtil.checkAndGetNonNullString(tfaBody, "clientlogin", "message") +
                                                " (" + JsonUtil.checkAndGetNonNullString(tfaBody, "clientlogin", "messagecode") + ")");
                            }
                        }
                    } else {
                        throw defaultException.get();
                    }
                }
                default -> throw defaultException.get();
            }
        }
    }

    private boolean patrol(Integer recentChangeId, Integer revisionId) {
        Map<String, String> paramsMap = new HashMap<>();
        if (recentChangeId != null) {
            paramsMap.put("rcid", recentChangeId + "");
        } else {
            paramsMap.put("revid", revisionId + "");
        }
        try (Response response = post(ActionType.PATROL, paramsMap)) {
            String body = checkAndGetBody(response, "patrol");
            return GSON.fromJson(body, JsonObject.class).has("patrol");
        } catch (Exception e) {
            throw new RuntimeException("Failed to patrol: " + e.getMessage(), e);
        }
    }

    /**
     * 巡查页面或修订版本。
     *
     * @param revision 要巡查的修订版本
     * @return 巡查是否成功
     * @since 0.1.0
     */
    public boolean patrol(Revision revision) {
        return patrol(null, revision.id());
    }

    /**
     * 巡查页面或修订版本。
     *
     * @param recentChange 要巡查的最近更改
     * @return 巡查是否成功
     * @since 0.1.0
     */
    public boolean patrol(RecentChange recentChange) {
        return patrol(recentChange.id(), null);
    }

    /**
     * 巡查最近更改。
     *
     * @param id 要巡查的最近更改
     * @return 巡查是否成功
     * @since 0.1.1
     */
    public boolean patrolRecentChange(int id) {
        return patrol(id, null);
    }

    /**
     * 巡查修订版本。
     *
     * @param id 要巡查的修订版本
     * @return 巡查是否成功
     * @since 0.1.1
     */
    public boolean patrolRevision(int id) {
        return patrol(null, id);
    }

    /**
     * 封禁一位用户，需要 {@link UserRight#BLOCK} 权限。
     *
     * @param user                   要封禁的用户。通过 用户名、IP、IP范围和用户ID(例如“#12345”) 中任意一种方式指定的用户
     * @param expiry                 封禁的时长
     * @param reason                 封禁的理由，为 {@code null} 时为空
     * @param annoOnly               仅封禁匿名用户（即禁用此 IP 地址的匿名编辑，包括临时账号编辑）
     * @param noCreate               防止创建账号
     * @param allowUserTalk          允许用户编辑自己的讨论页
     * @param reblock                如果该用户已被封禁，则覆盖已有的封禁
     * @param watchUser              监视用户或该 IP 的用户页和讨论页
     * @param partial                封禁用户于特定页面或命名空间而不是整个站点
     * @param pageRestrictions       阻止用户编辑的标题列表。仅在 {@code partial} 设置为 {@code true} 时适用
     * @param nameSpacesRestrictions 阻止用户编辑的命名空间ID列表。仅在 {@code partial} 设置为 {@code true} 时适用。此参数支持使用 {@link NameSpace#ALL} 表示指定所有命名空间
     * @since 0.1.0
     */
    public void block(String user, Expiry expiry, String reason, boolean annoOnly, boolean noCreate,
                      boolean allowUserTalk, boolean reblock,
                      boolean watchUser, boolean partial, String[] pageRestrictions, NameSpace... nameSpacesRestrictions) {
        BlockRequest request = new BlockRequest(user, expiry, this);
        if (reason != null && !reason.isEmpty()) {
            request.reason(reason);
        }
        if (annoOnly) {
            request.anonOnly();
        }
        if (noCreate) {
            request.noCreate();
        }
        if (allowUserTalk) {
            request.allowUserTalk();
        }
        if (reblock) {
            request.reblock();
        }
        if (watchUser) {
            request.watchUser();
        }
        if (partial) {
            if ((pageRestrictions == null || pageRestrictions.length == 0) && (nameSpacesRestrictions == null || nameSpacesRestrictions.length == 0)) {
                request.nameSpaceRestriction(NameSpace.ALL);
            } else {
                if (!(pageRestrictions == null || pageRestrictions.length == 0)) {
                    request.pageRestriction(pageRestrictions);
                }
                if (!(nameSpacesRestrictions == null || nameSpacesRestrictions.length == 0)) {
                    request.nameSpaceRestriction(nameSpacesRestrictions);
                }
            }
        }
        request.execute().parse();
    }

    /**
     * 解封一位用户。
     *
     * @param user      要解封的用户。通过 用户名、IP、IP范围和用户ID(例如“#12345”) 中任意一种方式指定的用户
     * @param reason    解封的理由，为 {@code null} 时为空
     * @param watchUser 监视用户或IP地址的用户页和讨论页
     * @since 0.1.0
     */
    public void unblock(String user, String reason, boolean watchUser) {
        UnblockRequest request = new UnblockRequest(user, this);
        if (reason != null && !reason.isEmpty()) {
            request.reason(reason);
        }
        if (watchUser) {
            request.watchUser();
        }
        request.execute().parse();
    }

    /**
     * 获取第一个修订版本到第二个修订版本之间的差异。<br>
     * 该方法接受来自两个页面的标题或修订版本 ID并返回差异结果。每组中的变量（标题组和修订 ID 组）必须有且仅有一个有效的变量。
     * 如果不满足这一条件，将抛出 {@link IllegalArgumentException} 异常。
     *
     * @param fromTitle      要比较的第一个标题
     * @param toTitle        要比较的第二个标题
     * @param fromRevisionId 要比较的第一个修订版本
     * @param toRevisionId   要比较的第二个修订版本
     * @param diffType       返回格式化为行内HTML的比较结果，为 {@code null} 时默认为 {@link DiffType#TABLE}
     * @return 两修订版本之间的差异
     * @throws IllegalArgumentException 如果每组中的有效变量不符合条件（每组有且仅有一个有效变量），抛出此异常
     * @since 0.1.2
     */
    public DifferentComparison compare(String fromTitle, String toTitle, Integer fromRevisionId, Integer toRevisionId, DiffType diffType) {
        Map<String, String> params = new HashMap<>();
        if (!exactlyOneValid(fromTitle, fromRevisionId) || !exactlyOneValid(toTitle, toRevisionId)) {
            throw new IllegalArgumentException("There must be only one valid variable in each group.");
        }
        if (isValid(fromTitle)) {
            params.put("fromtitle", fromTitle);
        }
        if (isValid(toTitle)) {
            params.put("totitle", toTitle);
        }
        if (isValid(fromRevisionId)) {
            params.put("fromrev", fromRevisionId + "");
        }
        if (isValid(toRevisionId)) {
            params.put("torev", toRevisionId + "");
        }
        if (diffType != null) {
            params.put("difftype", diffType.toString());
        }
        try (Response response = post(ActionType.COMPARE, params)) {
            String body = checkAndGetBody(response, "compare pages");
            return DifferentComparison.fromJson(JsonUtil.checkAndGetNonNullElement(body, "compare").getAsJsonObject());
        } catch (Exception e) {
            throw new RuntimeException("Failed to compare pages: " + e.getMessage(), e);
        }
    }

    private static boolean exactlyOneValid(String a, Integer b) {
        return isValid(a) ^ isValid(b);
    }

    private static boolean isValid(String value) {
        return value != null && !value.isEmpty();
    }

    private static boolean isValid(Integer value) {
        return value != null && value > 0;
    }

    /**
     * 获取滥用过滤器。
     *
     * @param id 过滤器的ID
     * @return 过滤器
     * @since 0.1.0
     */
    public AbuseFilter abuseFilter(int id) {
        return allAbuseFilters(id, null).get(id);
    }

    /**
     * 获取滥用过滤器。
     *
     * @param startId 枚举的起始过滤器ID，为 {@code null} 时表示从头开始
     * @param endId   枚举的结束过滤器ID，为 {@code null} 时表示获取所有过滤器
     * @return 包含所有滥用过滤器的 {@link HashMap}，键为过滤器ID，值为 {@link AbuseFilter}
     * @since 0.1.0
     */
    public HashMap<Integer, AbuseFilter> allAbuseFilters(Integer startId, Integer endId) {
        QueryRequest queryRequest = new QueryRequest(this, "list all abuse filters");
        AbuseFiltersListModule abuseFilters = new AbuseFiltersListModule(queryRequest);
        if (startId != null) {
            abuseFilters.startId(startId);
        }
        if (endId != null) {
            abuseFilters.endId(endId);
        }
        queryRequest.addSubmodule(abuseFilters);
        return abuseFilters.getResult();
    }

    private boolean edit(String title, int pageId, String text, String summary, boolean isMinor, boolean createOnly) {
        Map<String, String> paramsMap;
        if (title == null) {
            paramsMap = paramsMap("pageid", pageId, "text", text, "summary", summary);
        } else {
            paramsMap = paramsMap("title", title, "text", text, "summary", summary);
        }
        if (createOnly) {
            paramsMap.put("createonly", "true");
        }
        if (isMinor) {
            paramsMap.put("minor", "true");
        }
        if (loginAssert == Assert.BOT) {
            paramsMap.put("bot", "true");
        }
        try (Response response = post(ActionType.EDIT, paramsMap)) {
            String body = checkAndGetBody(response, "edit");
            String errorCode = JsonUtil.checkAndGetAsString(body, "error", "code");
            String errorInfo = JsonUtil.checkAndGetAsString(body, "error", "info");
            if (errorCode != null) {
                switch (errorCode) {
                    case "abusefilter-disallowed" -> {
                        AbuseFilter abuseFilter = AbuseFilter.fromJson(JsonUtil.checkAndGetNonNullString(body, "error", "abusefilter"));
                        throw new RuntimeException("Failed to edit: Triggered AbuseFilter#" + abuseFilter.id() + ". " + "Actions taken: " +
                                Arrays.toString(abuseFilter.actions()) + ", description: " + abuseFilter.description());
                    }
                    case "protectedpage" ->
                            throw new RuntimeException("Failed to edit: This page has been protected to prevent editing or other actions");
                    case "editconflict" -> throw new RuntimeException("Failed to edit: Edit conflict");
                    case "articleexists" -> {
                        if (createOnly) {
                            return false;
                        }
                        throw new RuntimeException("Failed to edit: Create conflict");
                    }
                    default -> throw new RuntimeException("Failed to edit: " + errorInfo + " (" + errorCode + ")");
                }
            }
            String result = JsonUtil.checkAndGetNonNullString(body, "edit", "result");

            return result.equals("Success");
        } catch (Exception e) {
            throw new RuntimeException("Failed to edit: " + e.getMessage(), e);
        }
    }

    /**
     * 创建和编辑页面。
     *
     * @param title      要编辑的页面标题
     * @param text       页面内容
     * @param summary    编辑摘要
     * @param isMinor    将此次编辑标记为小编辑
     * @param createOnly 如果页面已经存在，请勿编辑
     * @return 编辑是否成功
     * @since 0.1.0
     */
    public boolean edit(String title, String text, String summary, boolean isMinor, boolean createOnly) {
        return edit(title, -1, text, summary, isMinor, createOnly);
    }

    /**
     * 创建和编辑页面。
     *
     * @param title   要编辑的页面标题
     * @param text    页面内容
     * @param summary 编辑摘要
     * @return 编辑是否成功
     * @since 0.1.0
     */
    public boolean edit(String title, String text, String summary) {
        return edit(title, text, summary, false, false);
    }

    /**
     * 创建和编辑页面。
     *
     * @param pageId     要编辑的页面的页面ID
     * @param text       页面内容
     * @param summary    编辑摘要
     * @param isMinor    将此次编辑标记为小编辑
     * @param createOnly 如果页面已经存在，请勿编辑
     * @return 编辑是否成功
     * @since 0.1.0
     */
    public boolean edit(int pageId, String text, String summary, boolean isMinor, boolean createOnly) {
        return edit(null, pageId, text, summary, isMinor, createOnly);
    }

    /**
     * 创建和编辑页面。
     *
     * @param page       要编辑的页面的页面对象
     * @param text       页面内容
     * @param summary    编辑摘要
     * @param isMinor    将此次编辑标记为小编辑
     * @param createOnly 如果页面已经存在，请勿编辑
     * @return 编辑是否成功
     * @since 0.1.0
     */
    public boolean edit(Page page, String text, String summary, boolean isMinor, boolean createOnly) {
        return edit(page.title(), page.pageId(), text, summary, isMinor, createOnly);
    }

    /**
     * 创建和编辑页面。
     *
     * @param page    要编辑的页面的页面对象
     * @param text    页面内容
     * @param summary 编辑摘要
     * @return 编辑是否成功
     * @since 0.1.0
     */
    public boolean edit(Page page, String text, String summary) {
        return edit(page.title(), page.pageId(), text, summary, false, false);
    }

    /**
     * 循序列举在指定多个命名空间中的所有页面。
     *
     * @param pagePrefix     搜索所有以此值开头的页面标题，为 {@code null} 时忽略
     * @param filterRedirect 要列出哪些页面，为 {@code null} 时为 {@link FilterRedirect#ALL}
     * @param nameSpaces     要列举的多个命名空间
     * @return 指定多个命名空间中的所有页面
     * @since 0.1.0
     */
    public HashSet<Page> allPages(String pagePrefix, FilterRedirect filterRedirect, NameSpace... nameSpaces) {
        HashSet<Page> pages = new HashSet<>();
        for (NameSpace nameSpace : nameSpaces) {
            pages.addAll(allPages(pagePrefix, filterRedirect, nameSpace));
        }
        return pages;
    }

    /**
     * 循序列举在指定多个命名空间中的所有页面标题。
     *
     * @param pagePrefix     搜索所有以此值开头的页面标题，为 {@code null} 时忽略
     * @param filterRedirect 要列出哪些页面，为 {@code null} 时为 {@link FilterRedirect#ALL}
     * @param nameSpaces     要列举的多个命名空间
     * @return 指定多个命名空间中的所有页面标题
     * @since 0.1.0
     */
    public HashSet<String> allPageTitles(String pagePrefix, FilterRedirect filterRedirect, NameSpace... nameSpaces) {
        HashSet<String> pages = new HashSet<>();
        for (NameSpace nameSpace : nameSpaces) {
            for (Page page : allPages(pagePrefix, filterRedirect, nameSpace)) {
                pages.add(page.title());
            }
        }
        return pages;
    }

    /**
     * 循序列举在指定命名空间中的所有页面。
     *
     * @param pagePrefix     搜索所有以此值开头的页面标题，为 {@code null} 时忽略
     * @param filterRedirect 要列出哪些页面，为 {@code null} 时为 {@link FilterRedirect#ALL}
     * @param nameSpace      要列举的命名空间
     * @return 指定命名空间中的所有页面
     * @since 0.1.0
     */
    public HashSet<Page> allPages(String pagePrefix, FilterRedirect filterRedirect, NameSpace nameSpace) {
        QueryRequest queryRequest = new QueryRequest(this, "list all pages");
        AllPagesListModule allPages = new AllPagesListModule(queryRequest);
        if (pagePrefix != null) {
            allPages.prefix(pagePrefix);
        }
        if (filterRedirect != null) {
            allPages.filterRedirect(filterRedirect);
        }
        if (nameSpace != null) {
            allPages.nameSpace(nameSpace);
        }
        queryRequest.addSubmodule(allPages);
        queryRequest.execute().parse();
        return allPages.getResult();
    }

    /**
     * 列举所有注册用户。
     *
     * @param editedOnly 只列出有编辑的用户
     * @param groups     只包含指定组中的用户
     * @return 所有注册用户
     * @since 0.1.0
     */
    public UserSet allUsers(boolean editedOnly, UserGroup... groups) {
        QueryRequest queryRequest = new QueryRequest(this, "list all users");
        AllUsersListModule allUsers = new AllUsersListModule(queryRequest);
        if (editedOnly) {
            allUsers.withEditsOnly();
        }
        allUsers.groups(groups);
        queryRequest.addSubmodule(allUsers);
        queryRequest.execute().parse();
        return allUsers.getResult();
    }

    /**
     * 列举所有修订。
     *
     * @param user       只列出此用户做出的修订
     * @param start      枚举的起始{@link Timestamp 时间戳}，为 {@code null} 时忽略
     * @param end        结束枚举的时间戳，为 {@code null} 时忽略
     * @param nameSpaces 只列出此命名空间的页面，此参数支持使用 {@link NameSpace#ALL} 表示指定所有命名空间
     * @return 所有修订
     * @since 0.1.0
     */
    public HashSet<Revision> allRevisions(String user, Timestamp start, Timestamp end, NameSpace... nameSpaces) {
        HashSet<Revision> revisions = new HashSet<>();
        Map<String, String> continueParam = new HashMap<>();
        Map<String, String> baseParam = paramsMap("arvlimit", "max");
        if (start != null) {
            baseParam.put("arvstart", start.toString());
        }
        if (end != null) {
            baseParam.put("arvend", end.toString());
        }
        if (user != null) {
            baseParam.put("arvuser", user);
        }
        if (nameSpaces != null && nameSpaces.length != 0) {
            baseParam.put("arvnamespace", NameSpace.toApiParam(true, nameSpaces));
        }
        continuableAction(ActionTypes.ALL_REVISIONS, baseParam, "get all revisions", (jsonObject -> {
            JsonArray revisionsJson = JsonUtil.checkAndGetElement(jsonObject, "query", "allrevisions").getAsJsonArray();
            for (JsonElement page : revisionsJson) {
                for (JsonElement revision : page.getAsJsonObject().get("revisions").getAsJsonArray()) {
                    revisions.add(Revision.fromJson(revision.getAsJsonObject()));
                }
            }
        }));
        return revisions;
    }

    /**
     * 列举所有命名空间中的最近更改。
     *
     * @param start 枚举的起始{@link Timestamp 时间戳}，为 {@code null} 时忽略
     * @param end   结束枚举的时间戳，为 {@code null} 时忽略
     * @param show  只显示满足这些标准的项目
     * @return 列举的最近更改
     * @since 0.1.1
     */
    public HashSet<RecentChange> recentChanges(Timestamp start, Timestamp end, RevisionType... show) {
        return recentChanges(start, end, show, NameSpace.ALL);
    }

    /**
     * 列举最近更改。
     *
     * @param start      枚举的起始{@link Timestamp 时间戳}，为 {@code null} 时忽略
     * @param end        结束枚举的时间戳，为 {@code null} 时忽略
     * @param show       只显示满足这些标准的项目
     * @param nameSpaces 只列出此命名空间的页面，此参数支持使用 {@link NameSpace#ALL} 表示指定所有命名空间
     * @return 列举的最近更改
     * @since 0.1.0
     */
    public HashSet<RecentChange> recentChanges(Timestamp start, Timestamp end, RevisionType[] show, NameSpace... nameSpaces) {
        HashSet<RecentChange> recentChanges = new HashSet<>();
        Map<String, String> baseParam = paramsMap("rclimit", "max", "rcprop", "user|comment|timestamp|title|ids");
        if (show != null && show.length > 0) {
            StringBuilder rcshowBuilder = new StringBuilder();
            for (int i = 0; i < show.length; i++) {
                RevisionType type = show[i];
                if (type == RevisionType.NOT_PATROLLED || type == RevisionType.PATROLLED || type == RevisionType.UNPATROLLED) {
                    checkPermission("get recent changes", true, false, UserRight.PATROLMARKS, UserRight.PATROL);
                }
                rcshowBuilder.append(type.getValue());
                if (i != show.length - 1) {
                    rcshowBuilder.append("|");
                }
            }
            baseParam.put("rcshow", rcshowBuilder.toString());
        }
        if (start != null) {
            baseParam.put("rcstart", start.toString());
        }
        if (end != null) {
            baseParam.put("rcend", end.toString());
        }
        if (nameSpaces != null && nameSpaces.length != 0) {
            baseParam.put("rcnamespace", NameSpace.toApiParam(true, nameSpaces));
        }
        continuableAction(ActionTypes.RECENT_CHANGES, baseParam, "get recent changes", (jsonObject -> {
            JsonArray rcsJson = JsonUtil.checkAndGetNonNullElement(jsonObject, "query", "recentchanges").getAsJsonArray();
            for (JsonElement rcJson : rcsJson) {
                recentChanges.add(RecentChange.fromJson(rcJson.getAsJsonObject()));
            }
        }));
        return recentChanges;
    }

    /**
     * 获取页面内容。
     *
     * @param pageTitle 页面标题
     * @return 页面内容，页面不存在时返回空字符串
     * @since 0.1.0
     */
    public String getPageText(String pageTitle) {
        try (Response response = get(ActionTypes.REVISIONS, paramsMap("rvlimit", "max", "rvprop", "content", "titles", pageTitle))) {
            String body = checkAndGetBody(response, "get revisions");
            JsonObject bodyJson = GSON.fromJson(body, JsonObject.class);
            JsonObject page = JsonUtil.checkAndGetElement(bodyJson, "query", "pages").getAsJsonObject().asMap().entrySet().iterator().next().getValue().getAsJsonObject();
            if (page.has("missing")) {
                return "";
            }
            JsonArray pageRevisions = page.get("revisions").getAsJsonArray();
            return pageRevisions.get(0).getAsJsonObject().get("*").getAsString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get page text: " + e.getMessage(), e);
        }
    }

    /**
     * 查找所有链接至指定页面的页面。
     *
     * @param pageTitle      链接被指定的页面的标题
     * @param filterRedirect 是否包含重定向
     * @param nameSpaces     只包括这些命名空间的页面，此参数支持使用 {@link NameSpace#ALL} 表示指定所有命名空间
     * @return 所有链接至指定页面的页面
     * @since 0.1.0
     */
    public HashSet<String> linksHere(String pageTitle, FilterRedirect filterRedirect, NameSpace... nameSpaces) {
        HashSet<String> pages = new HashSet<>();
        Map<String, String> baseParam = paramsMap("lhlimit", "max", "titles", pageTitle, "lhprop", "title|pageid");
        if (filterRedirect == FilterRedirect.NON_REDIRECTS) {
            baseParam.put("lhshow", "!redirect");
        } else if (filterRedirect == FilterRedirect.REDIRECTS) {
            baseParam.put("lhshow", "redirect");
        }
        if (nameSpaces != null && nameSpaces.length != 0) {
            baseParam.put("lhnamespace", NameSpace.toApiParam(true, nameSpaces));
        }
        continuableAction(ActionTypes.LINKS_HERE, baseParam, "get pages link here", (jsonObject -> {
            JsonObject pageJson = JsonUtil.checkAndGetElement(jsonObject, "query", "pages").getAsJsonObject();
            JsonArray linkHerePages = JsonUtil.checkAndGetNonNullElement(pageJson.asMap().entrySet().iterator().next().getValue().getAsJsonObject(), "linkshere").getAsJsonArray();
            for (JsonElement linkHerePage : linkHerePages) {
                pages.add(linkHerePage.getAsJsonObject().get("title").getAsString());
            }
        }));
        return pages;
    }

    /**
     * 显示滥用过滤器的捕获事件。
     *
     * @param logId   显示指定日志ID的记录，为 {@code null} 时忽略
     * @param user    只显示由指定的用户或IP地址完成的记录，为 {@code null} 时忽略
     * @param title   只显示在指定页面上发生过的条项，为 {@code null} 时忽略
     * @param start   枚举的起始{@link Timestamp 时间戳}，为 {@code null} 时忽略
     * @param end     结束枚举的时间戳，为 {@code null} 时忽略
     * @param filters 只显示被指定过滤器捕获过的记录，为 {@code null} 时忽略
     * @since 0.1.0
     */
    public HashSet<AbuseFilterLogEntry> abuseLogs(Integer logId, String user, String title, Timestamp start, Timestamp end, AbuseFilter... filters) {
        Set<String> rights = getRightsName();
        checkPermission(rights, "get abuse logs", true, UserRight.ABUSEFILTER_LOG);
        checkPermission(rights, "get private abuse logs", AbuseFilter.hasPrivateFilter(filters), UserRight.ABUSEFILTER_LOG_PRIVATE);
        checkPermission(rights, "get details of abuse logs", true, UserRight.ABUSEFILTER_LOG_DETAIL);
        HashSet<AbuseFilterLogEntry> result = new HashSet<>();
        HashMap<String, String> baseParam = paramsMap("afllimit", "max", "aflprop", "action|details|filter|hidden|ids|result|revid|timestamp|title|user");
        if (logId != null) {
            baseParam.put("afllogid", logId.toString());
        }
        if (user != null && !user.isEmpty()) {
            baseParam.put("afluser", user);
        }
        if (title != null && !title.isEmpty()) {
            baseParam.put("afltitle", title);
        }
        if (start != null) {
            baseParam.put("aflstart", start.toString());
        }
        if (end != null) {
            baseParam.put("aflend", end.toString());
        }
        HashSet<AbuseFilter> filtersSet = new HashSet<>();
        if (filters.length > 0) {
            filtersSet.addAll(Arrays.asList(filters));
        }
        List<HashSet<AbuseFilter>> splitFilters = CollectionUtil.split(filtersSet, apiLimit(), true);
        for (HashSet<AbuseFilter> set : splitFilters) {
            HashMap<String, String> requestParam = new HashMap<>(baseParam);
            if (!set.isEmpty()) {
                requestParam.put("aflfilter", ParamUtil.toListParam(set));
            }
            continuableAction(ActionTypes.ABUSE_LOG, requestParam, "get abuse logs", (s) -> {
                JsonArray array = JsonUtil.checkAndGetElement(s, "query", "abuselog").getAsJsonArray();
                for (JsonElement obj : array) {
                    result.add(AbuseFilterLogEntry.fromJson(obj.getAsJsonObject()));
                }
            });
        }
        return result;
    }

    /**
     * 显示所有滥用过滤器简化的的捕获事件。
     *
     * @param start 枚举的起始{@link Timestamp 时间戳}，为 {@code null} 时忽略
     * @param end   结束枚举的时间戳，为 {@code null} 时忽略
     * @return 过滤器简化的的捕获事件
     */
    public HashSet<SimplifiedAbuseFilterLogEntry> simplifiedAbuseLogs(Timestamp start, Timestamp end) {
        Set<String> rights = getRightsName();
        checkPermission(rights, "get abuse logs", true, UserRight.ABUSEFILTER_LOG);
        HashSet<SimplifiedAbuseFilterLogEntry> result = new HashSet<>();
        HashMap<String, String> baseParam = paramsMap("afllimit", "max", "aflprop", "user|title|action|result|filter|timestamp");
        if (start != null) {
            baseParam.put("aflstart", start.toString());
        }
        if (end != null) {
            baseParam.put("aflend", end.toString());
        }
        continuableAction(ActionTypes.ABUSE_LOG, baseParam, "get abuse logs", (s) -> {
            JsonArray array = JsonUtil.checkAndGetElement(s, "query", "abuselog").getAsJsonArray();
            for (JsonElement obj : array) {
                result.add(SimplifiedAbuseFilterLogEntry.fromJson(obj.getAsJsonObject()));
            }
        });
        return result;
    }

    /**
     * 获取有关列出用户的信息。
     *
     * @param users      要获取信息的用户列表
     * @param properties 要包含的信息束
     * @return 用户详细信息
     * @since 0.1.0
     */
    public HashMap<String, JsonObject> usersMeta(String[] users, UserProperty... properties) {
        HashMap<String, JsonObject> usersMeta = new HashMap<>();
        if (users == null || users.length == 0) {
            return usersMeta;
        }
        String propParam = null;
        if (properties != null && properties.length > 0) {
            propParam = ParamUtil.toListParam(properties);
        }
        int maxUserSize = apiLimit();
        HashSet<String> userSet = new HashSet<>();
        Collections.addAll(userSet, users);
        List<HashSet<String>> splitUsers = CollectionUtil.split(userSet, maxUserSize);
        for (HashSet<String> user : splitUsers) {
            String usersIdentifier = ParamUtil.toListParam(user);
            Map<String, String> paramsMap = paramsMap("ususers", usersIdentifier);
            if (propParam != null) {
                paramsMap.put("usprop", propParam);
            }
            try (Response response = get(ActionTypes.USERS, paramsMap)) {
                String body = checkAndGetBody(response, "get users meta");
                JsonUtil.checkAndGetNonNullElement(body, "query", "users").getAsJsonArray().forEach(
                        e -> {
                            JsonObject json = e.getAsJsonObject();
                            String name = json.get("name").getAsString();
                            if (json.has("missing")) {
                                LOGGER.warn("There is no user named '{}'. Please check your spelling.", name);
                                usersMeta.put(name, null);
                            } else {
                                usersMeta.put(name, json);
                            }
                        }
                );
            } catch (IOException e) {
                throw new RuntimeException("Failed to get users meta: " + e.getMessage(), e);
            }
        }
        return usersMeta;
    }

    /**
     * 获取页面内容。
     *
     * @param page 页面
     * @return 页面内容，页面不存在时返回空字符串
     * @since 0.1.0
     */
    public String getPageText(Page page) {
        return getPageText(page.title());
    }

    /**
     * 从日志获取事件。
     *
     * @param type     过滤日志记录至仅限此类型，为 {@code null} 时忽略
     * @param executor 过滤记录为这些由指定用户做出的，为 {@code null} 时忽略。通过 用户名、IP、跨wiki用户名（例如“前缀>示例用户”）和用户ID(例如“#12345”) 中任意一种方式指定的用户
     * @param start    枚举的起始{@link Timestamp 时间戳}，为 {@code null} 时忽略
     * @param end      结束枚举的时间戳，为 {@code null} 时忽略
     * @return 日志条目
     * @since 0.1.0
     */
    public HashSet<LogEntry> logEvents(LogType type, String executor, Timestamp start, Timestamp end) {
        HashSet<LogEntry> entries = new HashSet<>();
        Map<String, String> baseParams = paramsMap("leprop", "ids|title|type|user|userid|timestamp|comment|parsedcomment|details|tags", "lelimit", "max");
        if (type != null) {
            baseParams.put("letype", type.toString());
        }
        if (executor != null) {
            baseParams.put("leuser", executor);
        }
        if (start != null) {
            baseParams.put("lestart", start.toString());
        }
        if (end != null) {
            baseParams.put("leend", end.toString());
        }
        continuableAction(ActionTypes.LOG_EVENT, baseParams, "get log events", jsonObject -> {
            JsonArray eventsJson = JsonUtil.checkAndGetNonNullElement(jsonObject, "query", "logevents").getAsJsonArray();
            for (JsonElement event : eventsJson) {
                entries.add(LogEntry.fromJson(event.getAsJsonObject()));
            }
        });
        return entries;
    }

    /**
     * 获取有关当前用户的信息。
     *
     * @return 当前用户的详细信息
     * @since 0.1.0
     */
    public JsonObject userInfo() {
        try (Response response = get(ActionTypes.USER_INFO, paramsMap("uiprop", "groups|rights|editcount|ratelimits"))) {
            String body = checkAndGetBody(response, "get user info");
            return JsonUtil.checkAndGetNonNullElement(body, "query", "userinfo").getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get user info: " + e.getMessage(), e);
        }
    }

    /**
     * 获取有关当前用户的信息。
     *
     * @param properties 要包含的信息束
     * @return 当前用户的详细信息
     * @since 0.1.0
     */
    public JsonObject userInfo(String... properties) {
        if (properties == null || properties.length == 0) {
            return userInfo();
        } else {
            String propParam = ParamUtil.toListParam(properties);
            try (Response response = get(ActionTypes.USER_INFO, paramsMap("uiprop", propParam))) {
                String body = checkAndGetBody(response, "get user info");
                return JsonUtil.checkAndGetNonNullElement(body, "query", "userinfo").getAsJsonObject();
            } catch (IOException e) {
                throw new RuntimeException("Failed to get user info: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 当前用户是否拥有某项 {@link UserRight 权限} 。
     *
     * @param right 要检查的权限
     * @return 用户拥有这项权限时返回 {@code true} ，否则返回 {@code false}
     * @since 0.1.0
     */
    @Override
    public boolean hasRight(UserRight right) {
        JsonObject userinfo = userInfo();
        for (JsonElement e : userinfo.get("rights").getAsJsonArray()) {
            if (e.getAsString().equals(right.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当前用户是否隶属于某个 {@link UserGroup 群组} 。
     *
     * @param group 要检查的群组
     * @return 用户隶属于这个群组时返回 {@code true} ，否则返回 {@code false}
     * @since 0.1.0
     */
    @Override
    public boolean inGroup(UserGroup group) {
        JsonObject userinfo = userInfo();
        for (JsonElement e : userinfo.get("groups").getAsJsonArray()) {
            if (e.getAsString().equals(group.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 列举当前用户隶属的所有 {@link UserGroup 群组} 。
     *
     * @return 当前用户隶属的所有群组
     * @since 0.1.0
     */
    @Override
    public HashSet<UserGroup> getGroups() {
        JsonObject userinfo = userInfo();
        HashSet<UserGroup> groups = new HashSet<>();
        for (JsonElement e : userinfo.get("groups").getAsJsonArray()) {
            if (e.getAsString().equals("*")) {
                continue;
            }
            groups.add(UserGroup.toUserGroup(e.getAsString()));
        }
        return groups;
    }

    /**
     * 列举当前用户拥有的所有 {@link UserRight 权限} 。
     *
     * @return 当前用户拥有的所有权限
     * @since 0.1.0
     */
    @Override
    public HashSet<UserRight> getRights() {
        JsonObject userinfo = userInfo();
        HashSet<UserRight> rights = new HashSet<>();
        for (JsonElement e : userinfo.get("rights").getAsJsonArray()) {
            rights.add(UserRight.toUserRight(e.getAsString()));
        }
        return rights;
    }

    /**
     * 获取api调用限制。
     *
     * @param normal 一般客户端的限制
     * @param higher 允许更高上限的客户端的限制
     * @return api调用限制
     * @since 0.1.0
     */
    public int apiLimit(int normal, int higher) {
        return hasRight(UserRight.APIHIGHLIMITS) ? higher : normal;
    }

    /**
     * 获取api调用限制。
     *
     * @return api调用限制。一般客户端的限制为 {@code 50} ，允许更高上限的客户端的限制为 {@code 500}
     * @since 0.1.0
     */
    public int apiLimit() {
        return apiLimit(normalApiLimit, higherApiLimit);
    }

    /**
     * 设置一般客户端api调用限制。
     *
     * @param normalApiLimit 新的一般客户端api调用限制
     * @since 0.1.0
     */
    public void setNormalApiLimit(int normalApiLimit) {
        if (normalApiLimit <= 0) {
            throw new IllegalArgumentException("normalApiLimit must be greater than 0");
        }
        if (normalApiLimit > higherApiLimit) {
            throw new IllegalArgumentException("normalApiLimit must be less than or equal to higherApiLimit");
        }
        this.normalApiLimit = normalApiLimit;
    }

    /**
     * 设置允许更高上限的客户端api调用限制。
     *
     * @param higherApiLimit 新的允许更高上限的客户端api调用限制
     * @since 0.1.0
     */
    public void setHigherApiLimit(int higherApiLimit) {
        if (higherApiLimit < normalApiLimit) {
            throw new IllegalArgumentException("higherApiLimit must be greater than or equal to normalApiLimit");
        }
        this.higherApiLimit = higherApiLimit;
    }
}