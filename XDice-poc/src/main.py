import random
import discord
import math
import re
import time
import json
import os
from dataclasses import dataclass
from enum import Enum
from typing import Optional


# region Constants
xdice_version = "1.0.0"


# region Enums
class PlusBehaviour(Enum):
    IGNORE = 1
    ADD = 2
    AUTO_SUCCESS = 3


class ExplodeBehaviour(Enum):
    NONE = 1
    DOUBLE = 2
    EXTRA = 3
    EXTRA_CHAIN = 4


class CritFailBehaviour(Enum):
    NONE = 1
    ONE_NO_SUCCESSES = 2


class ConfigStep(Enum):
    BEGIN = 1
    DEFAULT_DICE = 2
    COUNT_SUCCESSES = 3
    SUCCESS_ON = 4
    ADD_TOTAL = 5
    PLUS_BEHAVIOUR = 6
    EXPLODE_BEHAVIOUR = 7
    EXPLODE_ON = 8
    CRIT_FAIL_BEHAVIOUR = 9
    CONFIRM = 10
# endregion


@dataclass
class Config:
    guild_id: int
    config_mode: bool
    current_config_step: ConfigStep
    default_dice: int
    count_successes: bool
    success_on: list[int]
    add_total: bool
    plus_behaviour: PlusBehaviour
    explode_behaviour: ExplodeBehaviour
    explode_on: list[int]
    crit_fail_behaviour: CritFailBehaviour

    def save(self):
        json_output = f"""{{
    "configMode": false,
    "currentConfigStep": {self.current_config_step.value},
    "defaultDice": {self.default_dice},
    "countSuccesses": {str(self.count_successes).lower()},
    "successOn": {str(self.success_on)},
    "addTotal": {str(self.add_total).lower()},
    "plusBehaviour": {self.plus_behaviour.value},
    "explodeBehaviour": {self.explode_behaviour.value},
    "explodeOn": {str(self.explode_on)},
    "critFailBehaviour": {self.crit_fail_behaviour.value}
}}
"""
        with open(f"configs/{self.guild_id}.json", "w+") as f:
            f.write(json_output)


# region Commands
wtf_command = "dicebot, wtf?"
config_command = "/xdice config"
help_command = "/xdice help"
# endregion


# region Fun
insults = [
    "Suck it, n00b",
    "git gud",
    "I'm a robot, what do you want from me? Blame Peter.",
    "hahahahahaha",
    "- Y O U   H A V E   D I E D -",
    "Wasted",
    "What a goal!",
    "gg;ez",
    "hax",
    "Salty, much?",
    "You fight like a dairy farmer.",
    "You have died of dysentry",
    "Sounds like something a Bastion main would say",
    "Filthy casual",
    "Get rekt",
    "u mad?",
    "Scrub."
]
# endregion

# region Text Templates
config_helper_begin = """Hi! I'm the XDice configuration assistant!
I'm going to guide you through the process of setting up XDice for your game.

I've been told that humans feel more at ease when their assistants have names, so you can call me `ConfigurationAssistant_Instance_#{0}`, or **Confi** for short.

Alright! Now that we've gotten to know each other a little better, let's get on with the configuration, so you can get back to playing your games.

First, I need to know how many sides are on the most common die you and your fellow players will be rolling.

For example, if you are playing _Dungeons & Dragons_, you will be rolling a lot of d20s, so I will make that the default die.
That way, when you want to roll a d20, you won't need to say so; XDice will know automatically! Isn't she wonderful?

So, to set the default for your game, send a message like so:
`/xdice config {1} X` with X being the number of sides on your favourite dice.

For example, if you will roll mostly d20s, the command is:
`/xdice config {1} 20`.

Ready when you are!"""

config_helper_count_successes = """Great! By default, XDice will roll d{0}s for you. On to the next step!

Are you using a system where rolling a certain number or higher on your usual die counts as a "Success"? XDice can help you out, and automatically count the Successes you roll.

For example, if a 7 or higher on a d10 counts as a success for you, then XDice can show you something like this:

    **[8, 7, 5, 3, 1] = 2 Successes**

To enable this, send a message of `/xdice config count_successes yes`. To disable it, `/xdice config count_successes no`. Nice and straightforward!

So, would you like XDice to count successes for you? _(Note: This will only apply to d{0}s. Other dice will just be rolled and displayed for you.)_"""

