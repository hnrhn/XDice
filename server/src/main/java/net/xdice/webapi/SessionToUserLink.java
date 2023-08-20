package net.xdice.webapi;

public class SessionToUserLink {
    private String userId = null;
    private String roomId = null;

    public SessionToUserLink(String userId, String roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomCode() {
        return roomId;
    }

    public void setRoomCode(String room) {
        this.roomId = room;
    }
}
