/*
 * Copyright 2024 SpCo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.spcobot.wiki.core.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于创建和处理包含键值对的 {@link HashMap} 的工具类。
 * 主要用于构建参数映射，尤其适用于 API 请求。
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class MapUtil {
    /**
     * 创建一个包含键值对的 {@link HashMap}。
     * 键值对通过可变参数传递，奇数位置作为键，偶数位置作为值。
     *
     * @param elements 可变参数列表，交替传递键和值，必须为偶数个元素
     * @return 包含键值对的 {@link HashMap}
     * @throws IllegalArgumentException 如果传递的元素个数为奇数
     * @since 0.1.0
     */
    public static HashMap<String, String> paramsMap(Object... elements) {
        if (elements.length % 2 == 1) {
            throw new IllegalArgumentException("The number of elements cannot be an odd number");
        }
        HashMap<String, String> params = new HashMap<>();
        for (int i = 0; i < elements.length; i += 2) {
            params.put(elements[i].toString(), elements[i + 1].toString());
        }
        return params;
    }

    /**
     * 创建一个带有 "action" 键的 {@link HashMap}，键值对为 "action" 与传递的值。
     *
     * @param action 要设置的动作值
     * @return 包含 "action" 键的 {@link HashMap}
     * @since 0.1.0
     */
    public static HashMap<String, String> action(String action) {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", action);
        return params;
    }

    /**
     * 创建一个带有 "action" 为 "query" 且包含 "meta" 键的 {@link HashMap}。
     *
     * @param meta 要查询的元数据类型
     * @return 包含 "action" 和 "meta" 键的 {@link HashMap}
     * @since 0.1.0
     */
    public static HashMap<String, String> queryMeta(String meta) {
        HashMap<String, String> params = action("query");
        params.put("meta", meta);
        return params;
    }

    /**
     * 创建一个带有 "action" 为 "query" 且包含 "list" 键的 {@link HashMap}。
     *
     * @param list 要查询的列表类型
     * @return 包含 "action" 和 "list" 键的 {@link HashMap}
     * @since 0.1.0
     */
    public static HashMap<String, String> queryList(String list) {
        HashMap<String, String> params = action("query");
        params.put("list", list);
        return params;
    }

    /**
     * 创建一个带有 "action" 为 "query" 且包含 "prop" 键的 {@link HashMap}。
     *
     * @param prop 要查询的属性类型
     * @return 包含 "action" 和 "prop" 键的 {@link HashMap}
     * @since 0.1.0
     */
    public static HashMap<String, String> queryProp(String prop) {
        HashMap<String, String> params = action("query");
        params.put("prop", prop);
        return params;
    }

    /**
     * 将 {@link JsonObject} 转换为 {@link HashMap}，其中键为 JSON 中的字段名，值为相应的字符串形式。
     *
     * @param json 要转换的 {@link JsonObject}
     * @return 转换后的 {@link HashMap}
     * @since 0.1.0
     */
    public static HashMap<String, String> jsonToMap(JsonObject json) {
        final HashMap<String, String> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }
        return map;
    }
}