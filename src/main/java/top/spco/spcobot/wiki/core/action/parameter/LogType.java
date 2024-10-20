package top.spco.spcobot.wiki.core.action.parameter;

import java.util.HashMap;

/**
 * 日志类型。
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
@SuppressWarnings("SpellCheckingInspection")
public enum LogType {
    ABUSEFILTER,
    ABUSEFILTERBLOCKEDDOMAINHIT,
    ABUSEFILTERPRIVATEDETAILS,
    BLOCK,
    CHECKUSER_TEMPORARY_ACCOUNT,
    CONTENTMODEL,
    CREATE,
    DELETE,
    GBLBLOCK,
    GBLRIGHTS,
    GLOOPCONTROL,
    IMPORT,
    MANAGETAGS,
    MERGE,
    MOVE,
    NEWUSERS,
    OATH,
    PATROL,
    PROTECT,
    RENAMEUSER,
    RIGHTS,
    SMW,
    SPAMBLACKLIST,
    SUPPRESS,
    TAG,
    THANKS,
    TIMEDMEDIAHANDLER,
    TITLEBLACKLIST,
    UPLOAD;

    private static final HashMap<String, LogType> logTypes = new HashMap<>();

    static {
        for (LogType logType : LogType.values()) {
            logTypes.put(logType.toString(), logType);
        }
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace("_", "-");
    }

    public static LogType toLogType(String type) {
        return logTypes.get(type);
    }
}
