package net.xdice;

import net.xdice.enums.CritFailBehaviour;
import net.xdice.enums.ExplodeBehaviour;
import net.xdice.enums.PlusBehaviour;
import net.xdice.interfaces.HelpGenerator;
import net.xdice.models.XDiceConfig;

import java.util.ArrayList;
import java.util.Collections;

public class StandardHelpGenerator implements HelpGenerator {
    public String getHelp(XDiceConfig config) {
        ArrayList<String> help = new ArrayList<>();
        help.add("**= XDice v" + Constants.xDiceVersion + "=**");
        help.add("------------------------------------------------------------------------------------------------------------------------\n");
        help.add("**BASIC USAGE**");
        help.add("XDice listens for messages beginning with one of the activation phrases");
        help.add("`/roll` or `!roll`");
        help.add("then a \"dice request\" in this format:");
        help.add("`XdY + Z`");
        help.add("Therefore a full \"roll command\" looks something like this:");
        help.add("`/roll 3d12 + 5`");
        help.add("or");
        help.add("`!roll 3d12 + 5`\n");
        help.add("There is also a special roll command for randomly generating Rock/Paper/Scissors results:");
        help.add("`/rps` or `!rps`");
        help.add("and another special command for flipping a coin:");
        help.add("`/coin`");
        help.add("SPLIT");
        help.add("------------------------------------------------------------------------------------------------------------------------\n");
        help.add("**SHORTCUTS**");
        help.add("Not every part of the roll command is required. XDice on your server has been configured with default values for X, Y, and Z to help you type less:");
        help.add("```");
        help.add("X = 1");
        help.add("Y = " + config.getDefaultDice());
        help.add("Z = " + (config.getPlusBehaviour() == PlusBehaviour.IGNORE ? "N/A -- (Z is ignored on your server)" : "0"));
        help.add("```");
        help.add("Spaces between the X, Y, and Z elements are optional, as are the letters **oll** in the word \"roll\".");
        help.add("i.e. `!r` and `/r` work exactly the same as `!roll` and `/roll`\n");
        help.add("So for example, in this server `/r5` will roll 5d" + config.getDefaultDice() + "s, `!r d8` will roll 1d8.");
        help.add("The smallest possible roll command is just two characters long:");
        help.add("`/r` or `!r` -> This will roll 1d" + config.getDefaultDice() + ".");

        if (config.getPlusBehaviour() != PlusBehaviour.IGNORE) {
            String behaviour = config.getPlusBehaviour() == PlusBehaviour.AUTO_SUCCESS
                    ? "add an additional success to each roll result"
                    : "add the value of Z to the total of your rolled dice";
            help.add("\nFor your server, the number specified in the `Z` position will " + behaviour);
        }

        int extraSettingsPosition = help.size();
        help.add("IF YOU ARE SEEING THIS LINE, THE DEVELOPER MESSED UP");

        boolean extraSettings = false;

        if (config.isCountSuccesses()) {
            extraSettings = true;
            ArrayList<Integer> shuffled = new ArrayList<>(config.getSuccessOn());
            Collections.shuffle(shuffled);

            help.add("- Automatically count successes when any of the following numbers are rolled: " + config.getSuccessOn().toString());
            help.add("    **" + shuffled + " = " + config.getSuccessOn().size() + " Successes**");
        } else if (config.isAddTotal()) {
            extraSettings = true;
            ArrayList<Integer> sample = new ArrayList<>();
            for (int i = config.getDefaultDice(); i >= config.getDefaultDice() - 3; i--) {
                sample.add(i);
            }
            help.add("\n- Automatically calculate the total of all of your rolls:");
            help.add("    **" + sample + " = " + sample.stream().mapToInt(Integer::intValue).sum() + "**");
        }

        if (config.getExplodeBehaviour() != ExplodeBehaviour.NONE) {
            extraSettings = true;
            help.add("\n- \"Explode\" dice on rolls of the following numbers: " + config.getExplodeOn().toString());
            switch (config.getExplodeBehaviour()) {
                case DOUBLE -> help.add("    - Each exploded die will add an extra success to the Successes counter.");
                case EXTRA -> help.add("    - Each exploded die will roll an extra die of the same type and add the result to the list of rolled numbers. Extra dice do not explode.");
                case EXTRA_CHAIN -> help.add("    - Each exploded die will roll an extra die of the same type and add the result to the list of rolled numbers. Extra dice can also explode.");
            }
        }

        if (config.getCritFailBehaviour() == CritFailBehaviour.ONE_NO_SUCCESSES) {
            extraSettings = true;
            help.add("\n- Add a Critical Fail warning when you roll at least one 1 and no Successes:");
            help.add("    **[1, 1, 1] = 3 Critical Fails**");
        }

        if (extraSettings) {
            help.set(extraSettingsPosition, "SPLIT\n------------------------------------------------------------------------------------------------------------------------\n\n**SPECIAL SETTINGS**\nFor your server, XDice has also been configured to do the following when you roll d" + config.getDefaultDice() + "s");
        } else {
            help.remove(extraSettingsPosition);
        }

        return String.join("\n", help);
    }
}
