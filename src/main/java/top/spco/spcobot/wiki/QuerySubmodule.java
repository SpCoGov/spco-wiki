package top.spco.spcobot.wiki;

import top.spco.spcobot.wiki.action.PermissionRule;
import top.spco.spcobot.wiki.action.request.QueryRequest;
import top.spco.spcobot.wiki.action.request.QueryResponse;
import top.spco.spcobot.wiki.user.UserRight;

import java.util.Map;

/**
 * @author SpCo
 * @version 1.0.1
 * @since 1.0.1
 */
public abstract class QuerySubmodule<T> {
    protected final QueryRequest request;
    protected final String paramPrefix;
    protected T result;

    /**
     * @since 1.0.1
     */
    public QuerySubmodule(QueryRequest request, String paramPrefix) {
        this.request = request;
        this.paramPrefix = paramPrefix;
    }

    /**
     * @since 1.0.1
     */
    public abstract void parse(QueryResponse response);

    /**
     * @since 1.0.1
     */
    public void limit(int limit) {
        if (limit > 0) {
            request.addQueryParameter(paramPrefix + "limit", String.valueOf(limit));
        } else {
            request.addQueryParameter(paramPrefix + "limit", "max");
        }
    }

    /**
     * @since 1.0.1
     */
    public QuerySubmodule<T> addQueryParameter(String key, String value) {
        request.addQueryParameter(key, value);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public QuerySubmodule<T> addQueryParameter(Map<String, String> params) {
        request.addQueryParameter(params);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public void requiredPermission(UserRight... rights) {
        request.requiredPermission(rights);
    }

    /**
     * @since 1.0.1
     */
    public void requiredPermission(PermissionRule rule) {
        request.requiredPermission(rule);
    }

    /**
     * @since 1.0.1
     */
    public QuerySubmodule<T> addFormParameter(String key, String value) {
        request.addFormParameter(key, value);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public QuerySubmodule<T> addFormParameter(Map<String, String> params) {
        request.addFormParameter(params);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public T getResult() {
        return result;
    }
}
