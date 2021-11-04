package task;

import target.Graph;
import target.Target;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class Task {
    private Map<Target, TaskParameters> targetsParameters;
    private Instant timeStarted, timeEnded;

    public Task() {
        this.targetsParameters = new HashMap<>();
    }

    public Map<Target, TaskParameters> getTargetsParameters() {
        return targetsParameters;
    }

    abstract public void executeTaskOnTarget(Target target);
}