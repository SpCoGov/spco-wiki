package top.spco.spcobot.wiki.action.request;

import okhttp3.Response;
import top.spco.spcobot.wiki.ActionRequest;
import top.spco.spcobot.wiki.NameSpace;
import top.spco.spcobot.wiki.RequestMethod;
import top.spco.spcobot.wiki.Wiki;
import top.spco.spcobot.wiki.action.ActionType;
import top.spco.spcobot.wiki.action.parameter.Expiry;
import top.spco.spcobot.wiki.action.response.BlockResponse;
import top.spco.spcobot.wiki.user.UserRight;

/**
 * 封禁一位用户，需要 {@link UserRight#BLOCK} 权限。
 *
 * @author SpCo
 * @version 1.0.1
 * @since 1.0.1
 */
public class BlockRequest extends ActionRequest<BlockResponse> {
    public final String user;
    public final Expiry expiry;

    /**
     * @since 1.0.1
     */
    public BlockRequest(String user, Expiry expiry, Wiki wiki) {
        super(wiki, RequestMethod.POST, ActionType.BLOCK, "block user");
        this.user = user;
        this.expiry = expiry;
        addFormParameter("user", user).addFormParameter("expiry", expiry.toString());
        requiredPermission(UserRight.BLOCK);
    }

    /**
     * @return 返回自身
     * @since 1.0.1
     */
    public BlockRequest reason(String reason) {
        addFormParameter("reason", reason);
        return this;
    }

    /**
     * @return 返回自身
     * @since 1.0.1
     */
    public BlockRequest anonOnly() {
        addFormParameter("anononly", "true");
        return this;
    }

    /**
     * @return 返回自身
     * @since 1.0.1
     */
    public BlockRequest noCreate() {
        addFormParameter("nocreate", "true");
        return this;
    }

    /**
     * @return 返回自身
     * @since 1.0.1
     */
    public BlockRequest allowUserTalk() {
        addFormParameter("allowusertalk", "true");
        return this;
    }

    /**
     * @return 返回自身
     * @since 1.0.1
     */
    public BlockRequest reblock() {
        addFormParameter("reblock", "true");
        return this;
    }

    /**
     * @return 返回自身
     * @since 1.0.1
     */
    public BlockRequest watchUser() {
        addFormParameter("watchuser", "true");
        return this;
    }

    /**
     * @return 返回自身
     * @since 1.0.1
     */
    public BlockRequest pageRestriction(String... pageTitles) {
        if (pageTitles.length > 10) {
            throw new IllegalArgumentException("Page restrictions should not exceed 10.");
        }
        addFormParameter("partial", "true");
        addFormParameter("pagerestrictions", String.join("|", pageTitles));
        return this;
    }

    /**
     * @return 返回自身
     * @since 1.0.1
     */
    public BlockRequest nameSpaceRestriction(NameSpace... namespaces) {
        if (namespaces.length > 10) {
            throw new IllegalArgumentException("Namespace restrictions should not exceed 10.");
        }
        addFormParameter("namespacerestrictions", NameSpace.toApiParam(true, namespaces));
        return this;
    }

    @Override
    protected BlockResponse createResponse(Response response) {
        return new BlockResponse(this, response);
    }
}
