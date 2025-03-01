package top.spco.spcobot.wiki.action.query;

import top.spco.spcobot.wiki.action.request.QuerySubmodule;
import top.spco.spcobot.wiki.action.request.QueryRequest;

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