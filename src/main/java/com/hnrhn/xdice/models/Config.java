package com.hnrhn.xdice.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hnrhn.xdice.enums.ConfigStep;
import com.hnrhn.xdice.enums.CritFailBehaviour;
import com.hnrhn.xdice.enums.ExplodeBehaviour;
import com.hnrhn.xdice.enums.PlusBehaviour;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Config {
    public long guildId;

    public boolean configMode;

    @JsonDeserialize(using = ConfigStepDeserializer.class)
    public ConfigStep currentConfigStep;

    public int defaultDice;

    public boolean countSuccesses;

    public List<Integer> successOn;

    public boolean addTotal;

    @JsonDeserialize(using = PlusBehaviourDeserializer.class)
    public PlusBehaviour plusBehaviour;

    @JsonDeserialize(using = ExplodeBehaviourDeserializer.class)
    public ExplodeBehaviour explodeBehaviour;

    public List<Integer> explodeOn;

    @JsonDeserialize(using = CritFailBehaviourDeserializer.class)
    public CritFailBehaviour critFailBehaviour;

    public Config(){}

    public Config(
            long guildId,
            boolean configMode,
            ConfigStep currentConfigStep,
            int defaultDice,
            boolean countSuccesses,
            List<Integer> successOn,
            boolean addTotal,
            PlusBehaviour plusBehaviour,
            ExplodeBehaviour explodeBehaviour,
            List<Integer> explodeOn,
            CritFailBehaviour critFailBehaviour
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
    }

    public void save() {
        var jsonOutput = new StringBuilder()
            .append("{\n")
            .append("\t\"configMode\": false\n")
            .append("\t\"currentConfigStep\": ").append(currentConfigStep.toString()).append("\n")
            .append("\t\"defaultDice\": ").append(this.defaultDice).append("\n")
            .append("\t\"countSuccesses\": ").append(this.countSuccesses).append("\n")
            .append("\t\"successOn\": ").append(this.successOn).append("\n")
            .append("\t\"addTotal\": ").append(this.addTotal).append("\n")
            .append("\t\"plusBehaviour\": ").append(this.plusBehaviour.toString()).append("\n")
            .append("\t\"explodeBehaviour\": ").append(this.explodeBehaviour.toString()).append("\n")
            .append("\t\"explodeOn\": ").append(this.explodeOn.toString()).append("\n")
            .append("\t\"critFailBehaviour\": ").append(this.critFailBehaviour.toString()).append("\n")
            .append("}\n");

        try {
            Files.writeString(Paths.get("configs/" + this.guildId + ".json"), jsonOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
