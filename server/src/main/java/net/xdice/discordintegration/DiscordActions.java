package net.xdice.discordintegration;

import net.xdice.constants.Constants;
import net.xdice.core.DIContainer;
import net.xdice.models.XDiceConfig;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DiscordActions {
    public static void onLoggedIn(String botUsername, Collection<Server> servers) {
        System.out.println("Logged in as " + botUsername);

        HashSet<String> knownConfigs;
        try {
            knownConfigs = DIContainer.getRepository().getKnownGuilds();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Validate that a config for each ID can be loaded
        for (String guildId: knownConfigs) {
            try {
                DIContainer.getRepository().getConfig(guildId);
                System.out.println("Config found for guild " + guildId);
            } catch (SQLException e) {
                System.err.println("Error loading config for guild " + guildId);
                e.printStackTrace();
            }
        }

        HashSet<Server> connectedServers = new HashSet<>(servers);
        Set<Long> serverIds = connectedServers.stream().map(DiscordEntity::getId).collect(Collectors.toSet());
        serverIds.removeAll(knownConfigs);

        for (long serverId: serverIds) {
            Server matchingServer = connectedServers.stream().filter(server -> server.getId() == serverId).findFirst().orElseThrow();
            onJoinGuild(matchingServer);
        }

        System.out.println("All Configs loaded");
    }

    public static void onJoinGuild(Server server) {
        try {
            if (DIContainer.getRepository().getKnownGuilds().contains(server.getId())) {
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error loading known guilds:");
            e.printStackTrace();
            return;
        }

        ServerTextChannel configChannel = server.createTextChannelBuilder().setName(Constants.configChannelName).create().join();
        XDiceConfig newConfig = XDiceConfig.getDefaultConfig(server.getIdAsString());

        try {
            DIContainer.getRepository().saveConfig(newConfig);
            System.out.println("Created new config for " + server.getIdAsString());
        } catch (SQLException e) {
            e.printStackTrace();
            configChannel.sendMessage("ERROR: Could not create new Configuration for this server.");
            return;
        }

        // TODO: Move to consts.
        String welcomeMessage = "Thank you for installing XDice.\n\n"

                + "The " + Constants.configChannelName + " channel has been created to keep your regular chat channels clean. However, all XDice commands will work from any channel, and XDice will respond in the same channel.\n\n"

                + "Right now, XDice is set up in its most basic form. Additional features can be enabled by using Configuration Mode, which can be activated by sending the following message in any channel on this server:\n"
                + "`/xdice config begin`\n\n"

                + "To learn how to use the features which are enabled on this server, send this message on any channel:\n"
                + "`/xdice help`\n\n"

                + "To delete the " + Constants.configChannelName + " channel, send this message:\n"
                + "`/xdice config delete`";

        configChannel.sendMessage(welcomeMessage);
    }
}
