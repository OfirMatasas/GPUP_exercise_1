package userInterface;

import target.Target;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;

public class TargetSummary implements Serializable
{
    private Duration time;
    private String targetName;
    private String extraInformation;
    private Target.ResultStatus resultStatus;
    private boolean isSkipped;
//    private Set<String> skippedTargets;

    public TargetSummary(String targetName) {
        this.targetName = targetName;
        this.time = Duration.ZERO;
        this.extraInformation = null;
        this.resultStatus = Target.ResultStatus.Failure;
        this.isSkipped = false;
    }

    public TargetSummary(Duration time, String targetName, String extraInformation, Target.ResultStatus resultStatus) {
        this.time = time;
        this.targetName = targetName;
        this.extraInformation = extraInformation;
        this.resultStatus = resultStatus;
    }

    public void setSkipped(boolean skipped) {
        isSkipped = skipped;
    }

    public Duration getTime() {
        return time;
    }

    public void setTime(Duration time) {
        this.time = time;
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

    public Target.ResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(Target.ResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }

    public boolean isSkipped() {
        return this.isSkipped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetSummary that = (TargetSummary) o;
        return Objects.equals(targetName, that.targetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetName);
    }

//    public Set<String> getSkippedTargets() {
//        return skippedTargets;
//    }
//
//    public void setSkippedTargets(Set<String> skippedTargets) {
//        this.skippedTargets = skippedTargets;
//    }
}
