package top.spco.spcobot.wiki.action;

import com.google.common.collect.ImmutableMap;
import top.spco.spcobot.wiki.action.parameter.TokenType;

import java.util.HashMap;

import static top.spco.spcobot.wiki.action.parameter.TokenType.CSRF;
import static top.spco.spcobot.wiki.util.MapUtil.action;

/**
 * @author SpCo
 * @version 1.0.1
 * @since 0.1.0
 */
public enum ActionType {
    BLOCK(action("block"), CSRF),
    CLIENT_LOGIN(action("clientlogin"), TokenType.LOGIN, "logintoken"),
    CREATE_ACCOUNT(action("createaccount"), TokenType.CREATE_ACCOUNT, "createtoken"),
    COMPARE(action("compare")),
    DELETE(action("delete"), CSRF),
    EDIT(action("edit"), CSRF),
    LOGIN(action("login"), TokenType.LOGIN, "lgtoken"),
    MOVE(action("move"), CSRF),
    PATROL(action("patrol"), TokenType.PATROL),
    PROTECT(action("protect"), CSRF),
    QUERY(action("query")),
    ROLLBACK(action("rollback"),TokenType.ROLLBACK),
    THANK(action("thank"), CSRF),
    UNBLOCK(action("unblock"), CSRF),
    UPLOAD(action("upload"), CSRF),
    ;

    private final ImmutableMap<String, String> baseParams;
    private final boolean needToken;
    private final TokenType tokenType;
    private final String tokenParameterName;

    ActionType(HashMap<String, String> baseParams, TokenType tokenType, String tokenParameterName) {
        this.baseParams = ImmutableMap.copyOf(baseParams);
        this.needToken = true;
        if (tokenType == null) {
            throw new NullPointerException();
        }
        this.tokenType = tokenType;
        this.tokenParameterName = tokenParameterName;
    }

    ActionType(HashMap<String, String> baseParams, TokenType tokenType) {
        this(baseParams, tokenType, "token");
    }

    ActionType(HashMap<String, String> baseParams) {
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
