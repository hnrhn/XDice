package net.xdice.behaviour.standard;

import net.xdice.enums.*;
import net.xdice.interfaces.XDiceRoller;
import net.xdice.models.XDiceCommand;
import net.xdice.models.XDiceConfig;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class StandardRoller implements XDiceRoller {
    public String rollDice(XDiceCommand command, XDiceConfig config) {
        var successes = config.getPlusBehaviour() == PlusBehaviour.AUTO_SUCCESS ? command.getModifier() : 0;

        var rolledDice = new ArrayList<Integer>();

        for (int i = 0; i < command.getNumberOfDice(); i++) {
            rolledDice.add(roll(command.getTypeOfDice()));
        }

        switch (config.getExplodeBehaviour()) {
            case EXTRA:
                for (var rolledDie : new ArrayList<>(rolledDice)) {
                    if (config.getExplodeOn().contains(rolledDie)) {
                        rolledDice.add(roll(command.getTypeOfDice()));
                    }
                }
                break;
            case EXTRA_CHAIN:
                var explodingDiceCount = rolledDice.stream().filter(die -> config.getExplodeOn().contains(die)).count();
                while (explodingDiceCount > 0) {
                    var newDice = new ArrayList<Integer>();
                    for (int i = 0; i < explodingDiceCount; i++) {
                        newDice.add(roll(command.getTypeOfDice()));
                    }
                    rolledDice.addAll(newDice);
                    explodingDiceCount = newDice.stream().filter(die -> config.getExplodeOn().contains(die)).count();
                }
                break;
            case DOUBLE:
                successes += rolledDice.stream().filter(die -> config.getExplodeOn().contains(die)).count();
                break;
        }

        var result = rolledDice.toString();

        if (config.isAddTotal()) {
            int total = rolledDice.stream().mapToInt(roll -> roll).sum();
            if (config.getPlusBehaviour() == PlusBehaviour.ADD) {
                total += command.getModifier();
            }
            return result + " = " + total;
        }

        if (!config.isCountSuccesses() || command.getTypeOfDice() != config.getDefaultDice()) {
            return result;
        }

        successes += rolledDice.stream().filter(die -> config.getSuccessOn().contains(die)).count();
        var critFails = rolledDice.stream().filter(die -> die == 1).count();

        if (successes == 0 && critFails > 0 && config.getCritFailBehaviour() == CritFailBehaviour.ONE_NO_SUCCESSES) {
            return result + " = " + critFails + " Critical Fail" + (critFails == 1 ? "" : "s");
        }

        return result + " = " + successes + " Success" + (successes == 1 ? "" : "es");
    }

    public String rockPaperScissors() {
        RockPaperScissors[] possibleValues = RockPaperScissors.values();
        RockPaperScissors result = possibleValues[ThreadLocalRandom.current().nextInt(possibleValues.length)];
        return switch (result) {
            case ROCK -> "Rock";
            case PAPER -> "Paper";
            case SCISSORS -> "Scissors";
        };
    }

    public String flipCoin() {
        var possibleValues = CoinFlip.values();
        var result = possibleValues[ThreadLocalRandom.current().nextInt(possibleValues.length)];
        return switch (result) {
            case HEADS -> "Heads";
            case TAILS -> "Tails";
        };
    }

    private Integer roll(Integer diceType) {
        return (int) (ThreadLocalRandom.current().nextInt(12, (diceType * 10 + 9)) / 10.0);
    }
}
