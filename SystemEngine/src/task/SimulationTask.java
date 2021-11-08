package task;

import target.Graph;
import target.Target;
import userInterface.TaskRequirements;
import java.util.HashSet;
import java.util.Set;

public class SimulationTask extends Task{

    TaskRequirements requirements = new TaskRequirements();

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

    public void executeTask(Graph graph, Boolean fromScratch)
    {
        TaskParameters taskParameters = requirements.getTaskParametersFromUser();

        for(Target currentTarget : graph.getGraphTargets().values())
            getTargetParameters().put(currentTarget, taskParameters);

        Set<Target> executableTargets = makeExecutableTargetsSet(graph, fromScratch);
        //Starting task on graph
        requirements.printStartOfTaskOnGraph(graph);
        startTheClock();

        Set<Target> cloneSet = new HashSet<>(), returnedSet;

        while(executableTargets.size() != 0)
        {
            for(Target currTarget : executableTargets)
                cloneSet.add(currTarget);

            executableTargets.clear();

            for(Target currentTarget : cloneSet)
            {
                requirements.printStartOfTaskOnTarget(this, currentTarget);
                executeTaskOnTarget(currentTarget);
                returnedSet = addNewTargetsToExecutableSet(currentTarget);

                for(Target newExecutable : returnedSet)
                    executableTargets.add(newExecutable);

                requirements.printEndOfTaskOnTarget(this, currentTarget);
            }

            cloneSet.clear();
        }

        //Task stopped
        stopTheClock();
        requirements.printGraphTaskSummary(this, graph);
    }

    @Override
    public Set<Target> makeExecutableTargetsSet(Graph graph, Boolean fromScratch)
    {
        Set<Target> set = new HashSet<>();

        for(Target currentTarget : graph.getGraphTargets().values())
        {
            if(fromScratch)
            {
                Target.TargetProperty prop = currentTarget.getTargetProperty();
                if(prop.equals(Target.TargetProperty.INDEPENDENT)
                        || prop.equals(Target.TargetProperty.LEAF))
                {
                    set.add(currentTarget);
                }
            }
            else
            {
                if(currentTarget.getResultStatus().equals(Target.ResultStatus.Failure))
                {
                    set.add(currentTarget);
                }
            }
        }

        return set;
    }
}