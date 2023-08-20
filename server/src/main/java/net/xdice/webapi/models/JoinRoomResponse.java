package net.xdice.webapi.models;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class JoinRoomResponse {
    private static final JsonAdapter<JoinRoomResponse> jsonAdapter = new Moshi.Builder().build().adapter(JoinRoomResponse.class);

    private final String roomCode;
    private final User user;

    public JoinRoomResponse(String roomCode, User user) {
        this.roomCode = roomCode;
        this.user = user;
    }

    public String toJsonString() {
        return jsonAdapter.toJson(this);
    }
}
