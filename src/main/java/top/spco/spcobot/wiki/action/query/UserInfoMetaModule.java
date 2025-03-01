package top.spco.spcobot.wiki.action.query;

import com.google.gson.JsonObject;
import top.spco.spcobot.wiki.action.request.QueryRequest;
import top.spco.spcobot.wiki.action.request.QueryResponse;

// TODO
public class UserInfoMetaModule extends QueryMetaModule<JsonObject> {
    public UserInfoMetaModule(QueryRequest request, String paramPrefix, String moduleName) {
        super(request, paramPrefix, moduleName);
    }

    @Override
    public void parse(QueryResponse response) {

    }
}
