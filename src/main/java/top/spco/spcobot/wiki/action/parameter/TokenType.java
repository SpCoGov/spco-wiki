package top.spco.spcobot.wiki.action.parameter;

/**
 * @author SpCo
 * @version 1.0.1
 * @since 1.0.1
 */
public enum TokenType {
    CREATE_ACCOUNT,
    CSRF,
    LOGIN,
    PATROL,
    ROLLBACK,
    USER_RIGHTS,
    WATCH;

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace("_", "");
    }
}
