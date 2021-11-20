package userInterface;

import target.Graph;
import target.Target;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphSummary implements Serializable {
    //--------------------------------------------------Members-----------------------------------------------------//
    private final String graphName;
    private Duration totalTime;
    private Instant timeStarted;
    private Map<String, TargetSummary> targetsSummaryMap;
    private Map<TargetSummary.ResultStatus, Integer> allResultStatus;
    private Boolean firstRun;

    //------------------------------------------------Constructors--------------------------------------------------//
    public GraphSummary(Graph graph) {
        this.targetsSummaryMap = new HashMap<>();
        this.firstRun = true;
        this.graphName = graph.getGraphName();

        for(Target currentTarget : graph.getGraphTargets().values())
            this.targetsSummaryMap.put(currentTarget.getTargetName(), new TargetSummary(currentTarget.getTargetName()));
    }

    //--------------------------------------------------Getters-----------------------------------------------------//
    public Boolean getFirstRun() {
        return firstRun;
    }

    public String getGraphName() {
        return graphName;
    }

    public Map<TargetSummary.ResultStatus, Integer> getAllResultStatus() {
        return allResultStatus;
    }

    public Duration getTime() {
        return totalTime;
    }

    public Map<String, TargetSummary> getTargetsSummaryMap() {
        return targetsSummaryMap;
    }

    //--------------------------------------------------Setters-----------------------------------------------------//
    public void setFirstRun(Boolean firstRun) {
        this.firstRun = firstRun;
    }

    public void setRunningTargets(Target currentTarget, Boolean runningOrNot)
    {
        targetsSummaryMap.get(currentTarget.getTargetName()).setRunning(runningOrNot);

        for(Target requiredForTarget : currentTarget.getRequiredForTargets())
            setRunningTargets(requiredForTarget, runningOrNot);
    }

    public void setAllRequiredForTargetsOnSkipped(Target lastSkippedTarget, TargetSummary failedTargetSummary)
    {
        for(Target newSkippedTarget : lastSkippedTarget.getRequiredForTargets())
        {
            targetsSummaryMap.get(newSkippedTarget.getTargetName()).setSkipped(true);

            failedTargetSummary.addNewSkippedTarget(newSkippedTarget.getTargetName());
            setAllRequiredForTargetsOnSkipped(newSkippedTarget, failedTargetSummary);
        }
    }

    //--------------------------------------------------Methods-----------------------------------------------------//
    public void startTheClock()
    {
        timeStarted = Instant.now();
    }

    public void stopTheClock()
    {
        Instant timeEnded = Instant.now();
        totalTime = Duration.between(timeStarted, timeEnded);
    }

    public void calculateResults()
    {
        allResultStatus = new HashMap<>();
        Integer succeeded = 0, failed = 0, warning = 0;

        for(TargetSummary current : targetsSummaryMap.values())
        {
            if(!current.isRunning())
                continue;

            switch (current.getResultStatus())
            {
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

        allResultStatus.put(TargetSummary.ResultStatus.Success, succeeded);
        allResultStatus.put(TargetSummary.ResultStatus.Failure, failed);
        allResultStatus.put(TargetSummary.ResultStatus.Warning, warning);
    }
}
