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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * {@code UserGroupFilter} 类用于根据用户所属的用户组来管理用户。
 *
 * <p>该类的功能包括：
 * <ul>
 *   <li>从特定的用户组中获取用户。</li>
 *   <li>通过比较用户组的权重来筛选用户。</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre>{@code
 * UserList userList = new UserList(autopatrolSet, bureaucratSet, botSet, ...);
 * Set<User> filteredUsers = userList.getUser(UserGroup.SYSOP, List.of(UserGroup.PATROLLERS, UserGroup.BOT));
 * }</pre>
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class UserGroupFilter {
    private final Set<String> autopatrol;
    private final Set<String> bureaucrat;
    private final Set<String> bot;
    private final Set<String> cats;
    private final Set<String> checkuser;
    private final Set<String> globalInterfaceMaintainer;
    private final Set<String> interfaceAdmin;
    private final Set<String> patrollers;
    private final Set<String> staff;
    private final Set<String> sysop;

    public UserGroupFilter(Set<String> autopatrol, Set<String> bureaucrat, Set<String> bot, Set<String> cats, Set<String> checkuser, Set<String> globalInterfaceMaintainer, Set<String> interfaceAdmin, Set<String> patrollers, Set<String> staff, Set<String> sysop) {
        this.autopatrol = autopatrol;
        this.bureaucrat = bureaucrat;
        this.bot = bot;
        this.cats = cats;
        this.checkuser = checkuser;
        this.globalInterfaceMaintainer = globalInterfaceMaintainer;
        this.interfaceAdmin = interfaceAdmin;
        this.patrollers = patrollers;
        this.staff = staff;
        this.sysop = sysop;
    }

    public UserGroupFilter(Set<User> autopatrol, Set<User> bureaucrat, Set<User> bot, Set<User> cats, Set<User> checkuser, Set<User> globalInterfaceMaintainer, Set<User> interfaceAdmin, Set<User> patrollers, Set<User> staff, Set<User> sysop, Dummy dummy) {
        this.autopatrol = toUserNameSet(autopatrol);
        this.bureaucrat = toUserNameSet(bureaucrat);
        this.bot = toUserNameSet(bot);
        this.cats = toUserNameSet(cats);
        this.checkuser = toUserNameSet(checkuser);
        this.globalInterfaceMaintainer = toUserNameSet(globalInterfaceMaintainer);
        this.interfaceAdmin = toUserNameSet(interfaceAdmin);
        this.patrollers = toUserNameSet(patrollers);
        this.staff = toUserNameSet(staff);
        this.sysop = toUserNameSet(sysop);
    }

    public Set<String> getUser(UserGroup need, Collection<UserGroup> has) {
        Set<String> result = new HashSet<>();
        if (has == null || has.isEmpty()) {
            return result;
        }
        if (need == null) {
            return result;
        }
        result.addAll(getUser(need));
        for (UserGroup compared : has) {
            if (need.weight < compared.weight) {
                result.removeAll(getUser(compared));
            }
        }
        return result;
    }

    public Set<String> getUser(String need0, String... has0) {
        Set<String> result = new HashSet<>();
        UserGroup need = UserGroup.toUserGroup(need0);
        if (need == null) {
            return result;
        }
        int nonNull = 0;
        for (String s : has0) {
            if (s == null) {
                continue;
            }
            if (UserGroup.toUserGroup(s) == null) {
                continue;
            }
            nonNull++;
        }
        if (nonNull == 0) {
            return result;
        }
        UserGroup[] has = new UserGroup[nonNull];
        for (int i = 0; i < has0.length; i++) {
            if (has0[i] == null) {
                continue;
            }
            UserGroup g = UserGroup.toUserGroup(has0[i]);
            if (g == null) {
                continue;
            }
            has[i] = g;
        }
        result.addAll(getUser(need));
        for (UserGroup group : has) {
            if (group.weight < need.weight) {
                result.removeAll(getUser(group));
            }
        }
        return result;
    }

    private Set<String> getUser(UserGroup group) {
        return switch (group) {
            case BOT -> bot;
            case CATS -> cats;
            case CHECKUSER -> checkuser;
            case GLOBAL_INTERFACE_MAINTAINER -> globalInterfaceMaintainer;
            case INTERFACE_ADMIN -> interfaceAdmin;
            case STAFF -> staff;
            case SYSOP -> sysop;
            case AUTOPATROL -> autopatrol;
            case BUREAUCRAT -> bureaucrat;
            case PATROLLERS -> patrollers;
        };
    }

    private static HashSet<String> toUserNameSet(Set<User> users) {
        HashSet<String> result = new HashSet<>();
        for (User user : users) {
            result.add(user.getName());
        }
        return result;
    }

    private static class Dummy {

    }
}