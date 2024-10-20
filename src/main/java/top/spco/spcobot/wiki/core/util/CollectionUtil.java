package top.spco.spcobot.wiki.core.util;

import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class CollectionUtil {
    /**
     * 将一个 {@code Collection} 分割成多个子集合，每个子集合的大小不超过指定的上限。
     *
     * @param source     待分割的集合
     * @param size       每个子集合的最大元素数
     * @param atLeastOne 如果 source 为空，是否返回至少一个空的子集合
     * @param <T>        集合中元素的类型
     * @param <C>        Collection 的子类型
     * @return 包含多个子集合的列表，子集合的类型与 source 相同
     * @throws IllegalArgumentException 如果 size 小于 1
     */
    @SuppressWarnings("unchecked")
    public static <T, C extends Collection<T>> List<C> split(C source, int size, boolean atLeastOne) {
        if (size < 1) {
            throw new IllegalArgumentException("size must be greater than 0");
        }

        List<C> result = new ArrayList<>();
        Iterator<T> iterator = source.iterator();

        try {
            if (iterator.hasNext()) {
                while (iterator.hasNext()) {
                    C chunk = (C) source.getClass().getDeclaredConstructor().newInstance();

                    for (int i = 0; i < size && iterator.hasNext(); i++) {
                        chunk.add(iterator.next());
                    }
                    result.add(chunk);
                }
            } else if (atLeastOne) {
                C empty = (C) source.getClass().getDeclaredConstructor().newInstance();
                result.add(empty);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to create instance of sub-collection", e);
        }

        return result;
    }

    /**
     * 将一个 {@code Collection} 分割成多个子集合，每个子集合的大小不超过指定的上限。
     *
     * @param source 待分割的集合
     * @param size   每个子集合的最大元素数
     * @param <T>    集合中元素的类型
     * @param <C>    Collection 的子类型
     * @return 包含多个子集合的列表，子集合的类型与 source 相同
     * @throws IllegalArgumentException 如果 size 小于 1
     */
    public static <T, C extends Collection<T>> List<C> split(C source, int size) {
        return split(source, size, false);
    }

    @SafeVarargs
    public static <E extends @Nullable Object> ArrayList<E> newArrayList(E... elements) {
        if (elements == null) {
            throw new NullPointerException();
        }
        // Avoid integer overflow when a large array is passed in
        int capacity = 5 + elements.length + (elements.length / 10);
        ArrayList<E> list = new ArrayList<>(capacity);
        Collections.addAll(list, elements);
        return list;
    }
}
