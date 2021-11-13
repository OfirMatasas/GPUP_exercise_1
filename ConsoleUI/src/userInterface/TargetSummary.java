package userInterface;

import target.Target;

import java.time.Duration;

public class TargetSummary
{
    private Duration time;
    private String targetName;
    private String extraInformation;
    private Target.ResultStatus resultStatus;

    public TargetSummary(String targetName) {
        this.targetName = targetName;
        this.time = Duration.ZERO;
        this.extraInformation = null;
        this.resultStatus = Target.ResultStatus.Frozen;
    }

    public TargetSummary(Duration time, String targetName, String extraInformation, Target.ResultStatus resultStatus) {
        this.time = time;
        this.targetName = targetName;
        this.extraInformation = extraInformation;
        this.resultStatus = resultStatus;
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
}
