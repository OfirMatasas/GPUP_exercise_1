package task;

import target.Graph;
import target.Target;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public abstract class Task {
    private Map<Target, TaskParameters> targetsParameters;
    private Instant timeStarted, timeEnded;
    private Duration totalTimeSpentOnTask;
    protected Random rand;

    public Task() {
        this.targetsParameters = new HashMap<>();
        this.rand = new Random();
    }

    public Map<Target, TaskParameters> getTargetParameters()
    {
        return this.targetsParameters;
    }

    public Map<Target, TaskParameters> getTargetsParameters() {
        return targetsParameters;
    }

    public void startTheClock()
    {
        timeStarted = Instant.now();
    }

    public void stopTheClock()
    {
        timeEnded = Instant.now();
        totalTimeSpentOnTask = Duration.between(timeStarted, timeEnded);
    }

    public Duration getTotalTimeSpentOnTask()
    {
        return totalTimeSpentOnTask;
    }

    public void clearTaskHistory(Graph graph)
    {
        graph.setAllTargetruntimeStatusToDefault();
        graph.setAllTargetWasVisitedToDefault();
        graph.setAllTargetruntimeStatusToDefault();
    }
    
    protected Boolean isRecheckingTargetNecessary(Target target)
    {
        if(!target.getWasVisited())
            return false;
        else if(target.getResultStatus().equals(Target.ResultStatus.Success))
            return false;

        return true;
    }

    protected Boolean canExecuteOnTarget(Target target)
    {
        //Checking if all his depends-on-targets are succeeded. if not - return false.
        for(Target currentTarget : target.getRequireForTargets())
        {
            //Required-for-target is frozen / failed - return false.
            if(currentTarget.getResultStatus().equals(Target.ResultStatus.Frozen) || currentTarget.getResultStatus().equals(Target.ResultStatus.Failure))
                return false;
            //Required-for-target is done, but not successfully - return false.
            else if(!currentTarget.getRuntimeStatus().equals(Target.RuntimeStatus.Finished))
                return false;
        }

        return true;
    }

    abstract public void executeTaskOnTarget(Target target);

    abstract public void executeTask(Graph graph, Boolean fromScratch);
    abstract public Set<Target> makeExecutableTargetsSet(Graph graph, Boolean fromScratch);

    public Set<Target> addNewTargetsToExecutableSet(Target lastTargetFinished)
    {
        Set<Target> returnedSet = new HashSet<>();
        for(Target candidateTarget : lastTargetFinished.getDependsOnTargets())
        {
            for(Target currentTarget : candidateTarget.getRequireForTargets())
            {
                if(!currentTarget.getRuntimeStatus().equals(Target.RuntimeStatus.Finished))
                    break;
                else if(currentTarget.getRuntimeStatus().equals(Target.RuntimeStatus.Skipped))
                    break;
            }
            returnedSet.add(candidateTarget);
        }
        return returnedSet;
    }
}