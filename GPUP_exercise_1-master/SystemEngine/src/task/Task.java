package task;

import target.Graph;
import target.Target;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class Task {
    private Map<Target, TaskParameters> targetsParameters;
    private Instant timeStarted, timeEnded;
    private Duration totalTimeSpentOnTask;
    protected Random rand;
    protected Boolean firstRun;

    public Task() {
        this.targetsParameters = new HashMap<>();
        this.rand = new Random();
        this.firstRun = true;
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
        for(Target currentTarget : target.getDependsOnTargets())
        {
            //Depends-on-target is frozen / failed - return false.
            if(currentTarget.getResultStatus().equals(Target.ResultStatus.Frozen) || currentTarget.getResultStatus().equals(Target.ResultStatus.Failure))
                return false;
            //Depends-on-target is in process / waiting - check again in 3 seconds.
            else if (currentTarget.getRuntimeStatus().equals(Target.RuntimeStatus.InProcess) || currentTarget.getRuntimeStatus().equals(Target.RuntimeStatus.Waiting))
            {
                do {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while(currentTarget.getRuntimeStatus().equals(Target.RuntimeStatus.InProcess));
            }
            //Depends-on-target is done, but not successfully - return false.
            else if(!currentTarget.getRuntimeStatus().equals(Target.RuntimeStatus.Finished))
                return false;
        }

        return true;
    }

    abstract public void executeTaskOnTarget(Target target);
}