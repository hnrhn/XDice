namespace XDice.Constants
{
    public static class ConfigHelperTemp
    {
        public const string BeginStep = 
            "Hi! I'm the XDice configuration assistant!\n"
            + "I'm going to guide you through the process of setting up XDice for your game.\n\n"
            + "I've been told that humans feel more at ease when their assistants have names, so you can call me `ConfigurationAssistant_Instance_#{0}`, or **Confi** for short.\n\n"
            + "Alright! Now that we've gotten to know each other a little better, let's get on with the configuration, so you can get back to playing your games.\n\n" + "First, I need to know how many sides are on the most common die you and your fellow players will be rolling.\n\n"
            + "For example, if you are playing _Dungeons & Dragons_, you will be rolling a lot of d20s, so I will make that the default die.\n"
            + "That way, when you want to roll a d20, you won't need to say so; XDice will know automatically! Isn't she wonderful?\n\n"
            + "So, to set the default for your game, send a message like so:\n"
            + "`/xdice config {1} X` with X being the number of sides on your favourite dice.\n\n"
            + "For example, if you will roll mostly d20s, the command is:\n"
            + "`/xdice config {1} 20`.\n\n"
            + "Ready when you are!";

        public const string CountSuccessesStep =
            "Great! By default, XDice will roll d{0}s for you. On to the next step!\n\n"
            + "Are you using a system where rolling a certain number or higher on your usual die counts as a \"Success\"? XDice can help you out, and automatically count the Successes you roll.\n\n"
            + "For example, if a 7 or higher on a d10 counts as a success for you, then XDice can show you something like this:\n\n"
            + "\t**[8, 7, 5, 3, 1] = 2 Successes**\n\n"
            + "To enable this, send a message of `/xdice config count_successes yes`. To disable it, `/xdice config count_successes no`. Nice and straightforward!\n\n"
            + "So, would you like XDice to count successes for you? _(Note: This will only apply to d{0}s. Other dice will just be rolled and displayed for you.)_";

        public const string SuccessOnStep =
            "Excellent! Of course, to count Successes, XDice needs to know what your game considers a success to be, so let's configure that now.\n\n"
            + "Send a message like this: `/xdice config {0} 10 9 8 1`.\n"
            + "In this example, 8s, 9s, 10s, and..... 1s for some reason, will all count as Successes!\n\n"
            + "This step is a little tricky for poor XDice, so make sure you include every number you want to consider as a success, with **one** space between them.";

        public const string AddTotalStep =
            "Alrighty, no success-counting needed! But what about adding up the total of all of your dice in a roll? Is that something we can interest you in?\n\n"
            + "`/xdice config {0} yes` if you'd like that, or `/xdice config {0} no` if you want XDice to show you what you rolled and nothing more!";

        public const string PlusBehaviourStep =
            "Our next configuration step is multiple-choice. XDice can handle inputs like **XdY + Z**. What we're going to set up now is what she should do with that **+ Z** part. Here are your options:\n\n"
            + "1) Nothing. Ignore it entirely!\n"
            + "{1}2) Add Z on to the total of your rolls. e.g. `/roll 2d10 + 5 -> [10, 10] = 25`{1}\n"
            + "{2}3) Add an automatic success. For instance: `/roll 1d10 + 1 -> [10] = 2 Successes`{2}\n\n"
            + "To choose, send a message\n"
            + "`/xdice config {0} 1`\n"
            + "or\n"
            + "{1}`/xdice config {0} 2`{1}\n"
            + "or\n"
            + "{2}`/xdice config {0} 3`{2}\n"
            + "(and watch out: we were made in Ireland, so there's a U in that **behaviour**!)\n\n"
            + "Up to you, boss!";

        public const string ExplodeBehaviourStep =
            "Our next step seems dangerous. \"Exploding Dice\"! In some systems, when you roll a certain number, the die can \"explode\" into copies of itself! XDice likes this one. She can handle that in _four_ different ways:\n\n"
            + "1) No explosions. The gunpowder will be removed from all dice post-haste!\n"
            + "{1}2) Double success. XDice will add an extra success for every explosion, no questions asked{1}\n"
            + "3) Roll again. Every exploding die magically clones itself and rolls again. The copies can't explode, so on the second roll, what you get is what you get.\n"
            + "4) Roll again, with exploding copies. Potentially infinite explosions! Exploding dice create copies, and those copies can explode too.\n\n"
            + "Your magic incantation for choosing the right level of explosions for your game is `/xdice config {0} 1` (or 2 or 3 or 4!)\n\n"
            + "So what's it gonna be?";

        public const string ExplodeOnStep =
            "Got it! And now I just need to tell XDice which dice should explode for you.\n\n"
            + "`/xdice config {0} 10` will explode any 10s. `/xdice config {0} 10 9 8 1` will explode every 10, 9, 8, and 1 (why not?)\n\n"
            + "Careful, XDice is a fussy eater. If more than one number should explode, you need to make sure there's only **one** space between each of the numbers you type.\n\n"
            + "Off you go!";

        public const string CritFailStep =
            "We're getting close to the end now, just one more configuration to set up!\n"
            + "In many Success-based systems, rolling 1s without any successes is a Critical Failure. Do you want XDice to let you know when that happens? It'll look something like this:\n"
            + "**[3,2,1,1] = Critical Fail**\n\n"
            + "`/xdice config {0} yes`\n"
            + "or\n"
            + "`/xdice config {0} no`.\n\n"
            + "One last time, tell me what you need!";

        public const string ConfirmStep =
            "{1}\n"
            + "SPLIT\n\n"
            + "All done! Above is the output of the `/xdice help` command, which gives you a complete overview of the settings you have enabled on your server.\n\n"
            + "Look over it, and if you're happy with everything, send the message `/xdice config {0}`, and you'll be ready to play! If not, send `/xdice config cancel` to shut me down and exit Configuration Mode so you can start over.\n\n"
            + "And remember, if you ever need this help information again, just send XDice the message `/xdice help`.\n\n"
            + "Are you happy with this configuration?\n"
            + "`/xdice config {0}` to save and exit configuration mode.\n"
            + "or\n"
            + "`/xdice config cancel` to discard all changes and exit configuration mode.";

        public const string Signoff =
            "It's been a pleasure helping you configure my darling XDice. If you ever need me again... well... It's extremely unlikely that it will be _me_ specifically who gets assigned to your case, but one of my several billion siblings will be more than happy to help!\n\n"
            + "Happy rolling!\n\n"
            + "-- Confi";
    }
}