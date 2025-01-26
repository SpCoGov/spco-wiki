package top.spco.spcobot.wiki.action;

import top.spco.spcobot.wiki.user.UserRight;
import top.spco.spcobot.wiki.util.CollectionUtil;

import java.util.List;
import java.util.Set;

/**
 * 表示权限规则的类。
 * 包含两种规则类型：
 * 1. {@code ALL} - 必须拥有所有指定的权限；
 * 2. {@code ANY} - 只需拥有任意一个指定的权限。
 *
 * @author SpCo
 * @version 1.0.1
 * @since 1.0.1
 */
public record PermissionRule(RuleType ruleType,
                             Set<UserRight> requiredPermissions) {
    /**
     * 规则类型枚举。
     *
     * @author SpCo
     * @version 1.0.1
     * @since 1.0.1
     */
    public enum RuleType {
        /**
         * 必须全部满足。
         *
         * @since 1.0.1
         */
        ALL,
        /**
         * 至少满足一个。
         *
         * @since 1.0.1
         */
        ANY
    }

    /**
     * @since 1.0.1
     */
    public boolean isEmpty() {
        return requiredPermissions.isEmpty();
    }

    /**
     * @since 1.0.1
     */
    public boolean add(UserRight right) {
        return requiredPermissions.add(right);
    }

    /**
     * @since 1.0.1
     */
    public String toExceptionMessage() {
        if (isEmpty()) {
            return "";
        }
        List<String> permissionsList = requiredPermissions.stream()
                .map(UserRight::toString)
                .toList();
        switch (ruleType) {
            case ALL -> {
                return CollectionUtil.toNaturalString(permissionsList);
            }
            case ANY -> {
                if (permissionsList.size() == 1) {
                    return permissionsList.getFirst();
                } else {
                    return "Any one of " + CollectionUtil.toNaturalString(permissionsList);
                }
            }
            default -> {
                return "";
            }
        }
    }
}