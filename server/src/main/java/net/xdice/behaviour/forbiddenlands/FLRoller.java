package net.xdice.behaviour.forbiddenlands;

import net.xdice.behaviour.standard.StandardRoller;
import net.xdice.core.XDiceRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FLRoller extends StandardRoller {
    public String flRoll(FLDice flDice, XDiceRepositoryImpl repository, String userId) throws SQLException {
        List<Integer> base = new ArrayList<>();
        List<Integer> skill = new ArrayList<>();
        List<Integer> gear = new ArrayList<>();
        List<Integer> artifact = new ArrayList<>();
        List<Integer> pride = new ArrayList<>();

        for (int i = 0; i < flDice.getNumberOfBaseDice(); i++) {
            base.add(roll(6));
        }

        for (int i = 0; i < flDice.getNumberOfSkillDice(); i++) {
            skill.add(roll(6));
        }

        for (int i = 0; i < flDice.getNumberOfGearDice(); i++) {
            gear.add(roll(6));
        }

        if (flDice.getTypeOfArtifactDie() != null) {
            artifact = convertSpecial(roll(flDice.getTypeOfArtifactDie()));
        }

        FLResult result = new FLResult(base, skill, gear, flDice.getTypeOfArtifactDie(), artifact, pride, false);

        repository.upsertMostRecentRoll(userId, result, false);

        return parseResult(result);
    }

    public String flPush(String userIdentifier, XDiceRepositoryImpl repository, boolean usePride) throws SQLException {
        FLResult previousRoll = repository.getMostRecentRoll(userIdentifier);
        if (previousRoll.isPushed()) {
            return "You have already pushed this roll";
        }

        int numNewBase = (int)previousRoll.getBaseResult().stream().filter(x -> x != 1 && x != 6).count();
        int numNewSkill = (int)previousRoll.getSkillResult().stream().filter(x -> x != 6).count();
        int numNewGear = (int)previousRoll.getGearResult().stream().filter(x -> x != 1 && x != 6).count();
        int numNewArtifact =
            previousRoll.getArtifactDieType() != null
            && (int)previousRoll
                .getArtifactResult()
                .stream()
                .filter(x -> x != 1 && x >= 6)
                .count() == 0
            ? 1
            : 0;

        List<Integer> newBase = new ArrayList<>();
        List<Integer> newSkill = new ArrayList<>();
        List<Integer> newGear = new ArrayList<>();
        List<Integer> newArtifact = new ArrayList<>();
        List<Integer> newPride = new ArrayList<>();

        for (int i = 0; i < numNewBase; i++) {
            newBase.add(roll(6));
        }

        for (int i = 0; i < numNewSkill; i++) {
            newSkill.add(roll(6));
        }

        for (int i = 0; i < numNewGear; i++) {
            newGear.add(roll(6));
        }

        if (numNewArtifact > 0) {
            newArtifact = convertSpecial(roll(previousRoll.getArtifactDieType()));
        }

        List<Integer> pushedBase = Stream.concat(previousRoll.getBaseResult().stream().filter(x -> x == 1 || x == 6), newBase.stream()).collect(Collectors.toList());
        List<Integer> pushedSkill = Stream.concat(previousRoll.getSkillResult().stream().filter(x -> x == 6), newSkill.stream()).collect(Collectors.toList());
        List<Integer> pushedGear = Stream.concat(previousRoll.getGearResult().stream().filter(x -> x == 1 || x == 6), newGear.stream()).collect(Collectors.toList());
        List<Integer> pushedArtifact = Stream.concat(previousRoll.getArtifactResult().stream().filter(x -> x == 1 || x == 6), newArtifact.stream()).collect(Collectors.toList());
        List<Integer> pride = usePride ? convertSpecial(roll(12)) : Collections.emptyList();

        FLResult pushedResult = new FLResult(pushedBase, pushedSkill, pushedGear, previousRoll.getArtifactDieType(), pushedArtifact, pride, true);

        repository.upsertMostRecentRoll(userIdentifier, pushedResult, true);

        return parseResult(pushedResult);
    }

    public String flNewDeck(String serverId, XDiceRepositoryImpl repository) throws SQLException {
        repository.upsertRemainingCards(serverId, List.of(1,2,3,4,5,6,7,8,9,10));
        return "New initiative deck created.";
    }

    public String flDrawInitiative(String serverId, XDiceRepositoryImpl repository) throws SQLException {
        List<Integer> remainingCards = repository.getRemainingCards(serverId);
        if (remainingCards == null) {
            return "No cards remain";
        }
        int chosenIndex = ThreadLocalRandom.current().nextInt(remainingCards.size());
        String chosenCard = remainingCards.get(chosenIndex).toString();
        remainingCards.remove(chosenIndex);
        repository.upsertRemainingCards(serverId, remainingCards);
        return chosenCard;
    }

    private String parseResult(FLResult result) {
        ArrayList<String> parsedResult = new ArrayList<>();
        parsedResult.add("");
        if (result.getBaseResult().size() > 0) {
            parsedResult.add("Base: " + result.getBaseResult().toString().replace("6", ":crossed_swords:").replace("1", ":skull:"));
        }

        if (result.getSkillResult().size() > 0) {
            parsedResult.add("Skill: " + result.getSkillResult().toString().replace("6", ":crossed_swords:"));
        }

        if (result.getGearResult().size() > 0) {
            parsedResult.add("Gear: " + result.getGearResult().toString().replace("6", ":crossed_swords:").replace("1", ":skull:"));
        }

        if (result.getArtifactResult().size() > 0) {
            parsedResult.add("Artifact: " + result.getArtifactResult().toString().replace("6", ":crossed_swords:").replace("1", ":skull:"));
        }

        if (result.getPrideResult().size() > 0) {
            parsedResult.add("Pride: " + result.getPrideResult().toString().replace("6", ":crossed_swords:").replace("1", ":skull:"));
        }

        return String.join("\n", parsedResult);
    }

    private Integer roll(Integer diceType) {
        return (int) (ThreadLocalRandom.current().nextInt(12, (diceType * 10 + 9)) / 10.0);
    }

    private List<Integer> convertSpecial(int specialResult) {
        return switch (specialResult) {
            case 6, 7 -> List.of(6);
            case 8, 9 -> List.of(6, 6);
            case 10, 11 -> List.of(6, 6, 6);
            case 12 -> List.of(6, 6, 6, 6);
            default -> List.of(specialResult);
        };
    }
}
