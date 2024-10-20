package top.spco.spcobot.wiki.core.util;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class ParamUtil {
    /**
     * 将一个可迭代的对象转换为以“|”分隔的字符串。
     *
     * @param iterable 要转换的可迭代对象
     * @return 以“|”分隔的字符串，包含所有对象的字符串表示
     * @since 0.1.0
     */
    public static String toListParam(Iterable<?> iterable) {
        StringBuilder sb = new StringBuilder();
        for (Object o : iterable) {
            sb.append(o.toString()).append("|");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 将一个对象数组转换为以“|”分隔的字符串。
     *
     * @param objects 要转换的对象数组
     * @return 以“|”分隔的字符串，包含所有对象的字符串表示
     * @since 0.1.0
     */
    public static String toListParam(Object[] objects) {
        StringBuilder sb = new StringBuilder();
        for (Object o : objects) {
            sb.append(o.toString()).append("|");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
