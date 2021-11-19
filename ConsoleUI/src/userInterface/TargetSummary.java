package userInterface;

import target.Graph;
import target.Target;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TargetSummary implements Serializable
{
    static public enum RuntimeStatus { Frozen, Skipped, Waiting, InProcess, Finished }
    static public enum ResultStatus { Success, Warning, Failure }

    private Duration actualTime, predictedTime;
    private String targetName;
    private String extraInformation;
    private ResultStatus resultStatus;
    private RuntimeStatus runtimeStatus;
    private boolean isSkipped;
    private Instant timeStarted;
    private Set<String> skippedTargets;

    public TargetSummary(String targetName) {
        this.targetName = targetName;
        this.actualTime = Duration.ZERO;
        this.predictedTime = Duration.ZERO;
        this.extraInformation = null;
        this.resultStatus = ResultStatus.Failure;
        this.runtimeStatus = RuntimeStatus.Frozen;
        this.isSkipped = false;
    }

//    public TargetSummary(Duration time, String targetName, String extraInformation, ResultStatus resultStatus) {
//        this.time = time;
//        this.targetName = targetName;
//        this.extraInformation = extraInformation;
//        this.resultStatus = resultStatus;
//    }

    public Set<String> getSkippedTargets() {
        return skippedTargets;
    }

    public Duration getPredictedTime() {
        return predictedTime;
    }

    public void startTheClock()
    {
        timeStarted = Instant.now();
    }

    public void stopTheClock()
    {
        Instant timeEnded = Instant.now();
        actualTime = Duration.between(timeStarted, timeEnded);
    }

    public void setPredictedTime(Duration predictedTime) {
        this.predictedTime = predictedTime;
    }

    public Boolean checkIfFailedBefore()
    {
        if(skippedTargets == null)
        {
            skippedTargets = new HashSet<>();
            return false;
        }

        return true;
    }

    public void addNewSkippedTarget(String skippedTargetName)
    {
        skippedTargets.add(skippedTargetName);
    }

    public RuntimeStatus getRuntimeStatus() {
        return runtimeStatus;
    }

    public void setRuntimeStatus(RuntimeStatus runtimeStatus) {
        this.runtimeStatus = runtimeStatus;
    }

    public void setSkipped(boolean skipped) {
        isSkipped = skipped;
    }

    public Duration getTime() {
        return actualTime;
    }

    public void setTime(Duration time) {
        this.actualTime = time;
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

    public ResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(ResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }

    public boolean isSkipped() {
        return this.isSkipped;
    }

    public void setAllRequiredForTargetsRuntimeStatus(Target target, GraphSummary graphSummary, RuntimeStatus runtimeStatus)
    {
        for(Target requiredForTarget : target.getRequireForTargets())
        {
            graphSummary.getTargetsSummaryMap().get(requiredForTarget.getTargetName()).setRuntimeStatus(runtimeStatus);
            setAllRequiredForTargetsRuntimeStatus(requiredForTarget, graphSummary, runtimeStatus);
        }
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
}
