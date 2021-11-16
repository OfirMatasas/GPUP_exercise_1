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
    private String graphName;
    private Duration totalTime;
    private Instant timeStarted, timeEnded;
    private Map<String, TargetSummary> targetsSummaryMap;
    private Map<Target.ResultStatus, Integer> allResultStatus;
    private Boolean firstRun;
    private Graph graph;

    public void setAllRequiredForAsSkipped(Target target)
    {
        for(Target skippedTarget : target.getRequireForTargets())
        {
            targetsSummaryMap.get(skippedTarget.getTargetName()).setSkipped(true);
            setAllRequiredForAsSkipped(skippedTarget);
        }
    }

    public Boolean getFirstRun() {
        return firstRun;
    }

    public void setFirstRun(Boolean firstRun) {
        this.firstRun = firstRun;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public Map<Target.ResultStatus, Integer> getAllResultStatus() {
        return allResultStatus;
    }

    public GraphSummary(Graph graph) {
        this.targetsSummaryMap = new HashMap<>();
        this.firstRun = true;
        this.graph = graph;
        this.graphName = graph.getGraphName();

        for(Target currentTarget : graph.getGraphTargets().values())
            this.targetsSummaryMap.put(currentTarget.getTargetName(), new TargetSummary(currentTarget.getTargetName()));
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
        Integer succeeded=0, failed=0, warning=0;

        for(TargetSummary current : targetsSummaryMap.values())
        {
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

        allResultStatus.put(Target.ResultStatus.Success, succeeded);
        allResultStatus.put(Target.ResultStatus.Failure, failed);
        allResultStatus.put(Target.ResultStatus.Warning, warning);
    }

//    public Set<String> setAllRequiredForTargetsOnSkipped(Target failedTarget)
//    {
//        Set<String> skippedSet = new HashSet<>();
//
//        for(Target skippedTarget : failedTarget.getRequireForTargets())
//        {
//            targetsSummaryMap.get(skippedTarget.getTargetName()).setSkipped(true);
//            skippedSet.add(skippedTarget.getTargetName());
//            skippedSet.addAll(setAllRequiredForTargetsOnSkipped(skippedTarget));
//        }
//        return skippedSet;
//    }
}
