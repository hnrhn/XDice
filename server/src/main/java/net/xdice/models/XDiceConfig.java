package net.xdice.models;

import net.xdice.enums.*;

import java.util.List;

public class XDiceConfig {
    private final String guildId;

    private boolean configMode;

    private ConfigStep currentConfigStep;

    private int defaultDice;

    private boolean countSuccesses;

    private List<Integer> successOn;

    private boolean addTotal;

    private PlusBehaviour plusBehaviour;

    private ExplodeBehaviour explodeBehaviour;

    private List<Integer> explodeOn;

    private CritFailBehaviour critFailBehaviour;

    private RollerSelection rollerSelection;

    public XDiceConfig(
        String guildId,
        boolean configMode,
        ConfigStep currentConfigStep,
        int defaultDice,
        boolean countSuccesses,
        List<Integer> successOn,
        boolean addTotal,
        PlusBehaviour plusBehaviour,
        ExplodeBehaviour explodeBehaviour,
        List<Integer> explodeOn,
        CritFailBehaviour critFailBehaviour,
        RollerSelection rollerSelection
    ) {
        this.guildId = guildId;
        this.configMode = configMode;
        this.currentConfigStep = currentConfigStep;
        this.defaultDice = defaultDice;
        this.countSuccesses = countSuccesses;
        this.successOn = successOn;
        this.addTotal = addTotal;
        this.plusBehaviour = plusBehaviour;
        this.explodeBehaviour = explodeBehaviour;
        this.explodeOn = explodeOn;
        this.critFailBehaviour = critFailBehaviour;
        this.rollerSelection = rollerSelection;
    }

    public static XDiceConfig getDefaultConfig(String guildId) {
        return new XDiceConfig(
            guildId,
            false,
            ConfigStep.BEGIN,
            20,
            false,
            null,
            false,
            PlusBehaviour.IGNORE,
            ExplodeBehaviour.NONE,
            null,
            CritFailBehaviour.NONE,
            RollerSelection.STANDARD
        );
    }

    public String getGuildId() {
        return guildId;
    }

    public boolean isConfigMode() {
        return configMode;
    }

    public void setConfigMode(boolean configMode) {
        this.configMode = configMode;
    }

    public ConfigStep getCurrentConfigStep() {
        return currentConfigStep;
    }

    public void setCurrentConfigStep(ConfigStep currentConfigStep) {
        this.currentConfigStep = currentConfigStep;
    }

    public int getDefaultDice() {
        return defaultDice;
    }

    public void setDefaultDice(int defaultDice) {
        this.defaultDice = defaultDice;
    }

    public boolean isCountSuccesses() {
        return countSuccesses;
    }

    public void setCountSuccesses(boolean countSuccesses) {
        this.countSuccesses = countSuccesses;
    }

    public List<Integer> getSuccessOn() {
        return successOn;
    }

    public void setSuccessOn(List<Integer> successOn) {
        this.successOn = successOn;
    }

    public boolean isAddTotal() {
        return addTotal;
    }

    public void setAddTotal(boolean addTotal) {
        this.addTotal = addTotal;
    }

    public PlusBehaviour getPlusBehaviour() {
        return plusBehaviour;
    }

    public void setPlusBehaviour(PlusBehaviour plusBehaviour) {
        this.plusBehaviour = plusBehaviour;
    }

    public ExplodeBehaviour getExplodeBehaviour() {
        return explodeBehaviour;
    }

    public void setExplodeBehaviour(ExplodeBehaviour explodeBehaviour) {
        this.explodeBehaviour = explodeBehaviour;
    }

    public List<Integer> getExplodeOn() {
        return explodeOn;
    }

    public void setExplodeOn(List<Integer> explodeOn) {
        this.explodeOn = explodeOn;
    }

    public CritFailBehaviour getCritFailBehaviour() {
        return critFailBehaviour;
    }

    public void setCritFailBehaviour(CritFailBehaviour critFailBehaviour) {
        this.critFailBehaviour = critFailBehaviour;
    }

    public RollerSelection getRollerSelection() {
        return rollerSelection;
    }

    public void setRollerSelection(RollerSelection rollerSelection) {
        this.rollerSelection = rollerSelection;
    }
}
