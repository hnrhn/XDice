package net.xdice.webapi.models;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class JoinRoomRequest {
    private static final JsonAdapter<JoinRoomRequest> jsonAdapter = new Moshi.Builder().build().adapter(JoinRoomRequest.class);

    private String roomCode;
    private String username;
    private String password;

    public static JoinRoomRequest fromJsonString(String jsonString) throws IOException {
        return jsonAdapter.fromJson(jsonString);
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
