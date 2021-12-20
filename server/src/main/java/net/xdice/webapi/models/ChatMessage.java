package net.xdice.webapi.models;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class ChatMessage {
    private static final JsonAdapter<ChatMessage> jsonAdapter = new Moshi.Builder().build().adapter(ChatMessage.class);

    String authorId;
    String mentionedUserId;
    String messageContent;
    Long timestamp;
    String key;

    public ChatMessage(String authorId, String mentionedUserId, String messageContent) {
        this.authorId = authorId;
        this.mentionedUserId = mentionedUserId;
        this.messageContent = messageContent;
    }

    public String toJsonString() {
        return jsonAdapter.toJson(this);
    }

    public static ChatMessage fromJsonString(String jsonString) throws IOException {
        return jsonAdapter.fromJson(jsonString);
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getMentionedUserId() {
        return mentionedUserId;
    }

    public String getMessageContent() {
        return messageContent;
    }
}
