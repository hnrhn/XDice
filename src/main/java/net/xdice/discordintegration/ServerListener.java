package net.xdice.discordintegration;

import net.xdice.DiscordActions;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;

public class ServerListener implements ServerJoinListener {
    @Override
    public void onServerJoin(ServerJoinEvent event) {
        DiscordActions.onJoinGuild(event.getServer());
    }
}
