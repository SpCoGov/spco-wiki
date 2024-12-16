package top.spco.spcobot.wiki.core.action.query;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import top.spco.spcobot.wiki.core.RecentChange;
import top.spco.spcobot.wiki.core.action.request.QueryRequest;
import top.spco.spcobot.wiki.core.action.request.QueryResponse;
import top.spco.spcobot.wiki.core.util.JsonUtil;

import java.util.HashSet;

public class RecentChangesListModule extends QueryListModule<HashSet<RecentChange>> {
    public RecentChangesListModule(QueryRequest request) {
        super(request, "rc", "recentchanges");
        result = new HashSet<>();
    }

    @Override
    public void parse(QueryResponse response) {
        JsonElement element = JsonUtil.checkAndGetElement(response.getResponseBodyJson(), "query", "recentchanges");
        if (element == null) {
            return;
        }
        JsonArray rcsJson = element.getAsJsonArray();
        for (JsonElement rcJson : rcsJson) {
            result.add(RecentChange.fromJson(rcJson.getAsJsonObject()));
        }
    }
}
