package top.spco.spcobot.wiki.core.action.query;

import top.spco.spcobot.wiki.core.action.request.QueryRequest;
import top.spco.spcobot.wiki.core.action.request.QueryResponse;

public class SiteInfoMetaModule extends QueryMetaModule<Object> {
    public SiteInfoMetaModule(QueryRequest request, String paramPrefix, String moduleName) {
        super(request, paramPrefix, moduleName);
    }

    @Override
    public void parse(QueryResponse response) {

    }
}
