package net.xdice.behaviour.forbiddenlands;

import net.xdice.constants.Constants;
import net.xdice.interfaces.HelpGenerator;
import net.xdice.models.XDiceConfig;

import java.util.ArrayList;

public class FLHelpGenerator implements HelpGenerator {
    @Override
    public String getHelp(XDiceConfig config) {
        ArrayList<String> help = new ArrayList<>();
        help.add("**= XDice v" + Constants.xDiceVersion + " - [Forbidden Lands Edition] =**");
        help.add("------------------------------------------------------------------------------------------------------------------------\n");
        help.add("_XDice - Forbidden Lands Edition_ listens for the following activation phrases:\n");
        help.add("**Forbidden Lands Roll**");
        help.add("`/r BASE SKILL GEAR [ARTIFACT]`");
        help.add("    - BASE, SKILL, and GEAR are mandatory. If you wish to roll 0, you must enter that as a value.");
        help.add("    - ARTIFACT is optional, and can be one of the following options:");
        help.add("        - `m` or `8` or `d8`");
        help.add("        - `e` or `10` or `d10`");
        help.add("        - `l` or `12` or `d12`");
        help.add("    If ARTIFACT is excluded or is anything other than the above options, it defaults to 0");
        help.add("Example: `/r 3 0 2` - Rolls 3 Base dice, 0 Skill dice, 2 Gear dice, and 0 Artifact dice.");
        help.add("Example: `/r 1 2 3 l` - Rolls 1 Base die, 2 Skill dice, 3 Gear dice, and a Legendary artifact die (d12)\n");
        help.add("**Push**");
        help.add("`/push`");
        help.add("Re-rolls any dice from your previous roll which were not :crossed_swords: or :skull: and outputs the new total roll.\n");
        help.add("**Pride**");
        help.add("`/pride`");
        help.add("Works like **Push**, but also adds your Pride die (d12) to the new roll\n");
        help.add("**Initiative Deck**");
        help.add("`/newdeck`");
        help.add("Creates and shuffles a new deck of ten Initiative cards numbered 1-10.");
        help.add("`/draw`");
        help.add("Draw one of the cards from the Initiative Deck.\n");
        help.add("**Other dice types**");
        help.add("To roll dice outside of the Forbidden Lands format, use the template XdY");
        help.add("Example: `/r 5d20` - this will roll 5 d20s");
        help.add("`/coin`");
        help.add("Output either Heads or Tails\n");
        help.add("`/help`");
        help.add("Output this message again.");
        return String.join("\n", help);
    }
}
