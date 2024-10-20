package top.spco.spcobot.wiki.core.action.parameter;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public enum RevisionType {
    NOT_ANON("!anon"),
    ANON("anon"),
    NOT_AUTOPATROLLED("!autopatrolled"),
    AUTOPATROLLED("autopatrolled"),
    NOT_BOT("!bot"),
    BOT("bot"),
    NOT_MINOR("!minor"),
    MINOR("minor"),
    NOT_PATROLLED("!patrolled"),
    PATROLLED("patrolled"),
    NOT_REDIRECT("!redirect"),
    REDIRECT("redirect"),
    UNPATROLLED("unpatrolled"),
    ;
    private final String value;

    RevisionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
