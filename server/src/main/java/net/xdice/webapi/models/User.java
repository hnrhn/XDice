package net.xdice.webapi.models;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class User {
    private static final JsonAdapter<User> jsonAdapter = new Moshi.Builder().build().adapter(User.class);

    private String userId;
    private String username;
    private boolean isOwner;
    private boolean isHidden;

    public User(String userId, String username, boolean isOwner) {
        this.userId = userId;
        this.username = username;
        this.isOwner = isOwner;
        this.isHidden = false;
    }

    public User(String userId, String username, boolean isOwner, boolean isHidden) {
        this.userId = userId;
        this.username = username;
        this.isOwner = isOwner;
        this.isHidden = isHidden;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOwner() {
        return this.isOwner;
    }

    public boolean getIsHidden() {
        return this.isHidden;
    }

    public void setOwner(boolean owner) {
        this.isOwner = owner;
    }

    public void setIsHidden(boolean hiddenStatus) {
        this.isHidden = hiddenStatus;
    }

    public static User fromJsonString(String jsonString) throws IOException {
        return jsonAdapter.fromJson(jsonString);
    }

    public String toJsonString() {
        return jsonAdapter.toJson(this);
    }
}
