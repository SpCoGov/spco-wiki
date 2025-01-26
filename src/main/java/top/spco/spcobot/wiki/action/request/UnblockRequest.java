package top.spco.spcobot.wiki.action.request;

import okhttp3.Response;
import top.spco.spcobot.wiki.ActionRequest;
import top.spco.spcobot.wiki.RequestMethod;
import top.spco.spcobot.wiki.Wiki;
import top.spco.spcobot.wiki.action.ActionType;
import top.spco.spcobot.wiki.action.response.UnblockResponse;
import top.spco.spcobot.wiki.user.UserRight;

public class UnblockRequest extends ActionRequest<UnblockResponse> {
    public final String user;

    public UnblockRequest(String user, Wiki wiki) {
        super(wiki, RequestMethod.POST, ActionType.UNBLOCK, "unblock user");
        this.user = user;
        addFormParameter("user", user);
        requiredPermission(UserRight.BLOCK);
    }

    /**
     * 解封的原因。
     *
     * @return 返回自身
     * @since 1.0.1
     */
    public UnblockRequest reason(String reason) {
        addFormParameter("reason", reason);
        return this;
    }

    /**
     * 监视用户的用户页和讨论页。
     *
     * @return 返回自身
     * @since 1.0.1
     */
    public UnblockRequest watchUser() {
        addQueryParameter("watchuser", "true");
        return this;
    }

    @Override
    protected UnblockResponse createResponse(Response response) {
        return new UnblockResponse(this, response);
    }
}