config_helper_success_on = """Excellent! Of course, to count Successes, XDice needs to know what your game considers a success to be, so let's configure that now.

Send a message like this: `/xdice config {0} 10 9 8 1`.
In this example, 8s, 9s, 10s, and..... 1s for some reason, will all count as Successes!
                
This step is a little tricky for poor XDice, so make sure you include every number you want to consider as a success, with **one** space between them."""

config_helper_add_total = """Alrighty, no success-counting needed! But what about adding up the total of all of your dice in a roll? Is that something we can interest you in?

`/xdice config {0} yes` if you'd like that, or `/xdice config {0} no` if you want XDice to show you what you rolled and nothing more!"""

config_helper_plus_behaviour = """Our next configuration step is multiple-choice. XDice can handle inputs like **XdY + Z**. What we're going to set up now is what she should do with that **+ Z** part. Here are your options:

1) Nothing. Ignore it entirely!
{1}2) Add Z on to the total of your rolls. e.g. `/roll 2d10 + 5 -> [10, 10] = 25`{1}
{2}3) Add an automatic success. For instance: `/roll 1d10 + 1 -> [10] = 2 Successes`{2}

To choose, send a message
`/xdice config {0} 1`
or
{1}`/xdice config {0} 2`{1}
or
{2}`/xdice config {0} 3`{2}
(and watch out: we were made in Ireland, so there's a U in that **behaviour**!)

Up to you, boss!"""

config_helper_explode_behaviour = """Our next step seems dangerous. "Exploding Dice"! In some systems, when you roll a certain number, the die can "explode" into copies of itself! XDice likes this one. She can handle that in _four_ different ways:

1) No explosions. The gunpowder will be removed from all dice post-haste!
{1}2) Double success. XDice will add an extra success for every explosion, no questions asked{1}
3) Roll again. Every exploding die magically clones itself and rolls again. The copies can't explode, so on the second roll, what you get is what you get.
4) Roll again, with exploding copies. Potentially infinite explosions! Exploding dice create copies, and those copies can explode too.

Your magic incantation for choosing the right level of explosions for your game is `/xdice config {0} 1` (or 2 or 3 or 4!)

So what's it gonna be?"""

config_helper_explode_on = """Got it! And now I just need to tell XDice which dice should explode for you.

`/xdice config {0} 10` will explode any 10s. `/xdice config {0} 10 9 8 1` will explode every 10, 9, 8, and 1 (why not?)

Careful, XDice is a fussy eater. If more than one number should explode, you need to make sure there's only **one** space between each of the numbers you type.

Off you go!"""

config_helper_crit_fail = """We're getting close to the end now, just one more configuration to set up!
In many Success-based systems, rolling 1s without any successes is a Critical Failure. Do you want XDice to let you know when that happens? It'll look something like this:
**[3,2,1,1] = Critical Fail**

`/xdice config {0} yes`
or
`/xdice config {0} no`.

One last time, tell me what you need!"""

config_helper_confirm = """
{1}
SPLIT

All done! Above is the output of the `/xdice help` command, which gives you a complete overview of the settings you have enabled on your server.

Look over it, and if you're happy with everything, send the message `/xdice config {0}`, and you'll be ready to play! If not, send `/xdice config cancel` to shut me down and exit Configuration Mode so you can start over.

And remember, if you ever need this help information again, just send XDice the message `/xdice help`.

Are you happy with this configuration?
`/xdice config {0}` to save and exit configuration mode.
or
`/xdice config cancel` to discard all changes and exit configuration mode."""

config_helper_signoff = """It's been a pleasure helping you configure my darling XDice. If you ever need me again... well... It's extremely unlikely that it will be _me_ specifically who gets assigned to your case, but one of my several billion siblings will be more than happy to help!
            
Happy rolling!

-- Confi"""

config_helper_input_error = "Uh oh, that didn't work. Check your spelling and try again!"
# endregion
# endregion
config_channel_name = "xdice-config"

configs = {}

client = discord.Client()


