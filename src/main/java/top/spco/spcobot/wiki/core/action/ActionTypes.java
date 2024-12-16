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
package top.spco.spcobot.wiki.core.action;

import com.google.common.collect.ImmutableMap;
import top.spco.spcobot.wiki.core.action.parameter.TokenType;

import java.util.HashMap;

import static top.spco.spcobot.wiki.core.action.parameter.TokenType.CSRF;
import static top.spco.spcobot.wiki.core.util.MapUtil.*;

/**
 * @author SpCo
 * @version 1.0.1
 * @since 0.1.0
 * @deprecated 请使用 {@link ActionType}
 */
@Deprecated
public enum ActionTypes {
    // MARK: Action
    @Deprecated
    BLOCK(action("block"), CSRF),
    @Deprecated
    COMPARE(action("compare")),
    @Deprecated
    UNBLOCK(action("unblock"), CSRF),
    @Deprecated
    LOGIN(action("login"), TokenType.LOGIN, "lgtoken"),
    @Deprecated
    THANK(action("thank"), CSRF),
    @Deprecated
    UPLOAD(action("upload"), CSRF),
    @Deprecated
    MOVE(action("move"), CSRF),
    @Deprecated
    DELETE(action("delete"), CSRF),
    @Deprecated
    CLIENT_LOGIN(action("clientlogin"), TokenType.LOGIN, "logintoken"),
    @Deprecated
    EDIT(action("edit"), CSRF),
    @Deprecated
    PATROL(action("patrol"), TokenType.PATROL),

    // MARK: Query
    @Deprecated
    USER_INFO(queryMeta("userinfo")),
    @Deprecated
    SITE_INFO(queryMeta("siteinfo")),
    @Deprecated
    TOKENS_CSRF(paramsMap("action", "query", "meta", "tokens", "type", "csrf")),
    @Deprecated
    TOKENS_PATROL(paramsMap("action", "query", "meta", "tokens", "type", "patrol")),
    @Deprecated
    TOKENS_LOGIN(paramsMap("action", "query", "meta", "tokens", "type", "login")),
    @Deprecated
    ALL_PAGES(queryList("allpages")),
    @Deprecated
    ALL_REVISIONS(queryList("allrevisions")),
    @Deprecated
    ALL_USERS(queryList("allusers")),
    @Deprecated
    USERS(queryList("users")),
    @Deprecated
    RECENT_CHANGES(queryList("recentchanges")),
    @Deprecated
    LOG_EVENT(queryList("logevents")),
    @Deprecated
    ABUSE_FILTERS(queryList("abusefilters")),
    @Deprecated
    ABUSE_LOG(queryList("abuselog")),
    @Deprecated
    LINKS_HERE(queryProp("linkshere")),
    @Deprecated
    REVISIONS(queryProp("revisions")),
    ;

    private final ImmutableMap<String, String> baseParams;
    private final boolean needToken;
    private final TokenType tokenType;
    private final String tokenParameterName;

    ActionTypes(HashMap<String, String> baseParams, TokenType tokenType, String tokenParameterName) {
        this.baseParams = ImmutableMap.copyOf(baseParams);
        this.needToken = true;
        if (tokenType == null) {
            throw new NullPointerException();
        }
        this.tokenType = tokenType;
        this.tokenParameterName = tokenParameterName;
    }

    ActionTypes(HashMap<String, String> baseParams, TokenType tokenType) {
        this(baseParams, tokenType, "token");
    }

    ActionTypes(HashMap<String, String> baseParams) {
        this.baseParams = ImmutableMap.copyOf(baseParams);
        this.needToken = false;
        this.tokenType = null;
        this.tokenParameterName = null;
    }

    public ImmutableMap<String, String> getBaseParams() {
        return baseParams;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public boolean needToken() {
        return needToken;
    }

    public String getTokenParameterName() {
        return tokenParameterName;
    }
}