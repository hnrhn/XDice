package net.xdice.webapi.models;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class Error {
    private static final JsonAdapter<Error> jsonAdapter = new Moshi.Builder().build().adapter(Error.class);

    private final String errorText;

    public Error(String errorText) {
        this.errorText = errorText;
    }

    public String getErrorText() {
        return this.errorText;
    }

    public static Error fromJsonString(String jsonString) throws IOException {
        return jsonAdapter.fromJson(jsonString);
    }

    public String toJsonString() {
        return jsonAdapter.toJson(this);
    }
}
