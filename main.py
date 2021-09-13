import math

import discord
import random
import re

client = discord.Client()

last_rolls = {}

deck = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]


@client.event
async def on_ready():
    print(f"Logged in as {client.user}")


@client.event
async def on_message(message: discord.Message):
    if message.author == client.user:
        return

    message_content: str = message.content.lower()

    result = None

    if message_content.startswith("/r"):
        result = await roll_dice(message_content, message.author.mention)

    if message_content.startswith("/push"):
        result = await push(message.author.mention)

    if message_content.startswith("/newdeck"):
        result = await new_deck()

    if message_content.startswith("/draw"):
        result = await draw()

    if result is not None:
        await message.channel.send(f"{message.author.mention}: {result}")
        return


async def roll_dice(message: str, author: str):
    match = re.match(r"/r +(\d+) +(\d+) (\d+) *([mel])? *(p)?", message)

    if not match:
        if secondary_match := re.match(r"/r (\d+)d(\d+)", message):
            return [await roll(int(secondary_match.group(2))) for _ in range(int(secondary_match.group(1)))]
        return None

    base_dice = int(match.group(1)) if match.group(1) else None
    skill_dice = int(match.group(2)) if match.group(2) else None
    gear_dice = int(match.group(3)) if match.group(3) else None
    artifact_dice = match.group(4)
    pride_dice = match.group(5) is not None

    roll_result = await roll_forbidden(base_dice, skill_dice, gear_dice, artifact_dice, pride_dice)

    last_rolls[author] = roll_result

    return await parse(roll_result)


async def roll_forbidden(num_base: int, num_skill: int, num_gear: int, artifact: str, pride: bool):
    return {
        "base_result": [await roll(6) for _ in range(num_base)],
        "skill_result": [await roll(6) for _ in range(num_skill)],
        "gear_result": [await roll(6) for _ in range(num_gear)],
        "artifact_rolled": artifact,
        "artifact_result": await roll_special(artifact) if artifact is not None else [],
        "pride_used": pride,
        "pride_result": await roll_special("p") if pride else [],
        "pushed": False
    }


async def parse(roll_result):
    return f"""
Base: {str(roll_result["base_result"]).replace("6", ":crossed_swords:").replace("1", ":skull:")}
Skill: {str(roll_result["skill_result"]).replace("6", ":crossed_swords:")}
Gear: {str(roll_result["gear_result"]).replace("6", ":crossed_swords:").replace("1", ":skull:")}
Artifact: {str(roll_result["artifact_result"]).replace("6", ":crossed_swords:").replace("1", ":skull:")}
Pride: {str(roll_result["pride_result"]).replace("6", ":crossed_swords:").replace("1", ":skull:")}
"""


async def roll(dice_type: int):
    return math.floor(random.SystemRandom().randint(12, (dice_type * 10) + 9) / 10)


async def roll_special(letter):
    if letter == "m":
        result = await roll(8)
    elif letter == "e":
        result = await roll(10)
    elif letter == "l" or letter == "p":
        result = await roll(12)
    else:
        return []

    if result < 6:
        return [result]

    if result in [6, 7]:
        return [6]

    if result in [8, 9]:
        return [6, 6]

    if result in [10, 11]:
        return [6, 6, 6]

    if result == 12:
        return [6, 6, 6, 6]


async def push(author: str):
    if last_rolls[author]["pushed"]:
        return "You have already pushed this roll."

    rolls = await roll_forbidden(
        sum(1 for i in last_rolls[author]["base_result"] if (i != 1 and i != 6)),
        sum(1 for i in last_rolls[author]["skill_result"] if i != 6),
        sum(1 for i in last_rolls[author]["gear_result"] if (i != 1 and i != 6)),
        last_rolls[author]["artifact_rolled"] if not (1 in last_rolls[author]["artifact_result"] or 6 in last_rolls[author]["artifact_result"]) else None,
        last_rolls[author]["pride_used"] if not (1 in last_rolls[author]["pride_result"] or 6 in last_rolls[author]["pride_result"]) else False,
    )

    new_result = {
        "base_result": [i for i in last_rolls[author]["base_result"] if (i == 1 or i == 6)] + rolls["base_result"],
        "skill_result": [i for i in last_rolls[author]["skill_result"] if i == 6] + rolls["skill_result"],
        "gear_result": [i for i in last_rolls[author]["gear_result"] if (i == 1 or i == 6)] + rolls["gear_result"],
        "artifact_rolled": rolls["artifact_rolled"],
        "artifact_result": [i for i in last_rolls[author]["artifact_result"] if (i == 1 or i == 6)] + rolls["artifact_result"],
        "pride_used": rolls["pride_used"],
        "pride_result": [i for i in last_rolls[author]["pride_result"] if (i == 1 or i == 6)] + rolls["pride_result"],
    }

    last_rolls[author]["pushed"] = True

    return await parse(new_result)


async def new_deck():
    global deck
    deck = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    return "New deck created"


async def draw():
    global deck
    if len(deck) == 0:
        "No cards remain"
    index = random.SystemRandom().randrange(len(deck))
    ret = deck[index]
    del deck[index]
    return ret


if __name__ == "__main__":
    client.run("ODExMzUyNTI2Nzc5NzExNTE4.YCw87A.zpeYvBjWQIDZx4pkr_5DtXKnAXo")
