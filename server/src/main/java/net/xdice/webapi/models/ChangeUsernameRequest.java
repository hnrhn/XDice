package net.xdice.webapi.models;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class ChangeUsernameRequest {
    private static final JsonAdapter<ChangeUsernameRequest> jsonAdapter = new Moshi.Builder().build().adapter(ChangeUsernameRequest.class);

    private String newUsername;

    public String getNewUsername() {
        return newUsername;
    }

    public static ChangeUsernameRequest fromJsonString(String jsonString) throws IOException {
        return jsonAdapter.fromJson(jsonString);
    }
}
