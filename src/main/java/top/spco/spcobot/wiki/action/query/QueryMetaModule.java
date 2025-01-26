package top.spco.spcobot.wiki.action.query;

import top.spco.spcobot.wiki.QuerySubmodule;
import top.spco.spcobot.wiki.action.request.QueryRequest;

public abstract class QueryMetaModule<T> extends QuerySubmodule<T> {
    public QueryMetaModule(QueryRequest request, String paramPrefix, String moduleName) {
        super(request, paramPrefix);
        request.addQueryParameter("meta", moduleName);
    }
}
