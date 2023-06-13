package com.bear.bjornsdk.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.experimental.UtilityClass;

import java.util.Base64;

@UtilityClass
public class JsonArrayDeserializer {

    public String[] transformString(final JsonArray array) {
        final String[] arr = new String[array.size()];

        int index = 0;

        for (final JsonElement element : array) {
            arr[index++] = element.getAsString();
        }

        return arr;
    }
}
