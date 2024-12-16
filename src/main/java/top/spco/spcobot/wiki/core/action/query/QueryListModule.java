package top.spco.spcobot.wiki.core.action.query;

import top.spco.spcobot.wiki.core.QuerySubmodule;
import top.spco.spcobot.wiki.core.action.request.QueryRequest;

/**
 * @author SpCo
 * @version 1.0.1
 * @since 1.0.1
 */
public abstract class QueryListModule<T> extends QuerySubmodule<T> {
    public QueryListModule(QueryRequest request, String paramPrefix, String moduleName) {
        super(request, paramPrefix);
        limit(-1);
        request.addQueryParameter("list", moduleName);
    }
}