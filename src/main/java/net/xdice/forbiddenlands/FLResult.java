package net.xdice.forbiddenlands;

import java.util.ArrayList;
import java.util.List;

public class FLResult {
    private final List<Integer> baseResult;
    private final List<Integer> skillResult;
    private final List<Integer> gearResult;
    private final Integer artifactDieType;
    private final List<Integer> artifactResult;
    private final List<Integer> prideResult;
    private final boolean pushed;

    public FLResult(
        List<Integer> baseResult,
        List<Integer> skillResult,
        List<Integer> gearResult,
        Integer artifactDieType,
        List<Integer> artifactResult,
        List<Integer> prideResult,
        boolean pushed
    ) {
        this.baseResult = baseResult;
        this.skillResult = skillResult;
        this.gearResult = gearResult;
        this.artifactDieType = artifactDieType;
        this.artifactResult = artifactResult;
        this.prideResult = prideResult;
        this.pushed = pushed;
    }

    public List<Integer> getBaseResult() {
        return this.baseResult != null ? this.baseResult : new ArrayList<>();
    }

    public List<Integer> getSkillResult() {
        return this.skillResult != null ? this.skillResult : new ArrayList<>();
    }

    public List<Integer> getGearResult() {
        return this.gearResult != null ? this.gearResult : new ArrayList<>();
    }

    public Integer getArtifactDieType() {
        return this.artifactDieType;
    }

    public List<Integer> getArtifactResult() {
        return this.artifactResult != null ? this.artifactResult : new ArrayList<>();
    }

    public List<Integer> getPrideResult() {
        return this.prideResult != null ? this.prideResult : new ArrayList<>();
    }

    public boolean isPushed() {
        return this.pushed;
    }
}
