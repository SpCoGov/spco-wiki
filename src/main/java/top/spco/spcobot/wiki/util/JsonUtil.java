package top.spco.spcobot.wiki.util;

import com.google.gson.*;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import top.spco.spcobot.wiki.NameSpace;
import top.spco.spcobot.wiki.action.parameter.LogType;

import java.time.Instant;

/**
 * 使用 Gson 进行 JSON 解析和提取的工具类。
 * 提供安全获取 JSON 元素、字符串的方法，并在遇到无效或空的 JSON 结构时进行日志记录。
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class JsonUtil {
    private static final Logger LOGGER = LogUtil.getLogger();
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LogType.class, new LogTypeDeserializer())
            .registerTypeAdapter(Instant.class, new InstantDeserializer())
            .registerTypeAdapter(NameSpace.class, new NameSpaceDeserializer())
            .create();

    /**
     * 验证提供的键路径是否无效，例如键路径为 {@code null} 或长度为 0。
     *
     * @param key 要验证的键路径
     * @return 如果键路径无效则返回 {@code true}，否则返回 {@code false}
     * @since 0.1.0
     */
    private static boolean isKeyPathInvalid(String... key) {
        return key == null || key.length == 0;
    }

    /**
     * 解析给定的 JSON 字符串为 {@code JsonObject}。
     *
     * @param json 要解析的 JSON 字符串
     * @return {@code JsonObject}
     * @throws IllegalArgumentException 如果 JSON 语法错误或解析失败
     * @since 0.1.0
     */
    private static JsonObject parseJson(String json) {
        try {
            return GSON.fromJson(json, JsonObject.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("JSON syntax error while parsing: {}", json, e);
            throw new IllegalArgumentException("Failed to parse Json due to syntax error: " + json, e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error while parsing JSON: {}", json, e);
            throw new IllegalArgumentException("Failed to parse Json: " + json, e);
        }
    }

    private static JsonElement extractElement(JsonObject json, String... key) {
        return extractElement(json, true, key);
    }

    /**
     * 从 {@code JsonObject} 中提取指定键路径的 {@code JsonElement}。
     *
     * @param json 要提取的 {@code JsonObject}
     * @param key  键路径
     * @return 对应的 {@code JsonElement}，如果键不存在或值为 {@code null}，则返回 {@code null}
     * @since 0.1.0
     */
    private static JsonElement extractElement(JsonObject json, boolean nullable, String... key) {
        JsonObject j = json;
        for (int i = 0; i < key.length; i++) {
            if (!j.has(key[i])) {
                if (!nullable) {
                    LOGGER.warn("Key '{}' not found in JSON object: {}", key[i], json);
                }
                return null;
            }
            JsonElement element = j.get(key[i]);
            if (i == key.length - 1) {
                return element.isJsonNull() ? null : element;
            } else if (element.isJsonObject()) {
                j = element.getAsJsonObject();
            } else {
                if (!nullable) {
                    LOGGER.warn("Expected JsonObject at key '{}', but found: {}", key[i], element);
                }
                return null;
            }
        }
        return null;
    }

    /**
     * 根据键路径从 JsonObject 中获取非空字符串值。
     * 如果键不存在或值为 {@code null}，。
     *
     * @param jsonObject 要解析的 JsonObject
     * @param key        键路径
     * @return 获取的字符串值或 {@code null}
     * @throws IllegalArgumentException 如果键路径未找到或值为 {@code null}
     * @since 0.1.0
     */
    public static @NotNull String checkAndGetNonNullJsonString(JsonObject jsonObject, String... key) {
        String string = checkAndGetJsonString(jsonObject, false, key);
        if (string == null) {
            LOGGER.error("Failed to get non-null string for key path {}, original JSON: {}", key, jsonObject);
            throw new IllegalArgumentException("Failed to get non-null string, original json: " + jsonObject);
        }
        return string;
    }

    /**
     * 根据键路径从 JSON 字符串中获取非 {@code null} 字符串值。
     *
     * @param json 要解析的 JSON 字符串
     * @param key  键路径
     * @return 获取的非 {@code null} 字符串值
     * @throws IllegalArgumentException 如果键路径未找到或值为 {@code null}
     * @since 0.1.0
     */
    public static @NotNull String checkAndGetNonNullString(String json, String... key) {
        String result = checkAndGetAsString(json, false, key);
        if (result == null) {
            LOGGER.error("Failed to get non-null string for key path {}, original JSON: {}", key, json);
            throw new IllegalArgumentException("Failed to get non-null string, original json: " + json);
        }
        return result;
    }

    /**
     * 根据键路径从 JSON 字符串中获取字符串值。
     * 如果键不存在或值为 {@code null}，则抛出异常。
     *
     * @param json 要解析的 JSON 字符串
     * @param key  键路径
     * @return 获取的字符串值或 {@code null}
     * @since 0.1.0
     */
    public static String checkAndGetAsString(String json, String... key) {
        return checkAndGetAsString(json, true, key);
    }

    /**
     * 根据键路径从 JSON 字符串中获取字符串值。
     * 如果键不存在或值为 {@code null}，则返回 {@code null}。
     *
     * @param json     要解析的 JSON 字符串
     * @param nullable {@code true} 表明结果可以为 {@code null}
     * @param key      键路径
     * @return 获取的字符串值或 {@code null}
     * @since 0.1.0
     */
    public static String checkAndGetAsString(String json, boolean nullable, String... key) {
        if (json == null || isKeyPathInvalid(key)) {
            LOGGER.warn("Invalid input JSON or key path");
            return null;
        }

        JsonObject j = parseJson(json);
        JsonElement element = extractElement(j, nullable, key);

        return (element != null && element.isJsonPrimitive()) ? element.getAsString() : null;
    }

    /**
     * 根据键路径从 JsonObject 中获取字符串值。
     * 如果键不存在或值为 {@code null}，则返回 {@code null}。
     *
     * @param jsonObject 要解析的 JsonObject
     * @param nullable   {@code true} 表明结果可以为 {@code null}
     * @param key        键路径
     * @return 获取的字符串值或 {@code null}
     * @since 0.1.0
     */
    public static String checkAndGetJsonString(JsonObject jsonObject, boolean nullable, String... key) {
        if (jsonObject == null) {
            LOGGER.warn("Invalid input JSON is null");
            return null;
        }
        JsonElement element = extractElement(jsonObject, nullable, key);

        return (element != null && element.isJsonPrimitive()) ? element.getAsString() : null;
    }

    /**
     * 根据键路径从 JSON 字符串中获取 {@code JsonElement}。
     * 如果键不存在或值为 {@code null}，则返回 {@code null}。
     *
     * @param json 要解析的 JSON 字符串
     * @param key  键路径
     * @return 获取的 {@code JsonElement} 或 {@code null}
     * @since 0.1.0
     */
    public static JsonElement checkAndGetElement(String json, String... key) {
        return checkAndGetElement(json, true, key);
    }

    /**
     * 根据键路径从 JSON 字符串中获取 {@code JsonElement}。
     * 如果键不存在或值为 {@code null}，则返回 {@code null}。
     *
     * @param json     要解析的 JSON 字符串
     * @param nullable {@code true} 表明结果可以为 {@code null}
     * @param key      键路径
     * @return 获取的 {@code JsonElement} 或 {@code null}
     * @since 0.1.0
     */
    public static JsonElement checkAndGetElement(String json, boolean nullable, String... key) {
        if (json == null || isKeyPathInvalid(key)) {
            LOGGER.warn("Invalid input JSON or key path");
            return null;
        }

        JsonObject j = parseJson(json);
        return extractElement(j, nullable, key);
    }

    /**
     * 根据键路径从 {@code JsonObject} 中获取 {@code JsonElement}。
     * 如果键不存在或值为 {@code null}，则返回 {@code null}。
     *
     * @param json 要提取的 {@code JsonObject}
     * @param key  键路径
     * @return 获取的 {@code JsonElement} 或 {@code null}
     * @since 0.1.0
     */
    public static JsonElement checkAndGetElement(JsonObject json, String... key) {
        return checkAndGetElement(json, true, key);
    }

    /**
     * 根据键路径从 {@code JsonObject} 中获取 {@code JsonElement}。
     * 如果键不存在或值为 {@code null}，则返回 {@code null}。
     *
     * @param json     要提取的 {@code JsonObject}
     * @param nullable {@code true} 表明结果可以为 {@code null}
     * @param key      键路径
     * @return 获取的 {@code JsonElement} 或 {@code null}
     * @since 0.1.0
     */
    public static JsonElement checkAndGetElement(JsonObject json, boolean nullable, String... key) {
        if (json == null || isKeyPathInvalid(key)) {
            LOGGER.warn("Invalid input JSON or key path");
            return null;
        }

        return extractElement(json, nullable, key);
    }

    /**
     * 根据键路径从 JSON 字符串中获取非空 {@code JsonElement}。
     * 如果键不存在或值为 {@code null}，则抛出异常。
     *
     * @param json 要解析的 JSON 字符串
     * @param key  键路径
     * @return 获取的 {@code JsonElement}
     * @throws IllegalArgumentException 如果键路径未找到或值为 {@code null}
     * @since 0.1.0
     */
    public static @NotNull JsonElement checkAndGetNonNullElement(String json, String... key) {
        return checkAndGetNonNullElement(GSON.fromJson(json, JsonObject.class), key);
    }

    /**
     * 根据键路径从 {@code JsonObject} 中获取非空 {@code JsonElement}。
     * 如果键不存在或值为 {@code null}，则抛出异常。
     *
     * @param json 要提取的 {@code JsonObject}
     * @param key  键路径
     * @return 获取的 {@code JsonElement}
     * @throws IllegalArgumentException 如果键路径未找到或值为 {@code null}
     * @since 0.1.0
     */
    public static @NotNull JsonElement checkAndGetNonNullElement(JsonObject json, String... key) {
        JsonElement element = checkAndGetElement(json, false, key);
        if (element == null) {
            LOGGER.error("Failed to get non-null element for key path {}, original JSON: {}", key, json);
            throw new IllegalArgumentException("Failed to get non-null element, original json: " + json);
        }
        return element;
    }

    /**
     * 将 {@code JsonArray} 转换为 {@code String[]}。
     *
     * @param jsonArray 要转换的 {@code JsonArray}
     * @return 转换得到的 {@code String[]}
     * @since 0.1.0
     */
    public static String[] jsonArrayToStringArray(JsonArray jsonArray) {
        String[] result = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement element = jsonArray.get(i);
            result[i] = element.getAsString();  // 将每个元素转换为字符串
        }
        return result;
    }
}