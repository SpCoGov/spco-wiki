package top.spco.spcobot.wiki.user;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.spco.spcobot.wiki.Wiki;
import top.spco.spcobot.wiki.action.parameter.UserProperty;
import top.spco.spcobot.wiki.util.LogUtil;

import java.util.*;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class UserSet extends AbstractSet<User> implements Set<User> {
    private final HashSet<UserProperty> addedProperties = new HashSet<>();
    private final HashMap<String, User> users;
    private final Wiki wiki;

    public UserSet(Wiki wiki) {
        this.wiki = wiki;
        this.users = new HashMap<>();
    }

    public UserSet(Wiki wiki, int initialCapacity) {
        this.wiki = wiki;
        this.users = new HashMap<>(initialCapacity);
    }

    public UserSet(Wiki wiki, int initialCapacity, float loadFactor) {
        this.wiki = wiki;
        this.users = new HashMap<>(initialCapacity, loadFactor);
    }

    public UserSet(Wiki wiki, Set<User> users) {
        this.wiki = wiki;
        this.users = new HashMap<>();
        addAll(users);
    }

    @Override
    public boolean add(User user) {
        if (wiki.isSameWiki(user.getWiki())) {
            return users.put(user.getName(), user) == null;
        }
        return false;
    }

    public boolean contains(String userName) {
        return users.containsKey(userName);
    }

    public boolean contains(User user) {
        return contains(user.getName());
    }

    @Override
    public boolean contains(Object o) {
        return contains(o.toString());
    }

    public boolean isSingleUser() {
        return size() == 1;
    }

    public void add(String... userName) {
        if (userName == null || userName.length == 0) {
            return;
        }
        UserProperty[] properties = addedProperties.toArray(new UserProperty[0]);
        HashMap<String, JsonObject> usersMeta = wiki.usersMeta(userName, properties);
        for (String name : userName) {
            if (contains(name)) {
                continue;
            }
            JsonObject json = usersMeta.get(name);
            User user = User.fromJson(wiki, usersMeta.get(name));
            user.updatePropertiesFromJson(json, properties);
            add(user);
        }
    }

    public void updateProperties(UserProperty... properties) {
        if (properties == null || properties.length == 0) {
            return;
        }
        addedProperties.addAll(List.of(properties));
        var props = wiki.usersMeta(getUserNameSet().toArray(new String[0]), properties);
        for (User user : users.values()) {
            JsonObject prop = props.get(user.getName());
            LogUtil.getLogger().info(prop);
            user.updatePropertiesFromJson(prop, properties);
        }
    }

    public Set<String> getUserNameSet() {
        return users.keySet();
    }

    @NotNull
    @Override
    public Iterator<User> iterator() {
        return users.values().iterator();
    }

    @Override
    public int size() {
        return users.size();
    }
}
