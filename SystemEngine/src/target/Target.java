package target;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class Target {
    static public enum RuntimeStatus { Frozen, Skipped, Waiting, InProcess, Finished }
    static public enum ResultStatus { Success, Warning, Failure, Frozen }
    static public enum TargetProperty { LEAF, MIDDLE, ROOT, INDEPENDENT }
    static public enum Connection { REQUIRED_FOR, DEPENDS_ON }

    private Set<Target> dependsOnTargets;
    private Set<Target> requireForTargets;
    private RuntimeStatus runtimeStatus;
    private ResultStatus resultStatus;
    private Duration resultTime;
    private TargetProperty targetProperty;
    private String targetName;
    private String extraInformation;
    private Boolean wasVisited;

    public Duration getResultTime() {
        return resultTime;
    }

    public void setWasVisited(Boolean wasVisited) {
        this.wasVisited = wasVisited;
    }

    public Boolean getWasVisited() {
        return wasVisited;
    }

    public void setResultTime(Duration resultTime) {
        this.resultTime = resultTime;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getExtraInformation() {
        return extraInformation;
    }

    public void setExtraInformation(String extraInformation) {
        this.extraInformation = extraInformation;
    }

    public Target() {
        this.dependsOnTargets = new HashSet<>();
        this.requireForTargets = new HashSet<>();
        this.runtimeStatus = RuntimeStatus.Waiting;
        this.resultStatus = ResultStatus.Frozen;
        this.resultTime = Duration.ZERO;
        this.targetProperty = TargetProperty.INDEPENDENT;
        this.wasVisited=false;
    }

    public Set<Target> getDependsOnTargets() {
        return dependsOnTargets;
    }

    public void setDependsOnTargets(Set<Target> dependsOnTargets) {
        this.dependsOnTargets = dependsOnTargets;
    }

    public Set<Target> getRequireForTargets() {
        return requireForTargets;
    }

    public void setRequireForTargets(Set<Target> requireForTargets) {
        this.requireForTargets = requireForTargets;
    }

    public RuntimeStatus getRuntimeStatus() {
        return runtimeStatus;
    }

    public void setRuntimeStatus(RuntimeStatus runtimeStatus) {
        this.runtimeStatus = runtimeStatus;
    }

    public ResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(ResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }

    public TargetProperty getTargetProperty() {
        return targetProperty;
    }

    public void setTargetProperty(TargetProperty targetProperty) {
        this.targetProperty = targetProperty;
    }

    public void addToDependsOn(Target dependsOn) { dependsOnTargets.add(dependsOn); }

    public void addToRequiredFor(Target requiredFor) { requireForTargets.add(requiredFor); }
}