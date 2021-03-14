package com.hnrhn.xdice;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DiscordListener {
    public static void main(String[] args) throws IOException {
        var token = Files.readString(Paths.get("DEV_TOKEN_DO_NOT_COMMIT.txt"));

        DiscordApi api = new DiscordApiBuilder()
                .setToken(token)
                .login()
                .join(); // Block this thread until login completed.

        ProofOfConcept.loggedIn(api.getYourself().getName(), api.getServers());

        api.addServerJoinListener(event -> ProofOfConcept.joinGuild(event.getServer()));
        api.addMessageCreateListener(event -> {
            if (event.getMessage().getUserAuthor().orElseThrow().isBot()) {
                return;
            }
            ProofOfConcept.onMessage(event.getMessage());
        });
    }
}
