package top.spco.spcobot.wiki.action.query;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import top.spco.spcobot.wiki.LogEntry;
import top.spco.spcobot.wiki.action.parameter.LogType;
import top.spco.spcobot.wiki.action.request.QueryRequest;
import top.spco.spcobot.wiki.action.request.QueryResponse;
import top.spco.spcobot.wiki.util.JsonUtil;

import java.util.HashSet;

public class LogEventsListModule extends QueryListModule<HashSet<LogEntry>> {
    public LogEventsListModule(QueryRequest request) {
        super(request, "le", "logevents");
        result = new HashSet<>();
    }

    public LogEventsListModule type(LogType type) {
        addQueryParameter("letype", type.toString());
        return this;
    }

    public LogEventsListModule user(String user) {
        addQueryParameter("user", user);
        return this;
    }


    @Override
    public void parse(QueryResponse response) {
        JsonArray eventJson = JsonUtil.checkAndGetNonNullElement(response.getResponseBodyJson(), "query", "logevents").getAsJsonArray();
        for (JsonElement event : eventJson) {
            result.add(LogEntry.fromJson(event.getAsJsonObject()));
        }
    }
}