@client.event
async def on_guild_join(guild: discord.guild):
    if f"{str(guild.id)}.json" in os.listdir("configs"):
        return

    config_channel = await guild.create_text_channel(config_channel_name)
    new_config = Config(
        guild_id=guild.id,
        config_mode=False,
        current_config_step=ConfigStep.BEGIN,
        default_dice=20,
        count_successes=False,
        success_on=[],
        add_total=False,
        plus_behaviour=PlusBehaviour.IGNORE,
        explode_behaviour=ExplodeBehaviour.NONE,
        explode_on=[],
        crit_fail_behaviour=CritFailBehaviour.NONE,
    )
    configs[str(new_config.guild_id)] = new_config
    new_config.save()

    await config_channel.send(f"""Thank you for installing XDice.

The {config_channel_name} channel has been created to keep your regular chat channels clean. However, all XDice commands will work from any channel, and XDice will respond in the same channel.

Right now, XDice is set up in its most basic form. Additional features can be enabled by using Configuration Mode, which can be activated by sending the following message in any channel on this server:
`/xdice config`

To learn how to use the features which are enabled on this server, send this message on any channel:
`/xdice help`

To delete the {config_channel_name} channel, send this message
`/xdice config delete`""")


@client.event
async def on_ready():
    print(f"Logged in as {client.user}")

    known_configs = set(file.removesuffix(".json") for file in os.listdir("configs"))
    all_servers = set(str(guild.id) for guild in client.guilds)

    for server_id in known_configs:
        configs[server_id] = await load_config(server_id)
        print(f"Loaded config for {server_id}")

    # Initialise XDice for any servers it was added to while it was offline.
    for server_id in all_servers.difference(known_configs):
        await on_guild_join(discord.utils.get(client.guilds, id=int(server_id)))

    print("All configs loaded")


@client.event
async def on_message(message: discord.Message):
    if message.author == client.user:
        return

    message_content = message.content.lower()
    config = configs[str(message.author.guild.id)]

    if message_content.startswith("/xdice config "):
        for msg_part in (await configure(message)).split("SPLIT"):
            await message.channel.send(msg_part)

    elif message_content == wtf_command:
        time.sleep(1)
        await message.channel.send(random.SystemRandom().choice(insults))

    elif message_content == config_command:
        if not message.author.guild_permissions.manage_guild:
            await message.channel.send("Only admins can configure the XDice bot.")
        config.config_mode = True
        channel = discord.utils.get(message.guild.channels, name=config_channel_name)
        if not channel:
            await message.guild.create_text_channel(config_channel_name)
        await channel.send("XDice is now in configuration mode.\nTo continue, send a message with the contents `/xdice config begin`\nTo exit configuration mode **without saving any changes**, send `/xdice config cancel`")

    elif message_content == help_command:
        for msg_part in (await get_help(config)).split("SPLIT"):
            await message.channel.send(msg_part)

    else:
        result = await roll_dice(message_content, message)
        if result is not None:
            await message.channel.send(f"{message.author.mention}: {result}")


async def load_config(server_id: str):
    with open(f"configs/{server_id}.json") as f:
        json_string = f.read()

    raw_dict = json.loads(json_string)
    return Config(
        guild_id=int(server_id),
        config_mode=raw_dict["configMode"],
        current_config_step=ConfigStep.BEGIN,
        default_dice=raw_dict["defaultDice"],
        count_successes=raw_dict["countSuccesses"],
        success_on=raw_dict["successOn"],
        add_total=raw_dict["addTotal"],
        plus_behaviour=PlusBehaviour(raw_dict["plusBehaviour"]),
        explode_behaviour=ExplodeBehaviour(raw_dict["explodeBehaviour"]),
        explode_on=raw_dict["explodeOn"],
        crit_fail_behaviour=CritFailBehaviour(raw_dict["critFailBehaviour"])
    )


