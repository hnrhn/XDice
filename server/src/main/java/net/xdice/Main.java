package net.xdice;

import net.xdice.discordintegration.DiscordActions;
import net.xdice.discordintegration.MessageListener;
import net.xdice.discordintegration.ServerListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;

public class Main {
    public static void main(String[] args) {
        Environment env = Environment.getInstance();

        DiscordApi api = new DiscordApiBuilder()
                .setToken(env.getDiscordToken())
                .addIntents(Intent.MESSAGE_CONTENT)
                .login()
                .join(); // Block this thread until login completed.

        DiscordActions.onLoggedIn(api.getYourself().getMentionTag(), api.getServers());

        api.addServerJoinListener(new ServerListener());
        api.addMessageCreateListener(new MessageListener());
    }
}
