package net.xdice.discordintegration;

import net.xdice.DIContainer;
import net.xdice.forbiddenlands.FLRoller;
import net.xdice.models.XDiceCommand;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class MessageListener implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        DIContainer dependencies;
        try {
            dependencies = new DIContainer(event.getServer().orElseThrow().getId(), event.getMessageAuthor().getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return;     // TODO: return message to user
        }

        String mention = event.getMessageAuthor().asUser().orElseThrow().getMentionTag() + ": ";
        String responsePrefix = "";

        XDiceCommand command = dependencies.getParser().parseCommandString(event.getMessageContent(), dependencies.getConfig());
        String response = switch (command.getCommandType()) {
            case CONFIG -> dependencies.getDiscordConfigurator().configure(event.getMessage());
            case HELP -> dependencies.getHelpGenerator().getHelp(dependencies.getConfig());
            case DICE -> {
                responsePrefix = mention;
                yield dependencies.getDiceRoller().rollDice(command, dependencies.getConfig());
            }
            case ROCK_PAPER_SCISSORS -> {
                responsePrefix = mention;
                yield dependencies.getDiceRoller().rockPaperScissors();
            }
            case COIN -> {
                responsePrefix = mention;
                yield dependencies.getDiceRoller().flipCoin();
            }
            case WTF -> {
                dramaticPause();
                yield dependencies.getAi().randomWtfResponse();
            }
            case THANKS -> {
                dramaticPause();
                yield dependencies.getAi().randomThanksResponse();
            }
            case INSULT -> {
                dramaticPause();
                yield dependencies.getAi().randomInsultResponse();
            }
            case LOVE -> {
                dramaticPause();
                yield dependencies.getAi().randomLoveResponse();
            }
            case FL_ROLL -> {
                responsePrefix = mention;
                try {
                    yield ((FLRoller)dependencies.getDiceRoller()).flRoll(command.getFallenKingdomsDice(), DIContainer.getRepository(), Long.toString(dependencies.getConfigId()) + dependencies.getUserId());
                } catch (SQLException e) {
                    e.printStackTrace();
                    yield "ERROR";
                }
            }
            case FL_PUSH -> {
                responsePrefix = mention;
                try {
                    yield ((FLRoller)dependencies.getDiceRoller()).flPush(Long.toString(dependencies.getConfigId()) + dependencies.getUserId(), DIContainer.getRepository(), false);
                } catch (SQLException e) {
                    e.printStackTrace();
                    yield "ERROR";
                }
            }
            case FL_PRIDE -> {
                responsePrefix = mention;
                try {
                    yield ((FLRoller)dependencies.getDiceRoller()).flPush(Long.toString(dependencies.getConfigId()) + dependencies.getUserId(), DIContainer.getRepository(), true);
                } catch (SQLException e) {
                    e.printStackTrace();
                    yield "ERROR";
                }
            }
            case FL_CREATE_NEW_DECK -> {
                responsePrefix = mention;
                try {
                    yield ((FLRoller)dependencies.getDiceRoller()).flNewDeck(dependencies.getConfigId(), DIContainer.getRepository());
                } catch (SQLException e) {
                    e.printStackTrace();
                    yield "ERROR";
                }
            }
            case FL_DRAW_INITIATIVE -> {
                responsePrefix = mention;
                try {
                    yield ((FLRoller)dependencies.getDiceRoller()).flDrawInitiative(dependencies.getConfigId(), DIContainer.getRepository());
                } catch (SQLException e) {
                    e.printStackTrace();
                    yield "ERROR";
                }
            }
            case INVALID -> null;
        };

        if (response == null) {
            return;
        }

        for (String messagePart : response.split("SPLIT")) {
            event.getChannel().sendMessage(responsePrefix + messagePart);
        }
    }

    private static void dramaticPause() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
