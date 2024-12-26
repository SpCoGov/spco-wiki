package top.spco.spcobot.wiki.core.action.response;

import okhttp3.Response;
import top.spco.spcobot.wiki.core.ActionResponse;
import top.spco.spcobot.wiki.core.action.request.UnblockRequest;

public class UnblockResponse extends ActionResponse<UnblockRequest, Void> {
    public UnblockResponse(UnblockRequest request, Response response) {
        super(request, response);
        expectedApiErrorCode("cantunblock", (code, info) -> {
            throw new IllegalStateException("'" + request.user + "'" + " is not blocked.");
        });
        expectedApiErrorCode("blockedasrange", (code, info) -> {
            throw new IllegalStateException(info);
        });
    }

    @Override
    public Void parse() {
        return super.parse();
    }
}
