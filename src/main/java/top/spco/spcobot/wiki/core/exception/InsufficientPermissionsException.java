package top.spco.spcobot.wiki.core.exception;

import top.spco.spcobot.wiki.core.action.PermissionRule;
import top.spco.spcobot.wiki.core.user.UserRight;
import top.spco.spcobot.wiki.core.util.CollectionUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示权限不足的异常。
 * 当用户尝试执行他们没有权限进行的操作时，抛出该异常。
 *
 * @author SpCo
 * @version 1.0.1
 * @since 0.1.0
 */
public class InsufficientPermissionsException extends RuntimeException {

    /**
     * 创建一个默认的权限不足异常。
     *
     * @since 0.1.0
     */
    public InsufficientPermissionsException() {
        super("Insufficient permissions to perform this action.");
    }

    /**
     * 创建一个带有自定义消息的权限不足异常。
     *
     * @param action 自定义行为
     * @since 0.1.0
     */
    public InsufficientPermissionsException(String action) {
        super("Insufficient permissions to " + action + ".");
    }

    /**
     * 创建一个带有自定义消息和所缺少权限的权限不足异常。
     *
     * @param action             自定义行为
     * @param allNeeded          是否需要所有的权限
     * @param missingPermissions 所缺少的权限
     * @since 0.1.1
     */
    public InsufficientPermissionsException(String action, boolean allNeeded, UserRight... missingPermissions) {
        this(action, formatPermissions(allNeeded, missingPermissions));
    }

    /**
     * 创建一个带有自定义消息和所缺少权限的权限不足异常。
     *
     * @param action       自定义行为
     * @param missingRules 所缺少的权限
     * @since 1.0.1
     */
    public InsufficientPermissionsException(String action, PermissionRule... missingRules) {
        this(action, new HashSet<>(List.of(missingRules)));
    }

    /**
     * 创建一个带有自定义消息和所缺少权限的权限不足异常。
     *
     * @param action       自定义行为
     * @param missingRules 所缺少的权限
     * @since 1.0.1
     */
    public InsufficientPermissionsException(String action, Set<PermissionRule> missingRules) {
        this(action, formatPermissions(missingRules));
    }

    /**
     * 创建一个带有自定义消息和所缺少权限的权限不足异常。
     *
     * @param action             自定义行为
     * @param missingPermissions 所缺少的权限
     * @since 1.0.1
     */
    public InsufficientPermissionsException(String action, String missingPermissions) {
        super("Insufficient permissions to " + (action == null ? "perform this action" : action) +
                ". Missing permissions: " + missingPermissions + ".");
    }

    /**
     * 创建一个带有自定义消息和所缺少权限的权限不足异常。
     *
     * @param action             自定义行为
     * @param missingPermissions 所缺少的权限
     * @since 0.1.1
     */
    public InsufficientPermissionsException(String action, UserRight... missingPermissions) {
        this(action, true, missingPermissions);
    }

    private static String formatPermissions(Set<PermissionRule> missingRules) {
        List<String> ruleMessages = missingRules.stream()
                .map(PermissionRule::toExceptionMessage)
                .collect(Collectors.toList());

        return CollectionUtil.toNaturalString(ruleMessages);
    }

    private static String formatPermissions(boolean allNeeded, UserRight[] missingPermission) {
        Set<PermissionRule> missingPermissions = new HashSet<>();
        Set<UserRight> rights = new HashSet<>(List.of(missingPermission));
        if (allNeeded) {
            missingPermissions.add(new PermissionRule(PermissionRule.RuleType.ALL, rights));
        } else {
            missingPermissions.add(new PermissionRule(PermissionRule.RuleType.ANY, rights));
        }
        return formatPermissions(missingPermissions);
    }
}
