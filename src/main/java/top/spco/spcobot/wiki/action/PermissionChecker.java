package top.spco.spcobot.wiki.action;

import top.spco.spcobot.wiki.Wiki;
import top.spco.spcobot.wiki.exception.InsufficientPermissionsException;
import top.spco.spcobot.wiki.user.UserRight;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 权限检查器，根据权限规则检查用户是否拥有所需权限。
 */
public class PermissionChecker {
    private final Wiki wiki;
    private final Set<PermissionRule> rules = new HashSet<>();
    private String action;

    /**
     * @since 1.0.1
     */
    public PermissionChecker(Wiki wiki, PermissionRule... rules) {
        this.wiki = wiki;
        this.rules.addAll(List.of(rules));
    }

    /**
     * @since 1.0.1
     */
    public PermissionChecker(Wiki wiki, String action, PermissionRule... rules) {
        this.wiki = wiki;
        this.action = action;
        this.rules.addAll(List.of(rules));
    }

    /**
     * @since 1.0.1
     */
    public PermissionChecker addRule(PermissionRule rule) {
        rules.add(rule);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public Set<PermissionRule> missing() {
        HashSet<PermissionRule> missing = new HashSet<>();
        PermissionRule missingAllNeededPermissions = new PermissionRule(PermissionRule.RuleType.ALL, new HashSet<>());
        Set<String> rights = wiki.getRightsName();
        for (PermissionRule rule : rules) {
            switch (rule.ruleType()) {
                case ALL -> {
                    for (UserRight required : rule.requiredPermissions()) {
                        if (!rights.contains(required.toString())) {
                            missingAllNeededPermissions.add(required);
                        }
                    }
                }
                case ANY -> {
                    boolean hasAnyPermission = rule.requiredPermissions().stream()
                            .anyMatch(required -> rights.contains(required.toString()));
                    // 如果没有满足任何权限，标记整个规则为缺失
                    if (!hasAnyPermission) {
                        missing.add(rule);
                    }
                }
            }
        }
        if (!missingAllNeededPermissions.isEmpty()) {
            missing.add(missingAllNeededPermissions);
        }
        return missing;
    }

    /**
     * @since 1.0.1
     */
    public void passOrThrow() throws InsufficientPermissionsException {
        Set<PermissionRule> missing = missing();
        if (missing.isEmpty()) {
            return;
        }
        throw new InsufficientPermissionsException(action, missing);
    }
}
