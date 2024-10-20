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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    /**
     * 从输入字符串中提取两个标签之间的内容。
     *
     * <p>该方法使用正则表达式查找指定的开始标签和结束标签之间的文本。
     *
     * @param input    要处理的输入字符串
     * @param startTag 开始标签，用于标识提取的起始位置
     * @param endTag   结束标签，用于标识提取的结束位置
     * @return 返回开始标签和结束标签之间的内容。如果未找到匹配项，则返回空字符串
     * @since 0.1.0
     */
    public static String extractContent(String input, String startTag, String endTag) {
        String pattern = Pattern.quote(startTag) + "(.*?)" + Pattern.quote(endTag);
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);

        if (m.find()) {
            return m.group(1);
        } else {
            return "";
        }
    }

    /**
     * 从输入字符串中提取两个标签之间的内容，支持正则表达式标志。
     *
     * <p>该方法使用正则表达式查找指定的开始标签和结束标签之间的文本，且可以指定正则表达式的标志。
     *
     * @param input    要处理的输入字符串
     * @param startTag 开始标签，用于标识提取的起始位置
     * @param endTag   结束标签，用于标识提取的结束位置
     * @param flags    用于正则表达式编译的标志位，如 {@link Pattern#CASE_INSENSITIVE}
     * @return 返回开始标签和结束标签之间的内容。如果未找到匹配项，则返回空字符串
     * @since 0.1.0
     */
    public static String extractContent(String input, String startTag, String endTag, int flags) {
        String pattern = Pattern.quote(startTag) + "(.*?)" + Pattern.quote(endTag);
        Pattern r = Pattern.compile(pattern, flags);
        Matcher m = r.matcher(input);

        if (m.find()) {
            return m.group(1);
        } else {
            return "";
        }
    }

    /**
     * 将输入字符串转换为标题格式。
     *
     * <p>该方法按下划线分割输入字符串，并将每个单词的首字母大写，其他字母小写，最后将单词之间用空格分隔。
     *
     * @param input 要转换的输入字符串，使用下划线作为单词分隔符
     * @return 转换后的字符串，格式为标题格式（每个单词首字母大写）
     * @since 0.1.0
     */
    public static String convertToTitleCase(String input) {
        // 将输入字符串按下划线分割成数组
        String[] words = input.split("_");

        // 使用StringBuilder来拼接结果
        StringBuilder result = new StringBuilder();

        // 遍历数组，将每个单词的首字母大写，并将其余字母保持小写
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0))); // 首字母大写
                result.append(word.substring(1).toLowerCase()); // 其余部分小写
                result.append(" "); // 添加空格
            }
        }

        // 移除最后一个多余的空格
        if (!result.isEmpty()) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }

    /**
     * 验证给定的颜色代码是否符合十六进制颜色代码格式。
     *
     * <p>该方法检查输入的颜色代码是否为有效的 #RRGGBB 格式。
     *
     * @param code 要验证的颜色代码，格式应为 "#RRGGBB"
     * @return 如果颜色代码有效，返回 {@code true}；否则返回 {@code false}
     * @since 0.1.0
     */
    public static boolean isValidColorCode(String code) {
        if (code == null) {
            return false;
        }
        // 正则表达式检查是否符合 #RRGGBB 格式
        return code.matches("^#[0-9A-Fa-f]{6}$");
    }
}