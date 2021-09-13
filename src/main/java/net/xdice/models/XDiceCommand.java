package net.xdice.models;

import net.xdice.enums.CommandType;
import net.xdice.fallenkingdoms.FKDice;

public class XDiceCommand {
    private CommandType commandType;
    private Integer numberOfDice;
    private Integer typeOfDice;
    private Integer modifier;
    private FKDice fallenKingdomsDice;
    private String fallenKingdomsUserId;

    public XDiceCommand(){}

    public XDiceCommand(CommandType commandType) {
        this.commandType = commandType;
    }

    public XDiceCommand(
        CommandType commandType,
        int numberOfDice,
        int typeOfDice,
        int modifier
    ){
        this.commandType = commandType;
        this.numberOfDice = numberOfDice;
        this.typeOfDice = typeOfDice;
        this.modifier = modifier;
    }

    public XDiceCommand(CommandType commandType, FKDice fallenKingdomsDice) {
        this.commandType = commandType;
        this.fallenKingdomsDice = fallenKingdomsDice;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public Integer getNumberOfDice() {
        return numberOfDice;
    }

    public void setNumberOfDice(Integer numberOfDice) {
        this.numberOfDice = numberOfDice;
    }

    public Integer getTypeOfDice() {
        return typeOfDice;
    }

    public void setTypeOfDice(Integer typeOfDice) {
        this.typeOfDice = typeOfDice;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public FKDice getFallenKingdomsDice() {
        return fallenKingdomsDice;
    }

    public void setFallenKingdomsDice(FKDice fallenKingdomsDice) {
        this.fallenKingdomsDice = fallenKingdomsDice;
    }
}
