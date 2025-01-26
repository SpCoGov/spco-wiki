package top.spco.spcobot.wiki.action.parameter;

/**
 * 返回格式化为行内HTML的比较结果。
 *
 * @author SpCo
 * @version 0.1.2
 * @since 0.1.2
 */
public enum DiffType {
    /**
     * 行内模式。
     */
    INLINE("inline"),
    /**
     * 表格模式。
     */
    TABLE("table"),
    /**
     * 合并模式。
     */
    UNIFIED("unified"),
    ;
    private final String value;

    DiffType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
