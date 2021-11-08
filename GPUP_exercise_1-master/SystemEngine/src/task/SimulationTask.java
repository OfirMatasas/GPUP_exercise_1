package task;

import target.Graph;
import target.Target;

public class SimulationTask extends Task{

    @Override
    public void clearTaskHistory(Graph graph) {
        super.clearTaskHistory(graph);
    }

    public void executeTaskOnTarget(Target target)
    {
        if(!canExecuteOnTarget(target))
            return;

        getTargetsParameters().get(target).startTheClock();
        target.setWasVisited(true);
        target.setRuntimeStatus(Target.RuntimeStatus.InProcess);

        Double result = Math.random();

        if(result < getTargetParameters().get(target).getSuccessRate())
        {
            if(result < getTargetParameters().get(target).getSuccessWithWarnings())
                target.setResultStatus(Target.ResultStatus.Warning);
            else
                target.setResultStatus(Target.ResultStatus.Success);

            target.setRuntimeStatus(Target.RuntimeStatus.Finished);
        }
        else
        {
            target.setResultStatus(Target.ResultStatus.Failure);
            target.setAllDependsOnTargetsOnFrozen();
        }

        try {
            Thread.sleep(getTargetsParameters().get(target).getProcessingTime().toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getTargetsParameters().get(target).stopTheClock();
    }
}