package top.spco.spcobot.wiki.core.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import top.spco.spcobot.wiki.core.NameSpace;

import java.lang.reflect.Type;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class NameSpaceDeserializer implements JsonDeserializer<NameSpace> {
    @Override
    public NameSpace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        int value = jsonElement.getAsInt();
        return NameSpace.from(value);
    }
}
