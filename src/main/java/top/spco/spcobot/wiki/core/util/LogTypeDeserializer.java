package top.spco.spcobot.wiki.core.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import top.spco.spcobot.wiki.core.action.parameter.LogType;

import java.lang.reflect.Type;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class LogTypeDeserializer implements JsonDeserializer<LogType> {
    @Override
    public LogType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String value = jsonElement.getAsString();
        return LogType.toLogType(value);
    }
}
