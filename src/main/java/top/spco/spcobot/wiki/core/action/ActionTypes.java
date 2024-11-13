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

import java.util.HashMap;

import static top.spco.spcobot.wiki.core.util.MapUtil.*;

/**
 * @author SpCo
 * @version 0.1.2
 * @since 0.1.0
 */
public enum ActionTypes {
    BLOCK(action("block"), true),
    COMPARE(action("compare")),
    UNBLOCK(action("unblock"), true),
    LOGIN(action("login")),
    CLIENT_LOGIN(action("clientlogin")),
    USER_INFO(queryMeta("userinfo")),
    SITE_INFO(queryMeta("siteinfo")),
    TOKENS_CSRF(paramsMap("action", "query", "meta", "tokens", "type", "csrf")),
    TOKENS_PATROL(paramsMap("action", "query", "meta", "tokens", "type", "patrol")),
    TOKENS_LOGIN(paramsMap("action", "query", "meta", "tokens", "type", "login")),
    EDIT(action("edit"), true),
    PATROL(action("patrol"), true),
    ALL_PAGES(queryList("allpages")),
    ALL_REVISIONS(queryList("allrevisions")),
    ALL_USERS(queryList("allusers")),
    USERS(queryList("users")),
    RECENT_CHANGES(queryList("recentchanges")),
    LOG_EVENT(queryList("logevents")),
    ABUSE_FILTERS(queryList("abusefilters")),
    ABUSE_LOG(queryList("abuselog")),
    LINKS_HERE(queryProp("linkshere")),
    REVISIONS(queryProp("revisions")),
    ;

    private final ImmutableMap<String, String> baseParams;
    private final boolean needToken;
    private final String limitParamName;

    ActionTypes(HashMap<String, String> baseParams, boolean needToken) {
        this.baseParams = ImmutableMap.copyOf(baseParams);
        ;
        this.needToken = needToken;
        this.limitParamName = null;
    }

    ActionTypes(HashMap<String, String> baseParams, boolean needToken, String limitParamName) {
        this.baseParams = ImmutableMap.copyOf(baseParams);
        this.needToken = needToken;
        this.limitParamName = limitParamName;
    }

    ActionTypes(HashMap<String, String> baseParams) {
        this.baseParams = ImmutableMap.copyOf(baseParams);
        this.needToken = false;
        this.limitParamName = null;
    }

    public ImmutableMap<String, String> getBaseParams() {
        return baseParams;
    }

    public String getLimitParamName() {
        return limitParamName;
    }

    public boolean needToken() {
        return needToken;
    }
}