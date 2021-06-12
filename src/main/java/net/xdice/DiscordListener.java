package net.xdice;

import net.xdice.models.XDiceCommand;
import net.xdice.models.XDiceConfig;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class DiscordListener {
    public static void main(String[] args) throws IOException {
        String token = Files.readString(Paths.get("DEV_TOKEN_DO_NOT_COMMIT.txt"));

// region Pseudo-DI
        Connection databaseConnection = null;
        try {
            databaseConnection = DriverManager.getConnection("jdbc:sqlite:prod.db");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        ConfigRepository configRepository = new ConfigRepository(databaseConnection);
        DiscordCallbacks discordStuff = new DiscordCallbacks(configRepository);
        HelpGenerator helpGenerator = new HelpGenerator();
        DiscordConfigurator discordConfigurator = new DiscordConfigurator(configRepository, helpGenerator);
        Roller diceRoller = new Roller();
        ArtificialIntelligence ai = new ArtificialIntelligence();
// endregion

        DiscordApi api = new DiscordApiBuilder()
                .setToken(token)
                .login()
                .join(); // Block this thread until login completed.

        discordStuff.onLoggedIn(api.getYourself().getMentionTag(), api.getServers());

        api.addServerJoinListener(event -> discordStuff.onJoinGuild(event.getServer()));

        api.addMessageCreateListener(event -> {
            // Ignore bot users
            if (event.getMessage().getUserAuthor().orElseThrow().isBot()) {
                return;
            }

            String mention = event.getMessageAuthor().asUser().orElseThrow().getMentionTag() + ": ";
            String responsePrefix = "";

            // TODO: Validate that XDice should respond to incoming message before loading Config.
            XDiceConfig config;
            try {
                config = configRepository.getConfig(event.getServer().orElseThrow().getId());
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            XDiceCommand command = Parser.parseCommandString(event.getMessageContent(), config);
            String response = switch (command.getCommandType()){
                case CONFIG -> discordConfigurator.configure(event.getMessage());
                case HELP -> helpGenerator.getHelp(config);
                case DICE -> {
                    responsePrefix = mention;
                    yield diceRoller.rollDice(command, config);
                }
                case ROCK_PAPER_SCISSORS -> {
                    responsePrefix = mention;
                    yield diceRoller.rockPaperScissors();
                }
                case COIN -> {
                    responsePrefix = mention;
                    yield diceRoller.flipCoin();
                }
                case WTF -> {
                    dramaticPause();
                    yield ai.randomWtfResponse();
                }
                case THANKS -> {
                    dramaticPause();
                    yield ai.randomThanksResponse();
                }
                case INSULT -> {
                    dramaticPause();
                    yield ai.randomInsultResponse();
                }
                case LOVE -> {
                    dramaticPause();
                    yield ai.randomLoveResponse();
                }
                case INVALID -> null;
            };

            if (response == null) {
                return;
            }

            for (String messagePart: response.split("SPLIT")) {
                event.getChannel().sendMessage(responsePrefix + messagePart);
            }
        });
    }

    private static void dramaticPause() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
