package top.spco.spcobot.wiki.action.query;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import top.spco.spcobot.wiki.action.request.QueryRequest;
import top.spco.spcobot.wiki.action.request.QueryResponse;
import top.spco.spcobot.wiki.user.User;
import top.spco.spcobot.wiki.user.UserGroup;
import top.spco.spcobot.wiki.user.UserSet;
import top.spco.spcobot.wiki.util.JsonUtil;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AllUsersListModule extends QueryListModule<UserSet> {
    public AllUsersListModule(QueryRequest request) {
        super(request, "au", "allusers");
        result = new UserSet(request.wiki);
    }

    public AllUsersListModule groups(UserGroup... groups) {
        if (groups == null || groups.length == 0) {
            return this;
        }
        String augroup = Arrays.stream(groups)
                .map(g -> g.value)
                .collect(Collectors.joining("|"));
        addQueryParameter("augroup", String.join("|", augroup));
        return this;
    }

    public AllUsersListModule from(String user) {
        addQueryParameter("aufrom", user);
        return this;
    }

    public AllUsersListModule to(String user) {
        addQueryParameter("auto", user);
        return this;
    }

    public AllUsersListModule prefix(String prefix) {
        addQueryParameter("auprefix", prefix);
        return this;
    }

    public AllUsersListModule withEditsOnly() {
        addQueryParameter("auwitheditsonly", "true");
        return this;
    }

    public AllUsersListModule activeUsers() {
        addQueryParameter("auactiveusers", "true");
        return this;
    }

    @Override
    public void parse(QueryResponse response) {
        JsonElement element = JsonUtil.checkAndGetElement(response.getResponseBodyJson(), "query", "allusers");
        if (element == null) {
            return;
        }
        JsonArray usersJson = element.getAsJsonArray();
        for (JsonElement user : usersJson) {
            result.add(User.fromJson(request.wiki, user.getAsJsonObject()));
        }
    }
}
