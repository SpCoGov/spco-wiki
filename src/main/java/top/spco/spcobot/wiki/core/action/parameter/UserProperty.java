package top.spco.spcobot.wiki.core.action.parameter;

import top.spco.spcobot.wiki.core.Wiki;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public enum UserProperty {
    /**
     * 如果用户被封禁就标记，并注明是谁封禁，以何种原因封禁的。
     */
    BLOCK_INFO("blockinfo"),
    /**
     * 列举每位用户属于的所有组。
     */
    GROUPS("groups"),
    /**
     * 列举明确分配给每位用户的用户组，包括每个用户组成员的过期时间。
     */
    GROUP_MEMBER_SHIPS("groupmemberships"),
    /**
     * 列举用户自动作为成员之一的所有组。
     */
    IMPLICIT_GROUPS("implicitgroups"),
    /**
     * 列举每位用户拥有的所有权限。
     */
    RIGHTS("rights"),
    /**
     * 添加用户的编辑计数。
     */
    EDIT_COUNT("editcount"),
    /**
     * 添加用户的注册时间戳。
     */
    REGISTRATION("registration"),
    /**
     * 当用户可以并希望通过{@code Special:Emailuser}接收电子邮件时标记。
     */
    EMAILABLE("emailable"),
    /**
     * 标记用户性别。返回{@code "male"}、{@code "female"}或{@code "unknown"}。
     */
    GENDER("gender"),
    /**
     * 添加中心ID并为用户附加状态。
     */
    CENTRALIDS("centralids"),
    /**
     * 表明是否可以为有效但尚未注册的用户名创建一个账户。要检查当前用户是否可以执行账户创建操作，请使用 {@link Wiki#userInfo(String...)} 并传入 {@code "cancreateaccount"} 。
     */
    // TODO: 添加这个api
    CAN_CREATE("cancreate");
    public final String value;

    UserProperty(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
