package com.novoda.github.reports.sheets.convert;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.novoda.github.reports.sheets.sheet.Entry;

import java.lang.reflect.Type;

public class EntryDeserializer implements JsonDeserializer<Entry> {

    @Override
    public Entry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonObject titleObject = jsonObject.get("title").getAsJsonObject();
        JsonObject contentObject = jsonObject.get("content").getAsJsonObject();

        return new Entry(titleObject.get("$t").getAsString(), contentObject.get("$t").getAsString());
    }
}
