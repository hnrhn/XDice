package net.xdice.webapi.models;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class ChangeUsernameResponse {
    private static final JsonAdapter<ChangeUsernameResponse> jsonAdapter = new Moshi.Builder().build().adapter(ChangeUsernameResponse.class);

    private final String userId;
    private final String username;

    public ChangeUsernameResponse(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String toJsonString() {
        return jsonAdapter.toJson(this);
    }
}
