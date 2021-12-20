package net.xdice.webapi.models;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class CreateRoomRequest {
    private static final JsonAdapter<CreateRoomRequest> jsonAdapter = new Moshi.Builder().build().adapter(CreateRoomRequest.class);

    private String roomName;
    private String ownerName;
    private String password;

    public static CreateRoomRequest fromJsonString(String jsonString) throws IOException {
        return jsonAdapter.fromJson(jsonString);
    }

    public String getRoomName() {
        return roomName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getPassword() {
        return password;
    }
}
