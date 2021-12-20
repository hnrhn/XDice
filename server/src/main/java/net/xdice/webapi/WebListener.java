package net.xdice.webapi;

import static spark.Spark.*;

public class WebListener {
    public WebListener() {
        externalStaticFileLocation("C:\\Users\\Peter\\Development\\XDice\\web");    // TODO: Replace this with relative paths.
        webSocket("/ws", XDiceWebSocket.class);
        post("/createRoom", (request, response) -> "OK");
        post("/joinRoom/:roomId/:userId", (request, response) -> "OK");
        post("/leaveRoom/:roomId/:userId", (request, response) -> "OK");
        post("/configureRoom/:roomId", (request, response) -> "OK");
        delete("/deleteRoom", (request, response) -> "OK");
    }
}
