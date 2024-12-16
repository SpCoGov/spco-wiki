package top.spco.spcobot.wiki.core.action.response;

import okhttp3.Response;
import top.spco.spcobot.wiki.core.ActionResponse;
import top.spco.spcobot.wiki.core.action.request.BlockRequest;
import top.spco.spcobot.wiki.core.exception.AlreadyBlockedException;
import top.spco.spcobot.wiki.core.exception.NoSuchUserException;

public class BlockResponse extends ActionResponse<BlockRequest, Void> {
    public BlockResponse(BlockRequest request, Response response) {
        super(request, response);
        expectedApiErrorCode("missingtitle");
        expectedApiErrorCode("ipb-prevent-user-talk-edit");
        expectedApiErrorCode("nosuchuser", (code, info) -> {
            throw new NoSuchUserException(request.user);
        });
        expectedApiErrorCode("alreadyblocked", (code, info) -> {
            throw new AlreadyBlockedException(request.user);
        });
    }

    @Override
    public Void parse() {
        return super.parse();
    }
}
