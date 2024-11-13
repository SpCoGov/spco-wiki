package top.spco.spcobot.wiki.core;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import top.spco.spcobot.wiki.core.util.JsonUtil;

/**
 * 不同修订版本之间的差异。
 *
 * @author SpCo
 * @version 0.1.2
 * @since 0.1.2
 */
public record DifferentComparison(
        @SerializedName("fromid") int fromPageId,
        @SerializedName("toid") int toPageId,
        @SerializedName("fromrevid") int fromRevisionId,
        @SerializedName("torevid") int toRevisionId,
        @SerializedName("fromns") NameSpace fromNamespace,
        @SerializedName("tons") NameSpace toNamespace,
        @SerializedName("fromtitle") String fromTitle,
        @SerializedName("*") String diffHTML) {
    public static DifferentComparison fromJson(String json) {
        return JsonUtil.GSON.fromJson(json, DifferentComparison.class);
    }

    public static DifferentComparison fromJson(JsonObject json) {
        return JsonUtil.GSON.fromJson(json, DifferentComparison.class);
    }
}
