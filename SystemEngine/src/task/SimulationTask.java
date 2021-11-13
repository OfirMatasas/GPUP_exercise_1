package task;

import target.Graph;
import target.Target;
import userInterface.GraphSummary;
import userInterface.TargetSummary;
import userInterface.TaskRequirements;

import java.time.Duration;
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
//        if(!canExecuteOnTarget(target))
//            return;

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
        }
        else
        {
            target.setResultStatus(Target.ResultStatus.Failure);
            target.setAllRequiredForTargetsToChosenStatus(Target.RuntimeStatus.Skipped, Target.ResultStatus.Frozen);
        }

        target.setRuntimeStatus(Target.RuntimeStatus.Finished);

        try {
            Thread.sleep(getTargetsParameters().get(target).getProcessingTime().toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getTargetsParameters().get(target).stopTheClock();
    }

    public void executeTask(Graph graph, Boolean fromScratch, GraphSummary graphSummary)
    {
        TaskParameters taskParameters = requirements.getTaskParametersFromUser();

        for(Target currentTarget : graph.getGraphTargets().values())
            getTargetParameters().put(currentTarget, taskParameters);

        Set<Target> executableTargets = makeExecutableTargetsSet(graph, fromScratch);
        //Starting task on graph
        requirements.printStartOfTaskOnGraph(graph);
        graphSummary.startTheClock();

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
                updateGraphSummary(graphSummary, currentTarget, taskParameters);
                returnedSet = addNewTargetsToExecutableSet(currentTarget);

                for(Target newExecutable : returnedSet)
                    executableTargets.add(newExecutable);

                requirements.printEndOfTaskOnTarget(this, currentTarget);
            }

            cloneSet.clear();
        }

        //Task stopped
        graphSummary.stopTheClock();
        graphSummary.calculateResults();
        requirements.printGraphTaskSummary(graphSummary);
    }

    public void updateGraphSummary(GraphSummary graphSummary, Target target, TaskParameters targetTaskParameters) {
        Duration time = targetTaskParameters.getProcessingTime();
        String targetName = target.getTargetName();
        String extraInformation = target.getExtraInformation();
        Target.ResultStatus resultStatus = target.getResultStatus();

        graphSummary.getTargetsSummaryMap().put(targetName,
                new TargetSummary(time, targetName, extraInformation, resultStatus));
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
                    currentTarget.setResultStatus(Target.ResultStatus.Frozen);
                    currentTarget.setRuntimeStatus(Target.RuntimeStatus.Waiting);
                    currentTarget.setAllRequiredForTargetsToChosenStatus(Target.RuntimeStatus.Frozen, Target.ResultStatus.Frozen);
                    set.add(currentTarget);
                }
            }
        }

        return set;
    }
}