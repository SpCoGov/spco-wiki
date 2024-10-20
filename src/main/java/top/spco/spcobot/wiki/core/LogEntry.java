package top.spco.spcobot.wiki.core;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import top.spco.spcobot.wiki.core.action.parameter.LogType;

import java.time.Instant;

import static top.spco.spcobot.wiki.core.util.JsonUtil.GSON;

/**
 * 一条日志事件。
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class LogEntry {
    public int ids;
    public String title;
    public LogType type;
    public String user;
    public int userid;
    public Instant timestamp;
    @SerializedName("comment")
    public String summary;
    public String parsedComment;
    public String[] tags;

    public static LogEntry fromJson(String json) {
        return GSON.fromJson(json, LogEntry.class);
    }

    public static LogEntry fromJson(JsonObject json) {
        return GSON.fromJson(json, LogEntry.class);
    }
}
