package top.spco.spcobot.wiki.core.user;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import top.spco.spcobot.wiki.core.Wiki;
import top.spco.spcobot.wiki.core.WikiBase;
import top.spco.spcobot.wiki.core.action.parameter.UserProperty;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class User extends WikiBase implements UserAction {
    @SerializedName("name")
    private String name;
    private final HashMap<UserProperty, JsonElement> properties = new HashMap<>();

    public User(Wiki wiki, String name) {
        super(wiki);
        this.name = name;
    }

    public static User fromJson(Wiki wiki, JsonObject json) {
        String name = json.get("name").getAsString();
        return new User(wiki, name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public JsonElement getProperty(UserProperty key) {
        return properties.get(key);
    }

    public void setProperty(UserProperty key, JsonElement value) {
        if (key == null || value == null) {
            return;
        }
        properties.put(key, value);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * 用户是否拥有某项 {@link UserRight 权限} 。
     *
     * @param right 要检查的权限
     * @return 用户拥有这项权限时返回 {@code true} ，否则返回 {@code false}
     * @since 0.1.0
     */
    @Override
    public boolean hasRight(UserRight right) {
        return false;
    }

    /**
     * 用户是否隶属于某个 {@link UserGroup 群组} 。
     *
     * @param group 要检查的群组
     * @return 用户隶属于这个群组时返回 {@code true} ，否则返回 {@code false}
     * @since 0.1.0
     */
    @Override
    public boolean inGroup(UserGroup group) {
        return false;
    }

    /**
     * 列举用户隶属的所有 {@link UserGroup 群组} 。
     *
     * @return 用户隶属的所有群组
     * @since 0.1.0
     */
    @Override
    public HashSet<UserGroup> getGroups() {
        return null;
    }

    public void updateProperties(UserProperty... properties) {
        if (properties == null || properties.length == 0) {
            return;
        }
        JsonObject prop = wiki.usersMeta(new String[]{name}, properties).get(name);
        updatePropertiesFromJson(prop, properties);
    }

    void updatePropertiesFromJson(JsonObject prop, UserProperty... properties) {
        for (UserProperty property : properties) {
            setProperty(property, prop.get(property.value));
        }
    }

    /**
     * 列举用户拥有的所有 {@link UserRight 权限} 。
     *
     * @return 用户拥有的所有权限
     * @since 0.1.0
     */
    @Override
    public HashSet<UserRight> getRights() {
        return null;
    }
}
