package net.xdice.forbiddenlands;

public class FLDice {
    private final int NumberOfBaseDice;
    private final int NumberOfSkillDice;
    private final int NumberOfGearDice;
    private Integer TypeOfArtifactDie = null;
    private final boolean UsePrideDice;

    public FLDice(int base, int skill, int gear, Integer artifact, boolean prideUsed) {
        this.NumberOfBaseDice = base;
        this.NumberOfSkillDice = skill;
        this.NumberOfGearDice = gear;
        this.TypeOfArtifactDie = artifact;
        this.UsePrideDice = prideUsed;
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

    public boolean getPride() {
        return this.UsePrideDice;
    }
}
