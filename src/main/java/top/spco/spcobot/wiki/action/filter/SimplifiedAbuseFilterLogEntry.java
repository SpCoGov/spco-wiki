package top.spco.spcobot.wiki.action.filter;

import com.google.gson.JsonObject;
import top.spco.spcobot.wiki.NameSpace;

import static top.spco.spcobot.wiki.util.JsonUtil.GSON;

public record SimplifiedAbuseFilterLogEntry(
        String filter,
        String user,
        NameSpace ns,
        String title,
        String action,
        String result,
        String timestamp
) {
    public static SimplifiedAbuseFilterLogEntry fromJson(JsonObject json) {
        return GSON.fromJson(json, SimplifiedAbuseFilterLogEntry.class);
    }
}
