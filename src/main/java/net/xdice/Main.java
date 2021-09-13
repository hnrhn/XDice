package net.xdice;

import net.xdice.discordintegration.MessageListener;
import net.xdice.discordintegration.ServerListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        String token = Files.readString(Paths.get("DEV_TOKEN_DO_NOT_COMMIT.txt"));

        DiscordApi api = new DiscordApiBuilder()
                .setToken(token)
                .login()
                .join(); // Block this thread until login completed.

        DiscordActions.onLoggedIn(api.getYourself().getMentionTag(), api.getServers());

        api.addServerJoinListener(new ServerListener());
        api.addMessageCreateListener(new MessageListener());
    }
}
