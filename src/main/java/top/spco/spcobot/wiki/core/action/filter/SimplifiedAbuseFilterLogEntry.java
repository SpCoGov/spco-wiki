package top.spco.spcobot.wiki.core.action.filter;

import com.google.gson.JsonObject;
import top.spco.spcobot.wiki.core.NameSpace;

import static top.spco.spcobot.wiki.core.util.JsonUtil.GSON;

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