async def roll_dice(roll_string: str, message: discord.Message) -> Optional[str]:
    # Rock/Paper/Scissors doesn't use the same pattern-matching as dice-rolling, but it is still a user-requested
    # random "number", so it gets to live here.
    if roll_string.strip() == "/rps" or roll_string.strip() == "!rps":
        return await rock_paper_scissors()

    if roll_string.strip() == "/coin" or roll_string.strip() == "!coin":
        return await flip_coin()

    # Load the config for the server which the message came from.
    config = configs[str(message.author.guild.id)]

    # Match {!/}r[oll] [X][dY][+Z] in the incoming message
    match = re.match(r"[!/]r(?:oll)*\s*(\d*)\s*d*\s*(\d*)(?:\s*\++\s*(\d*))?", roll_string)

    if not match:
        return None

    # "/roll d10" -> Assume default of rolling 1 d10
    if not match.group(1):
        dice_to_roll = 1
    else:
        dice_to_roll = int(match.group(1))

    # "/roll 5" -> Assume roll 5 of the default dice type
    if not match.group(2):
        dice_type = config.default_dice
    else:
        dice_type = int(match.group(2))

    rolled_dice = []

    # Quantity and type of dice decided, time to roll them!
    for i in range(dice_to_roll):
        rolled_dice.append(await roll(dice_type))

    if match.group(3) and config.plus_behaviour == PlusBehaviour.AUTO_SUCCESS:
        extra_successes = int(match.group(3))
    else:
        extra_successes = 0

    # Roll an extra die for each exploding die, but do not explode those dice.
    if config.explode_behaviour == ExplodeBehaviour.EXTRA:
        for rolled_die in rolled_dice:
            if rolled_die in config.explode_on:
                rolled_dice.append(await roll(dice_type))
    # Roll an extra die for each exploding die, and then explode any of those which meet the criterion
    elif config.explode_behaviour == ExplodeBehaviour.EXTRA_CHAIN:
        next_link = rolled_dice.copy()
        more_explosions = True
        while more_explosions:
            current_link = next_link.copy()
            next_link = []
            more_explosions = False
            for rolled_die in current_link:
                if rolled_die in config.explode_on:
                    new_roll = await roll(dice_type)
                    rolled_dice.append(new_roll)
                    if new_roll in config.explode_on:
                        more_explosions = True
                        next_link.append(new_roll)
    # Add an extra success per explosion
    elif config.explode_behaviour == ExplodeBehaviour.DOUBLE:
        for rolled_die in rolled_dice:
            if rolled_die in config.explode_on:
                extra_successes += 1

    result = str(rolled_dice)

    if config.add_total:
        return f"{result} = {sum(rolled_dice)}"

    if (not config.count_successes) or (dice_type != config.default_dice):
        return result

    successes = extra_successes
    crit_fails = 0
    for rolled_die in rolled_dice:
        if rolled_die in config.success_on:
            successes += 1
        elif rolled_die == 1:
            crit_fails += 1

    if successes == 0 and crit_fails > 0 and config.crit_fail_behaviour == CritFailBehaviour.ONE_NO_SUCCESSES:
        return f"{result} = {crit_fails} Critical Fail"

    return f"""{result} = {successes} Success{"es" if successes != 1 else ""}"""


async def roll(dice_type: int) -> int:
    return math.floor(random.SystemRandom().randint(12, (dice_type * 10) + 9) / 10)


