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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.HttpUrl;
import top.spco.spcobot.wiki.action.parameter.Assert;
import top.spco.spcobot.wiki.util.JsonUtil;
import top.spco.spcobot.wiki.util.TOTPUtil;

import java.net.CookieManager;
import java.net.Proxy;
import java.util.function.Supplier;

/**
 * {@code WikiBuilder} 类用于构建访问 MediaWiki API 的 Wiki 实例，支持匿名登录、普通用户登录和机器人登录。
 *
 * <p>
 * 该类的不同方法允许创建不同的登录模式，包括：
 * <ul>
 *   <li>匿名用户登录</li>
 *   <li>普通用户登录（可选使用 OTP 供应商）</li>
 *   <li>机器人用户登录（需具备机器人权限）</li>
 * </ul>
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class WikiBuilder {
    private final HttpUrl actionApi;
    private CookieManager cookieManager = null;
    private Proxy proxy = null;
    private Supplier<String> otpSupplier = null;

    /**
     * 使用指定的 API 地址创建一个新的 {@code WikiBuilder} 实例。
     *
     * @param actionApi Wiki API 的 URL 地址
     * @since 0.1.0
     */
    public WikiBuilder(HttpUrl actionApi) {
        this.actionApi = actionApi;
    }

    /**
     * 设置 {@code CookieManager}，用于管理登录过程中的 Cookie。
     *
     * @param cookieManager 要设置的 {@code CookieManager} 实例
     * @return 当前的 {@code WikiBuilder} 实例（支持链式调用）
     * @since 0.1.0
     */
    public WikiBuilder setCookieManager(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
        return this;
    }

    /**
     * 当登录时需要OTP代码时，会自动输入该供应商提供的OTP代码。
     *
     * @param otpSupplier OTP代码供应商
     * @return 当前的 {@code WikiBuilder} 实例（支持链式调用）
     * @see TOTPUtil#getOTP(String)
     * @since 0.1.0
     */
    public WikiBuilder setOtpSupplier(Supplier<String> otpSupplier) {
        this.otpSupplier = otpSupplier;
        return this;
    }

    /**
     * 设置代理服务器，通过该代理访问 Wiki。
     *
     * @param proxy 要设置的 {@code Proxy} 实例
     * @return 当前的 {@code WikiBuilder} 实例（支持链式调用）
     * @since 0.1.0
     */
    public WikiBuilder setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * 以匿名用户登录Wiki。
     *
     * @return 登录的Wiki实例
     * @since 0.1.0
     */
    public Wiki anonymous() {
        return new Wiki(actionApi, cookieManager, proxy);
    }

    /**
     * 以普通用户登录Wiki。
     *
     * @return 登录的Wiki实例
     * @since 0.1.0
     */
    public Wiki normal(String username, String password) {
        return new Wiki(actionApi, cookieManager, proxy, username, password, Assert.USER, otpSupplier);
    }

    /**
     * 以机器人登录Wiki，
     * 用户需要拥有机器人权限。
     *
     * @return 登录的Wiki实例
     * @since 0.1.0
     */
    public Wiki bot(String username, String password) {
        return new Wiki(actionApi, cookieManager, proxy, username, password, Assert.BOT, otpSupplier);
    }

    /**
     * 通过 JSON 对象创建 {@code WikiBuilder} 实例。<br>
     * JSON 对象需要包含以下参数：
     * <table>
     *     <tr>
     *         <th>参数名</th>
     *         <th>参数类型</th>
     *         <th>描述</th>
     *         <th>示例</th>
     *     </tr>
     *     <tr>
     *         <td>api</td>
     *         <td>String</td>
     *         <td>Wiki API 的 URL 地址</td>
     *         <td><pre>{@code "https://en.wikipedia.org/w/api.php"}</pre></td>
     *     </tr>
     *     <tr>
     *         <td>login_type</td>
     *         <td>Integer</td>
     *         <td>登录模式：0 - 匿名，1 - 普通用户，2 - 机器人</td>
     *         <td><pre>{@code 1}</pre></td>
     *     </tr>
     *     <tr>
     *         <td>user</td>
     *         <td>Object</td>
     *         <td>登录的用户信息，{@code login_type} 为 {@code 0} 时请忽略此参数。</td>
     *         <td><pre>{@code {"username": "Username","password": "Password","otp_key": "XXXXXXXXXXXXXXXX"}}</pre></td>
     * </table>
     * user对象需要包含以下参数：
     * <table>
     *     <tr>
     *         <th>参数名</th>
     *         <th>参数类型</th>
     *         <th>描述</th>
     *         <th>示例</th>
     *     </tr>
     *     <tr>
     *         <td>username</td>
     *         <td>String</td>
     *         <td>登录的用户名</td>
     *         <td><pre>{@code "Username"}</pre></td>
     *     </tr>
     *     <tr>
     *         <td>password</td>
     *         <td>String</td>
     *         <td>登录的用户密码</td>
     *         <td><pre>{@code "Password"}</pre></td>
     *     </tr>
     *     <tr>
     *         <td>otp_key</td>
     *         <td>String</td>
     *         <td>可选，TOTP令牌</td>
     *         <td><pre>{@code "XXXXXXXXXXXXXXXX"}</pre></td>
     *     </tr>
     * </table>
     * 示例 JSON：
     * <pre>{@code
     * {
     *   "api": "https://en.wikipedia.org/w/api.php",
     *   "login_type": 1,
     *   "user": {
     *     "username": "Username",
     *     "password": "Password",
     *     "otp_key": "XXXXXXXXXXXXXXXX"
     *   }
     * }
     * }</pre>
     * @since 0.1.0
     */
    public static Wiki fromJson(JsonObject jsonObject) {
        String apiUrl = JsonUtil.checkAndGetNonNullElement(jsonObject, "api").getAsString();
        int loginType = JsonUtil.checkAndGetNonNullElement(jsonObject, "login_type").getAsInt();
        WikiBuilder builder = new WikiBuilder(HttpUrl.parse(apiUrl));
        switch (loginType) {
            case 0 -> {
                return builder.anonymous();
            }
            case 1, 2 -> {
                JsonObject userObject = JsonUtil.checkAndGetNonNullElement(jsonObject, "user").getAsJsonObject();
                String username = JsonUtil.checkAndGetNonNullElement(userObject, "username").getAsString();
                String password = JsonUtil.checkAndGetNonNullElement(userObject, "password").getAsString();
                JsonElement otpKey = JsonUtil.checkAndGetElement(userObject, "otp_key");
                if (otpKey != null) {
                    builder.setOtpSupplier(TOTPUtil.getOtpSupplier(otpKey.getAsString()));
                }
                if (loginType == 1) {
                    return builder.normal(username, password);
                } else {
                    return builder.bot(username, password);
                }
            }
            default -> throw new IllegalArgumentException("Invalid login type: " + loginType);
        }
    }
}