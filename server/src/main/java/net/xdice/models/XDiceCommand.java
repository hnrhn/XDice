package net.xdice.models;

import net.xdice.enums.CommandType;
import net.xdice.behaviour.forbiddenlands.FLDice;

public class XDiceCommand {
    private CommandType commandType;
    private Integer numberOfDice;
    private Integer typeOfDice;
    private Integer modifier;
    private FLDice forbiddenLandsDice;

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

    public XDiceCommand(CommandType commandType, FLDice forbiddenLandsDice) {
        this.commandType = commandType;
        this.forbiddenLandsDice = forbiddenLandsDice;
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

    public FLDice getForbiddenLandsDice() {
        return forbiddenLandsDice;
    }

    public void setForbiddenLandsDice(FLDice forbiddenLandsDice) {
        this.forbiddenLandsDice = forbiddenLandsDice;
    }
}
