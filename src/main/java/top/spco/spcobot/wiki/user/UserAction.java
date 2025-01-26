package top.spco.spcobot.wiki.user;

import java.util.HashSet;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public interface UserAction {
    /**
     * 用户是否拥有某项 {@link UserRight 权限} 。
     *
     * @param right 要检查的权限
     * @return 用户拥有这项权限时返回 {@code true} ，否则返回 {@code false}
     * @since 0.1.0
     */
    boolean hasRight(UserRight right);

    /**
     * 用户是否隶属于某个 {@link UserGroup 群组} 。
     *
     * @param group 要检查的群组
     * @return 用户隶属于这个群组时返回 {@code true} ，否则返回 {@code false}
     * @since 0.1.0
     */
    boolean inGroup(UserGroup group);

    /**
     * 列举用户隶属的所有 {@link UserGroup 群组} 。
     *
     * @return 用户隶属的所有群组
     * @since 0.1.0
     */
    HashSet<UserGroup> getGroups();

    /**
     * 列举用户拥有的所有 {@link UserRight 权限} 。
     *
     * @return 用户拥有的所有权限
     * @since 0.1.0
     */
    HashSet<UserRight> getRights();
}
