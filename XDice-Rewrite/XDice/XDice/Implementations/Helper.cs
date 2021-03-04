using System;
using System.Collections.Generic;
using System.Linq;
using XDice.Constants;
using XDice.Enums;
using XDice.Models;

namespace XDice.Implementations
{
    public static class Helper
    {
        public static string GetHelp(SimpleConfig config)
        {
            var help = new List<string>()
            {
                $"**= XDice v{GeneralConstants.XDiceVersion} =**",
                "------------------------------------------------------------------------------------------------------------------------",
                "",
                "**BASIC USAGE**",
                "XDice listens for messages beginning with one of the activation phrases",
                "`/roll`",
                "or",
                "`!roll`",
                "then a \"dice request\" in this format:",
                "`XdY + Z`",
                "",
                "Therefore a full \"roll command\" looks something like this:",
                "`/roll 3d12 + 5`",
                "or",
                "`!roll 3d12 + 5`",
                "",
                "There is also a special roll command for randomly generating Rock/Paper/Scissors results:",
                "`/rps` or `!rps`",
                "SPLIT",
                "------------------------------------------------------------------------------------------------------------------------",
                "",
                "**SHORTCUTS**",
                "Not every part of the roll command is required. XDice on your server has been configured with default values for X, Y, and Z to help you type less:",
                "```",
                "X = 1",
                $"Y = {config.DefaultDice}",
                $"Z = {(config.PlusBehaviour == PlusBehaviour.Ignore ? "N/A -- (Z is ignored on your server)" : "0")}",
                "```",
                "Spaces between the X, Y, and Z elements are optional, as are the letters **o-l-l** in the word \"roll\".",
                "i.e. `!r` and `/r` work exactly the same as `!roll` and `/roll`",
                "",
                "So for example, in this server `/r5` will roll 5d{config.default_dice}s, `!r d8` will roll 1d8.",
                "The smallest possible roll command is just two characters long:",
                $"`/r` or `!r` -> This will roll 1d{config.DefaultDice}."
            };

            if (config.PlusBehaviour != PlusBehaviour.Ignore)
            {
                var behaviour = config.PlusBehaviour == PlusBehaviour.AutoSuccess
                    ? "add an additional success to each roll result"
                    : "add the value of Z to the total of your rolled dice";
                help.Add($"\nFor your server, the number specified in the `Z` position will {behaviour}");
            }

            var extraSettingsPosition = help.Count;
            help.Add("PLACEHOLDER");

            var extraSettings = false;
            if (config.CountSuccessesModeActive)
            {
                extraSettings = true;
                var rand = new Random();
                var shuffled = config.SuccessOn.ToList().OrderBy(_ => rand.Next());
                help.Add("- Automatically count successes when any of the following numbers are rolled: {str(config.success_on)}");
                help.Add($"\t**{shuffled} = {config.SuccessOn.Count} Successes**");
            }
            else if (config.AddTotalModeActive)
            {
                extraSettings = true;
                var sample = new int[3];
                for (var i = 0; i < 3; i++)
                {
                    sample[i] = config.DefaultDice - i;
                }
                help.Add("\n- Automatically calculate the total of all of your rolls:");
                help.Add($"\t**{sample} = {sample.Sum()}");
            }

            if (config.ExplodeBehaviour != ExplodeBehaviour.None)
            {
                extraSettings = true;
                help.Add("\n");
                help.Add($"- \"Explode\" dice on rolls of these numbers: {config.ExplodeOn}");
                switch (config.ExplodeBehaviour)
                {
                    case ExplodeBehaviour.Double:
                        help.Add("\t- Each exploded die will add an extra success to the Successes counter.");
                        break;
                    case ExplodeBehaviour.Extra:
                        help.Add("\t- Each exploded die will roll an extra die of the same type and add the result to the list of rolled numbers. Extra dice do not explode.");
                        break;
                    case ExplodeBehaviour.ExtraWithChaining:
                        help.Add("\t- Each exploded die will roll an extra die of the same type and add the result to the list of rolled numbers. Extra dice can also explode.");
                        break;
                    case ExplodeBehaviour.None:
                    default:
                        throw new ArgumentOutOfRangeException();
                }
            }

            if (config.CritFailBehaviour == CritFailBehaviour.OnesWithNoSuccesses)
            {
                extraSettings = true;
                help.Add("- Add a Critical Fail warning when you roll at least one 1 and no Successes:");
                help.Add("\t**[1, 1, 1] = Critical Fail**");
            }

            if (extraSettings)
            {
                help[extraSettingsPosition] = $"SPLIT\n------------------------------------------------------------------------------------------------------------------------\n\n**SPECIAL SETTINGS**\nFor your server, XDice has also been configured to do the following when you roll d{config.DefaultDice}s:\n";
            }
            else
            {
                help.RemoveAt(extraSettingsPosition);
            }

            return string.Join("\n", help);
        }
    }
}