async def configure(config_message: discord.Message):
    if (not config_message.author.guild_permissions.manage_guild) or (not config_message.content.lower().startswith(config_command)):
        return f"{config_message.author.mention}: XDice is being configured by your server's admin, and is currently unavailable."

    server_id = str(config_message.author.guild.id)

    config = configs[server_id]
    command = config_message.content.lower().removeprefix("/xdice config ")

    if command == "delete":
        channel = discord.utils.get(config_message.guild.channels, name=config_channel_name)
        await channel.delete()
        return

    # Commands below this point can only be used once Configuration Mode is active.
    if not config.config_mode:
        return "XDice is not in configuration mode"

    if command == "cancel":
        configs[server_id] = load_config(server_id)
        return f"Discarding all changes and exiting configuration mode.\nSend the command `/xdice delete` to remove the {config_channel_name} channel."

    try:
        split = command.split(" ")
        current_step = split[0]
        command_param = split[1] if len(split) == 2 else ""

        if current_step != config.current_config_step.name.lower():
            raise ValueError

        if current_step == ConfigStep.BEGIN.name.lower():
            config.current_config_step = ConfigStep.DEFAULT_DICE
            return config_helper_begin.format(random.SystemRandom().randint(1000, 9999), ConfigStep.DEFAULT_DICE.name.lower())

        if current_step == ConfigStep.DEFAULT_DICE.name.lower():
            selection = command_param.replace("d", "")  # In case anyone types "d20" instead of "20", etc.
            config.default_dice = int(selection)
            config.current_config_step = ConfigStep.COUNT_SUCCESSES

            return config_helper_count_successes.format(selection)

        if current_step == ConfigStep.COUNT_SUCCESSES.name.lower():
            if command_param == "yes":
                config.count_successes = True
                config.current_config_step = ConfigStep.SUCCESS_ON
                return config_helper_success_on.format(ConfigStep.SUCCESS_ON.name.lower())
            elif command_param == "no":
                config.count_successes = False
                config.current_config_step = ConfigStep.ADD_TOTAL
                return config_helper_add_total.format(ConfigStep.ADD_TOTAL.name.lower())


        if current_step == ConfigStep.SUCCESS_ON.name.lower():
            config.success_on = [int(x) for x in split[1:]]
            config.current_config_step = ConfigStep.PLUS_BEHAVIOUR

            return f"""Nice!\n\n{config_helper_plus_behaviour.format(ConfigStep.PLUS_BEHAVIOUR.name.lower(), "~~", "")}"""

        if current_step == ConfigStep.ADD_TOTAL.name.lower():
            if command_param == "yes":
                config.add_total = True
                config.current_config_step = ConfigStep.PLUS_BEHAVIOUR

                return f"""Nice! A big ol' YES on adding up your totals. You got it!\n\n{config_helper_plus_behaviour.format(ConfigStep.PLUS_BEHAVIOUR.name.lower(), "", "~~")}"""
            elif command_param == "no":
                config.add_total = False
                config.current_config_step = ConfigStep.EXPLODE_BEHAVIOUR

                return f"""No assistance needed on the ol' numbers, you've got it!\n\n{config_helper_explode_behaviour.format(ConfigStep.EXPLODE_BEHAVIOUR.name.lower(), "~~" if config.plus_behaviour == PlusBehaviour.ADD else "")}"""
            else:
                raise ValueError

        if current_step == ConfigStep.PLUS_BEHAVIOUR.name.lower():
            valid_numbers = set(behaviour.value for behaviour in PlusBehaviour)
            chosen_number = int(command_param)
            if chosen_number in valid_numbers:
                config.plus_behaviour = PlusBehaviour(chosen_number)
                config.current_config_step = ConfigStep.EXPLODE_BEHAVIOUR
            else:
                raise ValueError
            return f"""Done and done!\n\n{config_helper_explode_behaviour.format(ConfigStep.EXPLODE_BEHAVIOUR.name.lower(), "~~" if config.plus_behaviour == PlusBehaviour.ADD else "")}"""

        if current_step == ConfigStep.EXPLODE_BEHAVIOUR.name.lower():
            valid_numbers = set(behaviour.value for behaviour in ExplodeBehaviour)
            chosen_number = int(command_param)
            if chosen_number in valid_numbers:
                config.explode_behaviour = ExplodeBehaviour(chosen_number)
            else:
                raise ValueError

            if chosen_number != ExplodeBehaviour.NONE.value:
                config.current_config_step = ConfigStep.EXPLODE_ON
                return config_helper_explode_on.format(ConfigStep.EXPLODE_ON.name.lower())
            else:
                if config.count_successes:
                    config.current_config_step = ConfigStep.CRIT_FAIL_BEHAVIOUR
                    return config_helper_crit_fail.format(ConfigStep.CRIT_FAIL_BEHAVIOUR)
                else:
                    config.current_config_step = ConfigStep.CONFIRM
                    return config_helper_confirm.format(ConfigStep.CONFIRM.name.lower(), await get_help(config))

        if current_step == ConfigStep.EXPLODE_ON.name.lower():
            config.explode_on = [int(x) for x in split[1:]]
            config.current_config_step = ConfigStep.CRIT_FAIL_BEHAVIOUR
            return config_helper_crit_fail.format(ConfigStep.CRIT_FAIL_BEHAVIOUR.name.lower())

        if current_step == ConfigStep.CRIT_FAIL_BEHAVIOUR.name.lower():
            if command_param == "yes":
                config.crit_fail_behaviour = CritFailBehaviour.ONE_NO_SUCCESSES
            elif command_param == "no":
                config.crit_fail_behaviour = CritFailBehaviour.NONE
            config.current_config_step = ConfigStep.CONFIRM
            return config_helper_confirm.format(ConfigStep.CONFIRM.name.lower(), await get_help(config))

        if current_step == ConfigStep.CONFIRM.name.lower():
            config.current_config_step = ConfigStep.BEGIN
            config.save()
            config.config_mode = False
            return config_helper_signoff

    except ValueError or IndexError:
        return config_helper_input_error


