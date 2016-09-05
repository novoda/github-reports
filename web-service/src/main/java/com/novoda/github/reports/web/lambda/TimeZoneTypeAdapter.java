package com.novoda.github.reports.web.lambda;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.TimeZone;

class TimeZoneTypeAdapter extends TypeAdapter<TimeZone> {

    @Override
    public void write(JsonWriter out, TimeZone value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.getID());
    }

    @Override
    public TimeZone read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return TimeZone.getDefault();
        }
        return TimeZone.getTimeZone(in.nextString());
    }

}
