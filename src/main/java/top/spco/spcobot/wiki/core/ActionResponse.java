package top.spco.spcobot.wiki.core;

import com.google.gson.JsonObject;
import okhttp3.Response;
import top.spco.spcobot.wiki.core.util.JsonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author SpCo
 * @version 1.0.1
 * @since 1.0.1
 */
public abstract class ActionResponse<R extends ActionRequest<?>, T> {
    public static final BiConsumer<String, String> DEFAULT_ERROR_HANDLER = (String code, String message) -> {
        throw new RuntimeException(code + ": " + message);
    };
    protected final Wiki wiki;
    protected final String actionDescription;
    private Response response;
    protected final R request;
    private final Map<String, BiConsumer<String, String>> expectedApiExceptions = new HashMap<>();
    protected String responseBody;
    protected JsonObject responseBodyJson;

    /**
     * @since 1.0.1
     */
    public ActionResponse(R request, Response response) {
        this.wiki = request.wiki;
        this.actionDescription = request.actionDescription;
        this.request = request;
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    /**
     * @since 1.0.1
     */
    public String getResponseBody() {
        if (responseBody == null) {
            try {
                responseBody = Wiki.checkAndGetBody(getResponse(), actionDescription);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read response body: " + e.getMessage(), e);
            }
        }
        return responseBody;
    }

    /**
     * @since 1.0.1
     */
    public JsonObject getResponseBodyJson() {
        if (responseBodyJson == null) {
            responseBodyJson = JsonUtil.GSON.fromJson(getResponseBody(), JsonObject.class);
        }
        return responseBodyJson;
    }

    /**
     * @since 1.0.1
     */
    public ActionResponse<R, T> expectedApiErrorCode(String code, BiConsumer<String, String> errorHandler) {
        expectedApiExceptions.put(code, errorHandler);
        return this;
    }

    /**
     * @since 1.0.1
     */
    public ActionResponse<R, T> expectedApiErrorCode(String code) {
        return expectedApiErrorCode(code, DEFAULT_ERROR_HANDLER);
    }

    /**
     * @since 1.0.1
     */
    @SuppressWarnings("unchecked")
    protected void checkError() {
        if (!responseBodyJson.has("error")) {
            return;
        }
        JsonObject errorJson = responseBodyJson.get("error").getAsJsonObject();
        String code = errorJson.get("code").getAsString();
        String info = errorJson.get("info").getAsString();
        if (code.equals("badtoken")) {
            // token timed out
            // refresh tokens and execute request again
            try {
                wiki.refreshToken();
            } catch (IOException e) {
                throw new RuntimeException("Token timed out and update failed: " + e.getMessage(), e);
            }
            ActionResponse<R, T> newResponse = (ActionResponse<R, T>) request.execute();
            responseBody = null;
            response = newResponse.getResponse();
            beforeParseBody();
        }
        if (expectedApiExceptions.containsKey(code)) {
            expectedApiExceptions.get(code).accept(code, info);
        } else {
            throw new RuntimeException(code + ": " + info);
        }
    }

    /**
     * @since 1.0.1
     */
    protected void beforeParseBody() {
        getResponseBodyJson();
        checkError();
    }

    /**
     * @since 1.0.1
     */
    public T parse() {
        beforeParseBody();
        return null;
    }
}