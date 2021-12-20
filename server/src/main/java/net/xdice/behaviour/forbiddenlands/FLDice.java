package net.xdice.behaviour.forbiddenlands;

public class FLDice {
    private final int NumberOfBaseDice;
    private final int NumberOfSkillDice;
    private final int NumberOfGearDice;
    private final Integer TypeOfArtifactDie;

    public FLDice(int base, int skill, int gear, Integer artifact) {
        this.NumberOfBaseDice = base;
        this.NumberOfSkillDice = skill;
        this.NumberOfGearDice = gear;
        this.TypeOfArtifactDie = artifact;
    }

    public int getNumberOfBaseDice() {
        return this.NumberOfBaseDice;
    }

    public int getNumberOfSkillDice() {
        return this.NumberOfSkillDice;
    }

    public int getNumberOfGearDice() {
        return this.NumberOfGearDice;
    }

    public Integer getTypeOfArtifactDie() {
        return this.TypeOfArtifactDie;
    }
}
