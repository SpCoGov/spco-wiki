package top.spco.spcobot.wiki;

import com.google.gson.JsonObject;
import top.spco.spcobot.wiki.util.JsonUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class Page extends WikiBase {
    private final int pageId;
    private final NameSpace nameSpace;
    private final String title;

    protected Page(int pageId, String title, NameSpace nameSpace, Wiki wiki) {
        super(wiki);
        this.pageId = pageId;
        this.title = title;
        this.nameSpace = nameSpace;
    }

    public static Page fromJson(Wiki wiki, JsonObject json) {
        int pageId = JsonUtil.checkAndGetNonNullElement(json, "pageid").getAsInt();
        String title = JsonUtil.checkAndGetNonNullElement(json, "title").getAsString();
        NameSpace nameSpace = NameSpace.from(JsonUtil.checkAndGetNonNullElement(json, "ns").getAsInt());
        return new Page(pageId, title, nameSpace, wiki);
    }

    public static HashSet<String> toTitleSet(Set<Page> pages) {
        HashSet<String> titleSet = new HashSet<>();
        for (Page page : pages) {
            titleSet.add(page.title);
        }
        return titleSet;
    }

    public int pageId() {
        return pageId;
    }

    public NameSpace nameSpace() {
        return nameSpace;
    }

    public String title() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}
