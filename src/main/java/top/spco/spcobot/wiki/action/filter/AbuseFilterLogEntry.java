package top.spco.spcobot.wiki.action.filter;

import com.google.gson.JsonObject;
import top.spco.spcobot.wiki.NameSpace;

import static top.spco.spcobot.wiki.util.JsonUtil.GSON;

public record AbuseFilterLogEntry(
        int id,
        String filterId,
        String filter,
        String user,
        NameSpace ns,
        String title,
        String action,
        String result,
        String timestamp,
        JsonObject details
) {
    public static AbuseFilterLogEntry fromJson(JsonObject json) {
        return GSON.fromJson(json, AbuseFilterLogEntry.class);
    }
}
