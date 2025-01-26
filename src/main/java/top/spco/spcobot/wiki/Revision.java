package top.spco.spcobot.wiki;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import static top.spco.spcobot.wiki.util.JsonUtil.GSON;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public record Revision(
        @SerializedName("revid") int id,
        @SerializedName("parentid") int parentId,
        @SerializedName("timestamp") String timestamp,
        @SerializedName("comment") String comment,
        @SerializedName("user") String user) {
    public static Revision fromJson(String json) {
        return GSON.fromJson(json, Revision.class);
    }

    public static Revision fromJson(JsonObject json) {
        return GSON.fromJson(json, Revision.class);
    }
}
