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
package top.spco.spcobot.wiki.util;

import java.util.HashSet;
import java.util.Set;

public class SetUtil {
    /**
     * 找到两个集合之间的差异。
     *
     * <p>该方法返回一个新的集合，其中包含在主集合中但不在比较集合中的所有元素。
     *
     * @param mainSet       主集合，提供要检查差异的元素
     * @param comparisonSet 用于比较的集合，包含要从主集合中移除的元素
     * @param <T>           集合中元素的类型
     * @return 一个新的集合，包含所有在主集合中但不在比较集合中的元素。如果主集合为空，则返回一个空集合
     * @since 0.1.0
     */
    public static <T> Set<T> findDifference(Set<T> mainSet, Set<T> comparisonSet) {
        // 创建一个新的 HashSet 来存放结果，以避免修改原始集合
        Set<T> result = new HashSet<>(mainSet);
        // 移除所有在 comparisonSet 中存在的元素
        result.removeAll(comparisonSet);
        return result;
    }
}