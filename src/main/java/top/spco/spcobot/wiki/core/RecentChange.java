package top.spco.spcobot.wiki.core;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import static top.spco.spcobot.wiki.core.util.JsonUtil.GSON;

/**
 * @author SpCo
 * @version 0.1.2
 * @since 0.1.0
 */
public record RecentChange(@SerializedName("rcid") int id,
                           @SerializedName("revid") int revisionId,
                           @SerializedName("old_revid") int oldRevisionId,
                           @SerializedName("timestamp") String timestamp,
                           String title,
                           @SerializedName("ns") NameSpace nameSpace,
                           String user,
                           @SerializedName("comment") String summary) {
    public static RecentChange fromJson(String json) {
        return GSON.fromJson(json, RecentChange.class);
    }

    public static RecentChange fromJson(JsonObject json) {
        return GSON.fromJson(json, RecentChange.class);
    }
}
