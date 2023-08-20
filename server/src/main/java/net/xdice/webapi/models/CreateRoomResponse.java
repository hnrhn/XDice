package net.xdice.webapi.models;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class CreateRoomResponse {
    private static final JsonAdapter<CreateRoomResponse> jsonAdapter = new Moshi.Builder().build().adapter(CreateRoomResponse.class);

    private final String roomCode;
    private final User user;

    public CreateRoomResponse(String roomCode, User user) {
        this.roomCode = roomCode;
        this.user = user;
    }

    public String toJsonString() {
        return jsonAdapter.toJson(this);
    }
}
