package top.spco.spcobot.wiki.core;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于检测文本中是否包含特定的敏感词汇或词语片段。
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class SpanBlacklist {
    private static final HashSet<String> snippets = new HashSet<>();
    private static final Pattern pattern;

    static {
        snippets.add("asp[eu]r?gite");
        snippets.add("asshole");
        snippets.add("butthurt");
        snippets.add("cunt");
        snippets.add("dick");
        snippets.add("faggot");
        snippets.add("freeminecraft");
        snippets.add("fuck");
        snippets.add("nigger");
        snippets.add("penis");
        snippets.add("queer");
        snippets.add("rap(?:e|list)");
        snippets.add("vagina");
        snippets.add("viagra");
        pattern = Pattern.compile(String.join("|", snippets), Pattern.CASE_INSENSITIVE);
    }

    /**
     * 初步检查文本是否会被过滤器#67阻止。
     *
     * @param text 需要检查的文本
     * @return 是否会被过滤器#67阻止
     * @since 0.1.0
     */
    public static boolean check(String text) {
        return pattern.matcher(text).find();
    }

    /**
     * 将文本中的敏感词汇替换为指定的替换字符串。
     *
     * @param text        需要净化的文本
     * @param replacement 替换敏感词汇的字符串
     * @return 替换后的净化文本
     * @since 0.1.0
     */
    public static String purify(String text, String replacement) {
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replacement);
    }

    /**
     * 使用默认的 "■" 替换文本中的敏感词汇。
     *
     * @param text 需要净化的文本
     * @return 替换后的净化文本
     * @since 0.1.0
     */
    public static String purify(String text) {
        return purify(text, "■");
    }
}
