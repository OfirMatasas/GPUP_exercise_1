package task;

import java.time.Duration;
import java.time.Instant;

public class TaskParameters {
    private Duration processingTime;
    private Boolean isRandom;
    private Double successRate, successWithWarnings;

    public TaskParameters() {
        this.processingTime = null;
        this.isRandom = false;
        this.successRate = this.successWithWarnings = 0.0;
    }

    public Duration getProcessingTime() {
        return processingTime;
    }

    public Boolean isRandom() {
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
