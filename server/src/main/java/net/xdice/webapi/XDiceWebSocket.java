package net.xdice.webapi;

import net.xdice.behaviour.forbiddenlands.FLRoller;
import net.xdice.core.DIContainer;
import net.xdice.models.XDiceCommand;
import net.xdice.webapi.models.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.common.frames.PingFrame;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.*;

@WebSocket
public class XDiceWebSocket {
    private static final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, SessionToUserLink> sessionToUserLinks = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @OnWebSocketConnect
    public void onSocketConnected(Session session) {
        sessionToUserLinks.put(session.getRemoteAddress().toString(), new SessionToUserLink(null, null));

        // Keep connection alive
        executorService.scheduleAtFixedRate(() -> {
            try {
                session.getRemote().sendPing(new PingFrame().getPayload());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 1, 1L, TimeUnit.MINUTES);
    }

    @OnWebSocketClose
    public void onSocketClosed(Session session, int statusCode, String reason) {
        System.out.println("Closed: " + session.getRemoteAddress() + " " + statusCode + " " + reason);
        var sessionId = session.getRemoteAddress().toString();
        var link = sessionToUserLinks.get(sessionId);
        var room = rooms.get(link.getRoomCode());

        room.deleteSession(session);
        room.deactivateUser(link.getUserId());
        sessionToUserLinks.remove(sessionId);

        if (room.getUsers().stream().allMatch(User::getIsHidden)) {
            rooms.remove(link.getRoomCode());
        }

        broadcastNewRoomDetailsToAllRoomMembers(room);
    }

    @OnWebSocketMessage
    public void onMessageReceived(Session session, String messageString) throws IOException {
        try {
            var webSocketMessage = WebSocketMessage.fromJsonString(messageString);
            assert webSocketMessage != null;

            switch(webSocketMessage.getType()) {
                case WebSocketConstants.CreateRoomRequestType -> createNewRoom(session, webSocketMessage);
                case WebSocketConstants.ChangeUsernameType -> changeUsername(session, webSocketMessage);
                case WebSocketConstants.JoinRoomType -> joinUserToRoom(session, webSocketMessage);
                case WebSocketConstants.ChatType -> {
                    SessionToUserLink link = sessionToUserLinks.get(session.getRemoteAddress().toString());
                    Room room = rooms.get(link.getRoomCode());

                    // First send the user's message to every client
                    room.getSessions().parallelStream().forEach(s -> {
                        try {
                            s.getRemote().sendString(messageString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    handleChatMessage(room, link.getUserId(), webSocketMessage);
                }
                default -> throw new IllegalStateException("Unexpected message type: " + webSocketMessage.getType());
            }

        } catch (Exception e) {
            // Uncaught exceptions close the Socket, this is simply to keep the connection open
            // TODO: Log errors better
            e.printStackTrace();
            session.getRemote().sendString(WebSocketMessage.errorResponseOf("There was an error in your request").toJsonString());
        }
    }

    private void handleChatMessage(Room room, String userId, WebSocketMessage convertedMessage) throws SQLException, IOException {
        // TODO: Combine with similar implementation for Discord
        var diContainer = new DIContainer(room.getRoomCode(), userId);
        ChatMessage request = ChatMessage.fromJsonString(convertedMessage.getData());
        request.setAuthorId(userId);  // TODO: make this unnecessary

        String responsePrefix = null;
        XDiceCommand command = diContainer.getParser().parseCommandString(request.getMessageContent(), diContainer.getConfig());
        String response = switch (command.getCommandType()) {
            case CONFIG, INVALID -> null;
            case HELP -> diContainer.getHelpGenerator().getHelp(diContainer.getConfig());
            case DICE -> {
                responsePrefix = userId;
                yield diContainer.getDiceRoller().rollDice(command, diContainer.getConfig());
            }
            case ROCK_PAPER_SCISSORS -> {
                responsePrefix = userId;
                yield diContainer.getDiceRoller().rockPaperScissors();
            }
            case COIN -> {
                responsePrefix = userId;
                yield diContainer.getDiceRoller().flipCoin();
            }
            case WTF -> diContainer.getAi().randomWtfResponse();
            case THANKS -> diContainer.getAi().randomThanksResponse();
            case INSULT -> diContainer.getAi().randomInsultResponse();
            case LOVE -> diContainer.getAi().randomLoveResponse();
            case FL_ROLL -> {
                responsePrefix = userId;
                try {
                    yield ((FLRoller) diContainer.getDiceRoller()).flRoll(command.getForbiddenLandsDice(), DIContainer.getRepository(), diContainer.getConfigId() + diContainer.getUserId());
                } catch (SQLException e) {
                    e.printStackTrace();
                    yield "ERROR";
                }
            }
            case FL_PUSH -> {
                responsePrefix = userId;
                try {
                    yield ((FLRoller) diContainer.getDiceRoller()).flPush(diContainer.getConfigId() + diContainer.getUserId(), DIContainer.getRepository(), false);
                } catch (SQLException e) {
                    e.printStackTrace();
                    yield "ERROR";
                }
            }
            case FL_PRIDE -> {
                responsePrefix = userId;
                try {
                    yield ((FLRoller) diContainer.getDiceRoller()).flPush(diContainer.getConfigId() + diContainer.getUserId(), DIContainer.getRepository(), true);
                } catch (SQLException e) {
                    e.printStackTrace();
                    yield "ERROR";
                }
            }
            case FL_CREATE_NEW_DECK -> {
                try {
                    yield ((FLRoller) diContainer.getDiceRoller()).flNewDeck(diContainer.getConfigId(), DIContainer.getRepository());
                } catch (SQLException e) {
                    e.printStackTrace();
                    yield "ERROR";
                }
            }
            case FL_DRAW_INITIATIVE -> {
                responsePrefix = userId;
                try {
                    yield ((FLRoller) diContainer.getDiceRoller()).flDrawInitiative(diContainer.getConfigId(), DIContainer.getRepository());
                } catch (SQLException e) {
                    e.printStackTrace();
                    yield "ERROR";
                }
            }
        };

        if (response != null) {
            WebSocketMessage wsOutput = WebSocketMessage.chatResponseOf(responsePrefix, response);

            room.getSessions().parallelStream().forEach(s -> {
                try {
                    s.getRemote().sendString(wsOutput.toJsonString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void changeUsername(Session session, WebSocketMessage convertedMessage) throws IOException {
        var currentRoom = rooms.get(sessionToUserLinks.get(session.getRemoteAddress().toString()).getRoomCode());
        var request = ChangeUsernameRequest.fromJsonString(convertedMessage.getData());

        // Prevent display name clashes
        if (currentRoom.getUsers().stream().anyMatch(u -> u.getUsername().equals(request.getNewUsername()))) {
            return;
        }

        String userId = sessionToUserLinks.get(session.getRemoteAddress().toString()).getUserId();

        // Rename the user
        currentRoom.renameUser(userId, request.getNewUsername());

        WebSocketMessage message = WebSocketMessage.changeUsernameResponseOf(userId, request.getNewUsername());

        session.getRemote().sendString(message.toJsonString());

        broadcastNewRoomDetailsToAllRoomMembers(currentRoom);
    }

    private void joinUserToRoom(Session session, WebSocketMessage message) throws IOException {
        JoinRoomRequest request = JoinRoomRequest.fromJsonString(message.getData());
        Room roomToJoin = rooms.get(request.getRoomCode());

        if (roomToJoin == null) {
            try {
                session.getRemote().sendString(WebSocketMessage.errorResponseOf("Invalid room code or password.").toJsonString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (roomToJoin.getRoomPassword() != null && !request.getPassword().equals(roomToJoin.getRoomPassword())) {
            try {
                session.getRemote().sendString(WebSocketMessage.errorResponseOf("Invalid room code or password.").toJsonString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        User newUser = new User(UUID.randomUUID().toString(), request.getUsername(), false);

        roomToJoin.addUserIfNotExists(newUser);
        roomToJoin.addSessionIfNotExists(session);

        SessionToUserLink link = sessionToUserLinks.get(session.getRemoteAddress().toString());
        link.setRoomCode(roomToJoin.getRoomCode());
        link.setUserId(newUser.getUserId());

        session.getRemote().sendString(WebSocketMessage.roomJoinedResponseOf(roomToJoin.getRoomCode(), newUser).toJsonString());
        broadcastNewRoomDetailsToAllRoomMembers(roomToJoin);
    }

    private void broadcastNewRoomDetailsToAllRoomMembers(Room currentRoom) {
        WebSocketMessage message = WebSocketMessage.roomDetailsResponseOf(currentRoom);

        currentRoom.getSessions().parallelStream().forEach(s -> {
            try {
                s.getRemote().sendString(message.toJsonString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void createNewRoom(Session session, WebSocketMessage newRoomRequest) throws IOException {
        var request = CreateRoomRequest.fromJsonString(newRoomRequest.getData());

        var newRoom = new Room();
        var owner = new User(UUID.randomUUID().toString(), request.getOwnerName(), true);

        newRoom.setOwnerId(owner.getUserId());
        newRoom.addUserIfNotExists(owner);
        newRoom.addSessionIfNotExists(session);

        String password = request.getPassword();
        if (password != null && !password.equals("")){
            newRoom.setRoomPassword(password);
        }

        boolean validRoomCodeCreated = false;
        String newRoomCode = "";
        while (!validRoomCodeCreated) {
            newRoomCode = request.getRoomName() + "#" + ThreadLocalRandom.current().nextInt(100, 10000);
            if (!rooms.containsKey(newRoomCode)){
                validRoomCodeCreated = true;
            }
        }
        newRoom.setRoomCode(newRoomCode);
        rooms.put(newRoomCode, newRoom);

        var link = sessionToUserLinks.get(session.getRemoteAddress().toString());
        link.setRoomCode(newRoom.getRoomCode());
        link.setUserId(owner.getUserId());

        try {
            session.getRemote().sendString(WebSocketMessage.roomCreatedResponseOf(newRoomCode, owner).toJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        broadcastNewRoomDetailsToAllRoomMembers(newRoom);
    }
}
