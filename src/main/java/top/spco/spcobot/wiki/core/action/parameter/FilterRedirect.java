package top.spco.spcobot.wiki.core.action.parameter;

import top.spco.spcobot.wiki.core.NameSpace;
import top.spco.spcobot.wiki.core.Wiki;

/**
 * 使用 {@link Wiki#allPages} 等方法获取页面时是否要获取重定向页面。
 *
 * @author SpCo
 * @version 0.1.0
 * @see Wiki#allPages(String, FilterRedirect, NameSpace)
 * @see Wiki#allPages(String, FilterRedirect, NameSpace...)
 * @see Wiki#allPageTitles(String, FilterRedirect, NameSpace...)
 * @see Wiki#linksHere(String, FilterRedirect, NameSpace...)
 * @since 0.1.0
 */
public enum FilterRedirect {
    /**
     * 获取所有页面，包括重定向和非重定向页面。
     */
    ALL("all"),
    /**
     * 仅获取非重定向页面。
     */
    NON_REDIRECTS("nonredirects"),
    /**
     * 仅获取重定向页面。
     */
    REDIRECTS("redirects");
    public final String value;

    FilterRedirect(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
