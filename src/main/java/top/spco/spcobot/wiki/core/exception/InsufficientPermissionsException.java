package top.spco.spcobot.wiki.core.exception;

import top.spco.spcobot.wiki.core.user.UserRight;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示权限不足的异常。
 * 当用户尝试执行他们没有权限进行的操作时，抛出该异常。
 *
 * @author SpCo
 * @version 0.1.1
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
     * @param action            自定义行为
     * @param allNeeded         是否需要所有的权限
     * @param missingPermission 所缺少的权限
     * @since 0.1.1
     */
    public InsufficientPermissionsException(String action, boolean allNeeded, UserRight... missingPermission) {
        super("Insufficient permissions to " + action +
                ". Missing permissions: " + formatPermissions(allNeeded, missingPermission) + ".");
    }

    /**
     * 创建一个带有自定义消息和所缺少权限的权限不足异常。
     *
     * @param action            自定义行为
     * @param missingPermission 所缺少的权限
     * @since 0.1.1
     */
    public InsufficientPermissionsException(String action, UserRight... missingPermission) {
        this(action, true, missingPermission);
    }

    private static String formatPermissions(boolean allNeeded, UserRight[] missingPermission) {
        List<String> permissionsList = Arrays.stream(missingPermission)
                .map(UserRight::toString)
                .collect(Collectors.toList());

        if (permissionsList.size() == 1) {
            return permissionsList.getFirst();
        } else {
            if (allNeeded) {
                return String.join(", ", permissionsList.subList(0, permissionsList.size() - 1)) +
                        " and " + permissionsList.getLast();
            }
            return String.join(" or ", permissionsList);
        }
    }
}
