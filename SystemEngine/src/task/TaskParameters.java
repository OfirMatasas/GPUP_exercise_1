package task;

import java.time.Duration;
import java.time.Instant;

public class TaskParameters {
    private Duration processingTime;
    private Boolean isRandom;
    private Double successRate, successWithWarnings;
    private Instant timeStarted, timeEnded;
    private Duration totalTimeSpentOnTarget;

    public TaskParameters() {
        this.processingTime = null;
        this.isRandom = false;
        this.successRate = this.successWithWarnings = 0.0;
    }

    public void startTheClock()
    {
        timeStarted = Instant.now();
    }

    public void stopTheClock()
    {
        timeEnded = Instant.now();
        totalTimeSpentOnTarget = Duration.between(timeStarted, timeEnded);
    }

    public Duration getProcessingTime() {
        return processingTime;
    }

    public Boolean getRandom() {
        return isRandom;
    }

    public void setRandom(Boolean random) {
        isRandom = random;
    }

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }

    public Double getSuccessWithWarnings() {
        return successWithWarnings;
    }

    public void setSuccessWithWarnings(Double successWithWarnings) {
        this.successWithWarnings = successWithWarnings;
    }

    public void setProcessingTime(Duration processingTime) {
        this.processingTime = processingTime;
    }
}
