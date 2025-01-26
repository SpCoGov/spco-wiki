package top.spco.spcobot.wiki.action.filter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import top.spco.spcobot.wiki.util.JsonUtil;

import static top.spco.spcobot.wiki.util.JsonUtil.GSON;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public record AbuseFilter(
        int id,
        String description,
        String[] actions,
        boolean enabled,
        boolean deleted,
        boolean isPrivate) {
    /**
     * 过滤器id。
     *
     * @return 过滤器id
     * @since 0.1.0
     */
    @Override
    public int id() {
        return id;
    }

    /**
     * 过滤器是否被删除。
     *
     * @return {@code true} 表示已删除，{@code false} 表示未删除。未获取此属性时也会返回 {@code false}
     * @since 0.1.0
     */
    @Override
    public boolean deleted() {
        return deleted;
    }

    /**
     * 过滤器的公开描述。
     *
     * @return 公开描述
     * @since 0.1.0
     */
    @Override
    public String description() {
        return description;
    }

    /**
     * 触发过滤器后的操作
     *
     * @return 操作
     * @since 0.1.0
     */
    @Override
    public String[] actions() {
        return actions;
    }

    /**
     * 过滤器是否被开启
     *
     * @return {@code true} 表示已开启，{@code false} 表示未开启。未获取此属性时也会返回 {@code false}
     * @since 0.1.0
     */
    @Override
    public boolean enabled() {
        return enabled;
    }

    /**
     * 过滤器是否为非公开
     *
     * @return {@code true} 表示非公开，{@code false} 表示公开。未获取此属性时也会返回 {@code false}
     * @since 0.1.0
     */
    @Override
    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     * @since 0.1.0
     */
    public static AbuseFilter fromJson(String json) {
        JsonObject jsonObject = GSON.fromJson(json, JsonObject.class);
        return fromJson(jsonObject);
    }

    /**
     * @since 0.1.0
     */
    public static AbuseFilter fromJson(JsonObject json) {
        int id = JsonUtil.checkAndGetNonNullElement(json, "id").getAsInt();
        String description = JsonUtil.checkAndGetNonNullJsonString(json, "description");
        String[] actions;
        boolean enabled;
        boolean deleted;
        boolean isPrivate;
        try {
            JsonArray actionsJa = JsonUtil.checkAndGetNonNullElement(json, "actions").getAsJsonArray();
            actions = JsonUtil.jsonArrayToStringArray(actionsJa);
        } catch (IllegalStateException e) {
            if (e.getMessage().startsWith("Not a JSON Array")) {
                String actionsS = JsonUtil.checkAndGetNonNullJsonString(json, "actions");
                actions = actionsS.split(",");
            } else {
                throw e;
            }
        }
        enabled = json.has("enabled");
        deleted = json.has("deleted");
        isPrivate = json.has("private");

        return new AbuseFilter(id, description, actions, enabled, deleted, isPrivate);
    }

    @Override
    public String toString() {
        return id + "";
    }

    public static boolean hasPrivateFilter(AbuseFilter[] filters) {
        if (filters == null) {
            return false;
        }
        for (AbuseFilter filter : filters) {
            if (filter.isPrivate()) {
                return true;
            }
        }
        return false;
    }
}
