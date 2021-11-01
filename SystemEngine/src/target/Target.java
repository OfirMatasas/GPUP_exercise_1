package target;

import java.sql.Time;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class Target {
    static public enum RuntimeStatus { FROZEN, SKIPPED, WAITING, IN_PROCESS, FINISHED}
    static public enum ResultStatus { SUCCESS, WARNING, FAILURE, FROZEN }
    static public enum TargetProperty { LEAF, MIDDLE, ROOT, INDEPENDENT }

    private Set<Target> dependsOnTargets;
    private Set<Target> requireForTargets;
    private RuntimeStatus runtimeStatus;
    private ResultStatus resultStatus;
    private Duration resultTime;
    private TargetProperty targetProperty;
    private String targetName;
    private String extraInformation;

    public Duration getResultTime() {
        return resultTime;
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
        this.runtimeStatus = RuntimeStatus.WAITING;
        this.resultStatus = ResultStatus.FROZEN;
        this.resultTime = Duration.ZERO;
        this.targetProperty = TargetProperty.INDEPENDENT;
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
}
