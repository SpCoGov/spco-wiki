package top.spco.spcobot.wiki.action.query;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import top.spco.spcobot.wiki.action.filter.AbuseFilter;
import top.spco.spcobot.wiki.action.request.QueryRequest;
import top.spco.spcobot.wiki.action.request.QueryResponse;
import top.spco.spcobot.wiki.util.JsonUtil;

import java.util.HashMap;

public class AbuseFiltersListModule extends QueryListModule<HashMap<Integer, AbuseFilter>> {
    public AbuseFiltersListModule(QueryRequest request) {
        super(request, "abf", "abusefilters");
        result = new HashMap<>();
        addQueryParameter("abfprop", "actions|comments|description|id|pattern|private|status");
    }

    public AbuseFiltersListModule startId(int id) {
        addQueryParameter("abfstartid", id);
        return this;
    }

    public AbuseFiltersListModule endId(int id) {
        addQueryParameter("abfendid", id);
        return this;
    }

    @Override
    public void parse(QueryResponse response) {
        JsonArray array = JsonUtil.checkAndGetNonNullElement(response.getResponseBodyJson(), "query", "abusefilters").getAsJsonArray();
        for (JsonElement arrayElement : array) {
            JsonObject jsonObject = arrayElement.getAsJsonObject();
            AbuseFilter filter = AbuseFilter.fromJson(jsonObject);
            result.put(filter.id(), filter);
        }
    }
}
