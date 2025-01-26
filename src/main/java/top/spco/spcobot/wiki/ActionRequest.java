package top.spco.spcobot.wiki;

import okhttp3.*;
import top.spco.spcobot.wiki.action.ActionType;
import top.spco.spcobot.wiki.action.PermissionChecker;
import top.spco.spcobot.wiki.action.PermissionRule;
import top.spco.spcobot.wiki.action.parameter.TokenType;
import top.spco.spcobot.wiki.user.UserRight;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import static top.spco.spcobot.wiki.util.MapUtil.paramsMap;

/**
 * @author SpCo
 * @version 1.0.1
 * @since 1.0.1
 */
public abstract class ActionRequest<T extends ActionResponse<?, ?>> {
    public final Wiki wiki;
    public final RequestMethod method;
    public final String actionDescription;
    protected final HashMap<String, String> queryParams = paramsMap("format", "json");
    protected final HashMap<String, String> formParams = new HashMap<>();
    private final PermissionRule requiredPermissions = new PermissionRule(PermissionRule.RuleType.ALL, new HashSet<>());
    private final HashSet<PermissionRule> requiredMultipleChoicePermissions = new HashSet<>();

    /**
     * @since 1.0.1
     */
    protected ActionRequest(Wiki wiki, RequestMethod method, ActionType action, String actionDescription) {
        this.wiki = Objects.requireNonNull(wiki, "Wiki cannot be null");
        this.method = Objects.requireNonNull(method, "RequestMethod cannot be null");
        this.actionDescription = Objects.requireNonNull(actionDescription, "Action description cannot be null");
        try {
            applyActionType(action);
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply action type: " + e.getMessage(), e);
        }
    }

    protected void applyActionType(ActionType action) throws IOException {
        if (action != null) {
            addQueryParameter(action.getBaseParams());
            if (action.needToken()) {
                switch (action.getTokenType()) {
                    case PATROL ->
                            addFormParameter(action.getTokenParameterName(), wiki.getTokenCache(TokenType.PATROL));
                    case CSRF -> addFormParameter(action.getTokenParameterName(), wiki.getTokenCache(TokenType.CSRF));
                    case LOGIN -> addFormParameter(action.getTokenParameterName(), wiki.getLoginToken());
                    case USER_RIGHTS ->
                            addFormParameter(action.getTokenParameterName(), wiki.getTokenCache(TokenType.USER_RIGHTS));
                    case WATCH -> addFormParameter(action.getTokenParameterName(), wiki.getTokenCache(TokenType.WATCH));
                    case ROLLBACK ->
                            addFormParameter(action.getTokenParameterName(), wiki.getTokenCache(TokenType.ROLLBACK));
                    case CREATE_ACCOUNT ->
                            addFormParameter(action.getTokenParameterName(), wiki.getTokenCache(TokenType.CREATE_ACCOUNT));
                }
            }
        }
    }

    /**
     * @since 1.0.1
     */
    protected Request buildRequest() {
        return buildRequest(null, null);
    }

    /**
     * @since 1.0.1
     */
    protected Request buildRequest(Map<String, String> additionalQueryParameters, Map<String, String> additionalFormParameters) {
        HttpUrl.Builder urlBuilder = wiki.getActionApi().newBuilder();
        queryParams.forEach(urlBuilder::addQueryParameter);
        if (additionalQueryParameters != null) {
            additionalQueryParameters.forEach(urlBuilder::addQueryParameter);
        }
        Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.build());
        switch (method) {
            case GET -> requestBuilder.get();
            case POST -> {
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                formParams.forEach(formBodyBuilder::add);
                if (additionalFormParameters != null) {
                    additionalFormParameters.forEach(formBodyBuilder::add);
                }
                requestBuilder.post(formBodyBuilder.build());
            }
        }
        return requestBuilder.build();
    }

    /**
     * @since 1.0.1
     */
    public void requiredPermission(UserRight... rights) {
        for (UserRight right : rights) {
            requiredPermissions.add(right);
        }
    }

    /**
     * @since 1.0.1
     */
    public void requiredPermission(PermissionRule rule) {
        if (rule.ruleType() == PermissionRule.RuleType.ALL) {
            for (UserRight right : rule.requiredPermissions()) {
                requiredPermissions.add(right);
            }
            return;
        }
        requiredMultipleChoicePermissions.add(rule);
    }

    /**
     * @since 1.0.1
     */
    protected void checkPermission() {
        PermissionChecker checker = createPermissionChecker(requiredPermissions);
        for (PermissionRule rule : requiredMultipleChoicePermissions) {
            checker.addRule(rule);
        }
        checker.passOrThrow();
    }

    /**
     * @since 1.0.1
     */
    public ActionRequest<T> addQueryParameter(String key, String value) {
        queryParams.put(key, value);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public ActionRequest<T> addQueryParameter(Map<String, String> params) {
        queryParams.putAll(params);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public ActionRequest<T> addFormParameter(String key, String value) {
        formParams.put(key, value);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public ActionRequest<T> addFormParameter(Map<String, String> params) {
        formParams.putAll(params);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public PermissionChecker createPermissionChecker(PermissionRule... rules) {
        return new PermissionChecker(wiki, actionDescription, rules);
    }

    /**
     * @since 1.0.1
     */
    protected abstract T createResponse(Response response) throws Exception;

    /**
     * @since 1.0.1
     */
    protected boolean needPermissionCheck() {
        return !requiredPermissions.isEmpty() || !requiredMultipleChoicePermissions.isEmpty();
    }

    /**
     * @since 1.0.1
     */
    public T execute() {
        if (needPermissionCheck()) {
            checkPermission();
        }
        Call call = wiki.newCall(buildRequest());
        try {
            return createResponse(call.execute());
        } catch (Exception e) {
            throw new RuntimeException("Failed to " + actionDescription + ": " + e.getMessage(), e);
        }
    }
}