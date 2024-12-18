package top.spco.spcobot.wiki.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.function.Supplier;

/**
 * {@code LogUtil} 提供了日志工具方法，以便于进行日志记录。它提供了获取 {@code Logger} 的方法，检查 {@code Logger} 活跃状态的方法，
 * 并提供了用于懒加载计算结果的工具方法。
 *
 * @version 1.0.1
 * @since 0.1.0
 */
public class LogUtil {
    public static final String FATAL_MARKER_ID = "FATAL";
    public static final Marker FATAL_MARKER = MarkerManager.getMarker(FATAL_MARKER_ID);
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    /**
     * 返回一个对象，该对象会在调用其 {@code toString} 方法时，调用并返回所提供的结果供应商的结果的字符串表示形式。
     * 这可以用于懒加载计算结果。
     *
     * @param result 一个结果供应商，当需要字符串表示形式时，将会被调用。
     * @return 一个对象，其{@code toString}方法会返回结果供应商的结果的字符串表示形式。
     */
    public static Object defer(final Supplier<Object> result) {
        class ToString {
            @Override
            public String toString() {
                return result.get().toString();
            }
        }

        return new ToString();
    }

    /**
     * Caller sensitive, DO NOT WRAP
     * <p><b>该函数或方法的行为可能会因调用者的不同而改变，因此不应该尝试将其包装在其他函数或方法中。</b>
     *
     * @return 一个与调用者类关联的 {@code Logger} 。
     */
    public static Logger getLogger() {
        return LogManager.getLogger(STACK_WALKER.getCallerClass());
    }
}