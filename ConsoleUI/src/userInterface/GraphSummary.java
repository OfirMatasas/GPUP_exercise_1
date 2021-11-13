package userInterface;

import target.Graph;
import target.Target;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class GraphSummary {
    private Duration totalTime;
    private Instant timeStarted, timeEnded;
    private Map<String, TargetSummary> targetsSummaryMap;
    private Map<Target.ResultStatus, Integer> allResultStatus;

    public Map<Target.ResultStatus, Integer> getAllResultStatus() {
        return allResultStatus;
    }

    public GraphSummary(Graph graph) {
        targetsSummaryMap = new HashMap<>();

        for(Target currentTarget : graph.getGraphTargets().values())
            targetsSummaryMap.put(currentTarget.getTargetName(), new TargetSummary(currentTarget.getTargetName()));
    }

    public Duration getTime() {
        return totalTime;
    }

    public void setTime(Duration time) {
        this.totalTime = time;
    }

    public Map<String, TargetSummary> getTargetsSummaryMap() {
        return targetsSummaryMap;
    }

    public void startTheClock()
    {
        timeStarted = Instant.now();
    }

    public void stopTheClock()
    {
        timeEnded = Instant.now();
        totalTime = Duration.between(timeStarted, timeEnded);
    }

    public void calculateResults()
    {
        allResultStatus = new HashMap<>();
        Integer succeeded=0, frozen=0, failed=0, warning=0;

        for(TargetSummary current : targetsSummaryMap.values())
        {
            switch (current.getResultStatus())
            {
                case Frozen:
                {
                    frozen++;
                    break;
                }
                case Failure:
                {
                    failed++;
                    break;
                }
                case Success:
                {
                    succeeded++;
                    break;
                }
                case Warning:
                {
                    warning++;
                    break;
                }
            }
        }

        allResultStatus.put(Target.ResultStatus.Success, succeeded);
        allResultStatus.put(Target.ResultStatus.Failure, failed);
        allResultStatus.put(Target.ResultStatus.Warning, warning);
        allResultStatus.put(Target.ResultStatus.Frozen, frozen);
    }
}
