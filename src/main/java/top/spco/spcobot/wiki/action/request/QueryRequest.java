package top.spco.spcobot.wiki.action.request;

import com.google.gson.JsonObject;
import okhttp3.Call;
import okhttp3.Response;
import top.spco.spcobot.wiki.ActionRequest;
import top.spco.spcobot.wiki.RequestMethod;
import top.spco.spcobot.wiki.Wiki;
import top.spco.spcobot.wiki.action.ActionType;
import top.spco.spcobot.wiki.util.MapUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author SpCo
 * @version 1.0.1
 * @since 1.0.1
 */
public class QueryRequest extends ActionRequest<QueryResponse> {
    private boolean shouldStoreResponses = false;
    protected final HashSet<QuerySubmodule<?>> submodules = new HashSet<>();
    private final HashSet<Class<? extends QuerySubmodule<?>>> addedSubmodules = new HashSet<>();

    /**
     * @since 1.0.1
     */
    public QueryRequest(Wiki wiki, String actionDescription) {
        super(wiki, RequestMethod.GET, ActionType.QUERY, actionDescription);
    }

    /**
     * @since 1.0.1
     */
    public QueryRequest(Wiki wiki) {
        super(wiki, RequestMethod.GET, ActionType.QUERY, "query information");
    }

    @Override
    protected QueryResponse createResponse(Response response) {
        return new QueryResponse(this, response);
    }

    /**
     * @since 1.0.1
     */
    public void setShouldStoreResponses(boolean shouldStoreResponses) {
        this.shouldStoreResponses = shouldStoreResponses;
    }

    /**
     * @since 1.0.1
     */
    @SuppressWarnings("unchecked")
    public void addSubmodule(QuerySubmodule<?> submodule) {
        if (addedSubmodules.contains(submodule.getClass())) {
            throw new DuplicateSubmoduleException();
        }
        addedSubmodules.add((Class<? extends QuerySubmodule<?>>) submodule.getClass());
        submodules.add(submodule);
    }

    /**
     * @since 1.0.1
     */
    public HashSet<QuerySubmodule<?>> getSubmodules() {
        return submodules;
    }

    @Override
    public QueryResponse execute() {
        if (needPermissionCheck()) {
            checkPermission();
        }
        QueryResponse first = null;
        QueryResponse prev = null;
        boolean continuable = false;
        Map<String, String> continueParam = new HashMap<>();
        do {
            Map<String, String> additional = new HashMap<>();
            if (continuable) {
                additional.putAll(continueParam);
            }
            Call call = wiki.newCall(buildRequest(additional, null));
            try {
                QueryResponse response = createResponse(call.execute());
                // 初始化链表头
                if (first == null) {
                    first = response;
                }
                if (shouldStoreResponses) {
                    // 构建链表
                    if (prev != null) {
                        prev.next = response;
                    }
                    // 更新上一个节点
                    prev = response;
                }
                response.first = first;
                continuable = response.getResponseBodyJson().has("continue");
                if (continuable) {
                    JsonObject continueJson = response.getResponseBodyJson().get("continue").getAsJsonObject();
                    continueParam = MapUtil.jsonToMap(continueJson);
                }
                for (QuerySubmodule<?> submodule : submodules) {
                    submodule.parsingPhase = true;
                    submodule.parse(response);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to " + actionDescription + ": " + e.getMessage(), e);
            }
        } while (continuable);
        return first;
    }
}