async def get_help(config: Config):
    string_builder = [
        f"**= XDice v{xdice_version} =**",
        "------------------------------------------------------------------------------------------------------------------------",
        "",
        f"""**BASIC USAGE**
XDice listens for messages beginning with one of the activation phrases
`/roll`
or
`!roll`.
then a "dice request" in this format:
`XdY + Z`.

Therefore a full "roll command" looks something like this:
`/roll 3d12 + 5`
or
`!roll 3d12 + 5`

There is also a special roll command for randomly generating Rock/Paper/Scissors results:
`/rps` or `!rps`
SPLIT
------------------------------------------------------------------------------------------------------------------------

**SHORTCUTS**
Not every part of the roll command is required. XDice on your server has been configured with default values for X, Y, and Z to help you type less:
```
X = 1
Y = {config.default_dice}
Z = {"N/A -- (Z is ignored on your server)" if config.plus_behaviour == PlusBehaviour.IGNORE else "0"}
```
Spaces between the X, Y, and Z elements are optional, as are the letters **o-l-l** in the word "roll".
i.e. `!r` and `/r` work exactly the same as `!roll` and `/roll`

So for example, in this server `/r5` will roll 5d{config.default_dice}s, `!r d8` will roll 1d8.
The smallest possible roll command is just two characters long:
`/r` or `!r` -> This will roll 1d{config.default_dice}.""",
    ]

    if config.plus_behaviour != PlusBehaviour.IGNORE:
        behaviour = "add an additional success to each roll result" if config.plus_behaviour == PlusBehaviour.AUTO_SUCCESS else "add the value of Z to the total of your rolled dice."
        string_builder.append(f"""\nFor your server, the number specified in the `Z` position will {behaviour}""")

    extra_settings_position = len(string_builder)
    string_builder.append("PLACEHOLDER")

    extra_settings = False
    if config.count_successes:
        extra_settings = True
        shuffled = config.success_on.copy()
        random.shuffle(shuffled)

        string_builder.append(f"- Automatically count successes when any of the following numbers are rolled: {str(config.success_on)}")
        string_builder.append(f"    **{str(shuffled)} = {len(config.success_on)} Successes**")
    elif config.add_total:
        extra_settings = True
        sample = [i for i in range(config.default_dice, config.default_dice - 3, -1)]
        string_builder.append("\n- Automatically calculate the total of all of your rolls:")
        string_builder.append(f"    **{str(sample)} = {sum(sample)}**")

    if config.explode_behaviour != ExplodeBehaviour.NONE:
        extra_settings = True
        string_builder.append(f"""\n- "Explode" dice on rolls of the following numbers: {str(config.explode_on)}""")
        if config.explode_behaviour == ExplodeBehaviour.DOUBLE:
            string_builder.append("    - Each exploded die will add an extra success to the Successes counter.")
        elif config.explode_behaviour == ExplodeBehaviour.EXTRA:
            string_builder.append("    - Each exploded die will roll an extra die of the same type and add the result to the list of rolled numbers. Extra dice do not explode.")
        elif config.explode_behaviour == ExplodeBehaviour.EXTRA_CHAIN:
            string_builder.append("    - Each exploded die will roll an extra die of the same type and add the result to the list of rolled numbers. Extra dice can also explode.")

    if config.crit_fail_behaviour == CritFailBehaviour.ONE_NO_SUCCESSES:
        extra_settings = True
        string_builder.append("\n- Add a Critical Fail warning when you roll at least one 1 and no Successes:")
        string_builder.append("    **[1, 1, 1] = Critical Fail**")

    if extra_settings:
        string_builder[extra_settings_position] = f"SPLIT\n------------------------------------------------------------------------------------------------------------------------\n\n**SPECIAL SETTINGS**\nFor your server, XDice has also been configured to do the following when you roll d{config.default_dice}s:\n"
    else:
        del(string_builder[extra_settings_position])

    return "\n".join(string_builder)


async def rock_paper_scissors():
    num = random.SystemRandom().randint(1, 3)
    if num == 1:
        return "Rock"
    elif num == 2:
        return "Paper"
    elif num == 3:
        return "Scissors"
    else:
        return "...... there is no fourth option, how did you do that?"


async def flip_coin():
    num = random.SystemRandom().randint(1, 2)
    if num == 1:
        return "Heads"
    elif num == 2:
        return "Tails"
    else:
        return "...... there is no third option, how did you do that?"


if __name__ == '__main__':
    client.run('YOUR_TOKEN_GOES_HERE')
