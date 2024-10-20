package top.spco.spcobot.wiki.core;

/**
 * 表示一个与 {@link Wiki} 相关的基础类。
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public abstract class WikiBase {
    protected final Wiki wiki;

    /**
     * 构造一个 WikiBase 实例，初始化 Wiki 对象。
     *
     * @param wiki 要关联的 Wiki 实例
     * @since 0.1.0
     */
    public WikiBase(Wiki wiki) {
        this.wiki = wiki;
    }

    /**
     * 获取当前的 Wiki 实例。
     *
     * @return 当前的 Wiki 实例
     * @since 0.1.0
     */
    public Wiki getWiki() {
        return wiki;
    }
}
