package com.dandrona.vcfwrapper;

import com.google.gson.*;

public class KeyValueObjectSerializer implements JsonSerializer<KeyValueObject> {
    @Override
    public JsonElement serialize(KeyValueObject src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty(src.getKey(), src.getValue());
        return result;
    }
}
