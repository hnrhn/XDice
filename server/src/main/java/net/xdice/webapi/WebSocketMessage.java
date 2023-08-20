package net.xdice.webapi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.xdice.webapi.models.*;
import net.xdice.webapi.models.Error;

import java.io.IOException;

public class WebSocketMessage {
    private static final JsonAdapter<WebSocketMessage> jsonAdapter = new Moshi.Builder().build().adapter(WebSocketMessage.class);

    public static WebSocketMessage fromJsonString(String jsonString) throws IOException {
        return jsonAdapter.fromJson(jsonString);
    }

    public String toJsonString() {
        return jsonAdapter.toJson(this);
    }

    private String type;

    private String data;

    public String getData() {
        return this.data;
    }

    public String getType() {
        return this.type;
    }

    public static WebSocketMessage chatResponseOf(String mentionedUserId, String messageContent) {
        WebSocketMessage response = new WebSocketMessage();
        response.type = WebSocketConstants.ChatType;
        response.data = new ChatMessage(WebSocketConstants.XDice, mentionedUserId, messageContent).toJsonString();
        return response;
    }

    public static WebSocketMessage errorResponseOf(String errorText) {
        WebSocketMessage response = new WebSocketMessage();
        response.type = WebSocketConstants.ErrorType;
        response.data = new Error(errorText).toJsonString();
        return response;
    }

    public static WebSocketMessage roomJoinedResponseOf(String newRoomCode, User user) {
        WebSocketMessage response = new WebSocketMessage();
        response.type = WebSocketConstants.RoomJoinedType;
        response.data = new JoinRoomResponse(newRoomCode, user).toJsonString();
        return response;
    }

    public static WebSocketMessage roomCreatedResponseOf(String newRoomCode, User owner) {
        WebSocketMessage response = new WebSocketMessage();
        response.type = WebSocketConstants.RoomCreatedType;
        response.data = new CreateRoomResponse(newRoomCode, owner).toJsonString();
        return response;
    }

    public static WebSocketMessage roomDetailsResponseOf(Room room) {
        WebSocketMessage response = new WebSocketMessage();
        response.type = WebSocketConstants.RoomDetailsType;
        response.data = room.toJsonString();
        return response;
    }

    public static WebSocketMessage changeUsernameResponseOf(String userId, String newUsername) {
        WebSocketMessage response = new WebSocketMessage();
        response.type = WebSocketConstants.ChangeUsernameType;
        response.data = new ChangeUsernameResponse(userId, newUsername).toJsonString();
        return response;
    }
}
