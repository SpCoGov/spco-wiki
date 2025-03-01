package top.spco.spcobot.wiki.action.request;

import org.apache.logging.log4j.Logger;
import top.spco.spcobot.wiki.action.PermissionRule;
import top.spco.spcobot.wiki.user.UserRight;
import top.spco.spcobot.wiki.util.LogUtil;

import java.util.Map;

/**
 * @author SpCo
 * @version 1.0.1
 * @since 1.0.1
 */
public abstract class QuerySubmodule<T> {
    private final static Logger LOGGER = LogUtil.getLogger();
    protected final QueryRequest request;
    protected final String paramPrefix;
    protected T result;
    boolean parsingPhase = false;

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
            addQueryParameter(paramPrefix + "limit", String.valueOf(limit));
        } else {
            addQueryParameter(paramPrefix + "limit", "max");
        }
    }

    /**
     * @since 1.0.1
     */
    public QuerySubmodule<T> addQueryParameter(String key, String value) {
        addParameterWarning();
        request.addQueryParameter(key, value);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public QuerySubmodule<T> addQueryParameter(String key, int value) {
        return addQueryParameter(key, String.valueOf(value));
    }

    /**
     * @since 1.0.1
     */
    public QuerySubmodule<T> addQueryParameter(String key, boolean value) {
        if (value) {
            addQueryParameter(key, "true");
        }
        return this;
    }

    /**
     * @since 1.0.1
     */
    public QuerySubmodule<T> addQueryParameter(Map<String, String> params) {
        addParameterWarning();
        request.addQueryParameter(params);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public void requiredPermission(UserRight... rights) {
        addParameterWarning();
        request.requiredPermission(rights);
    }

    /**
     * @since 1.0.1
     */
    public void requiredPermission(PermissionRule rule) {
        addParameterWarning();
        request.requiredPermission(rule);
    }

    /**
     * @since 1.0.1
     */
    public QuerySubmodule<T> addFormParameter(String key, String value) {
        addParameterWarning();
        request.addFormParameter(key, value);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public QuerySubmodule<T> addFormParameter(Map<String, String> params) {
        addParameterWarning();
        request.addFormParameter(params);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public T getResult() {
        return result;
    }

    private void addParameterWarning() {
        if (parsingPhase) {
            LOGGER.warn("{} is adding parameters during the parsing phase. The added parameters will not be applied.", this.getClass().getName());
        }
    }
}
