package top.spco.spcobot.wiki.core.action.query;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import top.spco.spcobot.wiki.core.NameSpace;
import top.spco.spcobot.wiki.core.Page;
import top.spco.spcobot.wiki.core.action.parameter.FilterRedirect;
import top.spco.spcobot.wiki.core.action.request.QueryRequest;
import top.spco.spcobot.wiki.core.action.request.QueryResponse;
import top.spco.spcobot.wiki.core.util.JsonUtil;

import java.util.HashSet;

/**
 * @author SpCo
 * @version 1.0.1
 * @since 1.0.1
 */
public class AllPagesListModule extends QueryListModule<HashSet<Page>> {
    public AllPagesListModule(QueryRequest request) {
        super(request, "ap", "allpages");
        result = new HashSet<>();
    }

    public AllPagesListModule from(String title) {
        addQueryParameter("apfrom", title);
        return this;
    }

    public AllPagesListModule to(String title) {
        addQueryParameter("apto", title);
        return this;
    }

    public AllPagesListModule prefix(String prefix) {
        addQueryParameter("apprefix", prefix);
        return this;
    }

    public AllPagesListModule filterRedirect(FilterRedirect filterRedirect) {
        addQueryParameter("apfilterredir", filterRedirect.value);
        return this;
    }

    public AllPagesListModule nameSpace(NameSpace nameSpace) {
        addQueryParameter("apnamespace", String.valueOf(nameSpace.value));
        return this;
    }

    public AllPagesListModule minSize(int minSize) {
        addQueryParameter("apminsize", String.valueOf(minSize));
        return this;
    }

    public AllPagesListModule maxSize(int maxSize) {
        addQueryParameter("apmaxsize", String.valueOf(maxSize));
        return this;
    }

    @Override
    public void parse(QueryResponse response) {
        JsonElement element = JsonUtil.checkAndGetElement(response.getResponseBodyJson(), "query", "allpages");
        if (element == null) {
            return;
        }
        JsonArray pagesJson = element.getAsJsonArray();
        for (JsonElement page : pagesJson) {
            result.add(Page.fromJson(request.wiki, page.getAsJsonObject()));
        }
    }
}
