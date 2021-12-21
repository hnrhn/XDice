package net.xdice.webapi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.xdice.webapi.models.User;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Room {
    private static final JsonAdapter<Room> jsonAdapter = new Moshi.Builder().build().adapter(Room.class);
    private static final User xDiceUser = new User("XDice", "XDice", false, true);

    private String roomCode;
    private String ownerId;
    private String roomPassword = null;
    private final List<User> users = new ArrayList<>();   // TODO: Concurrent Collections

    private transient final List<Session> sessions = new ArrayList<>();

    public Room() {
        users.add(xDiceUser);
    }

    public void addUserIfNotExists(User newUser) {
        if (this.users.stream().noneMatch(u -> u.getUserId().equals(newUser.getUserId()))) {
            this.users.add(newUser);
            users.sort(Comparator.comparing(User::getUsername));
        }
    }

    public void deactivateUser(String userId) {
        User toRemove = users.stream().filter(u -> u.getUserId().equals(userId)).findFirst().orElse(null);

        if (toRemove == null) {
            return;
        }

        toRemove.setIsHidden(true);
    }

    public String getRoomCode() {
        return this.roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(String owner) {
        this.ownerId = owner;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void addSessionIfNotExists(Session session) {
        if (!this.sessions.contains(session)) {
            this.sessions.add(session);
        }
    }

    public void deleteSession(Session sessionToDelete) {
        sessions.remove(sessionToDelete);
    }

    public boolean isEmpty() {
        return this.users.isEmpty();
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public void setRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }

    public void renameUser(String userId, String newUsername) {
        User userToUpdate = this.users.stream().filter(u -> u.getUserId().equals(userId)).findFirst().orElse(null);
        if (userToUpdate != null) {
            userToUpdate.setUsername(newUsername);
        }
    }

    public String toJsonString() {
        return jsonAdapter.toJson(this);
    }
}
