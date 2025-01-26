/*
 * Copyright 2024 SpCo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.spcobot.wiki.user;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public enum UserGroup {
    /**
     * 巡查豁免者
     */
    AUTOPATROL("autopatrol", 0),
    /**
     * 机器人
     */
    BOT("bot", 1),
    /**
     * 行政员
     */
    BUREAUCRAT("bureaucrat", 8),
    /**
     * 全域破坏处理员
     */
    CATS("cats", 5),
    /**
     * 用户查核员
     */
    CHECKUSER("checkuser", 4),
    /**
     * 全域界面维护员
     */
    GLOBAL_INTERFACE_MAINTAINER("global-interface-maintainer", 6),
    /**
     * 界面管理员
     */
    INTERFACE_ADMIN("interface-admin", 2),
    /**
     * 巡查员
     */
    PATROLLERS("patrollers", 3),
    /**
     * 职员
     */
    STAFF("staff", 9),
    /**
     * 管理员
     */
    SYSOP("sysop", 7);
    public final String value;
    public final int weight;

    UserGroup(final String value, final int weight) {
        this.value = value;
        this.weight = weight;
    }

    public static UserGroup toUserGroup(String name) {
        return switch (name) {
            case "autopatrol", "ap", "autopatrols" -> AUTOPATROL;
            case "bot", "bots" -> BOT;
            case "bureaucrat", "bureaucrats" -> BUREAUCRAT;
            case "checkuser", "checkusers" -> CHECKUSER;
            case "global_interface_maintainer", "global_interface_maintainers", "globalInterfaceMaintainers",
                 "globalInterfaceMaintainer" -> GLOBAL_INTERFACE_MAINTAINER;
            case "interface_admin", "interface_admins", "interfaceAdmins", "interfaceAdmin" -> INTERFACE_ADMIN;
            case "patrollers", "patroller" -> PATROLLERS;
            case "staff", "staffs" -> STAFF;
            case "sysop", "sysops", "admin", "admins", "administrator", "administrators" -> SYSOP;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return value;
    }
}