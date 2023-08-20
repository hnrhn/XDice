package net.xdice;

import net.xdice.webapi.WebListener;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        String token = Files.readString(Paths.get("DEV_TOKEN_DO_NOT_COMMIT.txt"));
//
//        DiscordApi api = new DiscordApiBuilder()
//                .setToken(token)
//                .login()
//                .join(); // Block this thread until login completed.
//
//        DiscordActions.onLoggedIn(api.getYourself().getMentionTag(), api.getServers());
//
//        api.addServerJoinListener(new ServerListener());
//        api.addMessageCreateListener(new MessageListener());

        // Create REST API:
        new WebListener();
    }
}
