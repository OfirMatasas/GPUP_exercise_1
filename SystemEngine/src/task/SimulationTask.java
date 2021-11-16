package task;

import myExceptions.EmptyGraph;
import myExceptions.FileNotFound;
import myExceptions.OpeningFileCrash;
import target.Graph;
import target.Target;
import userInterface.GraphSummary;
import userInterface.TargetSummary;
import userInterface.TaskRequirements;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class SimulationTask extends Task{

    TaskRequirements requirements = new TaskRequirements();

    @Override
    public void clearTaskHistory(Graph graph) {
        super.clearTaskHistory(graph);
    }

    public void executeTaskOnTarget(Target target, GraphSummary graphSummary)
    {
        getTargetsParameters().get(target).startTheClock();
        target.setWasVisited(true);
        target.setRuntimeStatus(Target.RuntimeStatus.InProcess);

        Double result = Math.random();

        if(result < getTargetParameters().get(target).getSuccessRate())
        {
            for(Target currentTarget : target.getRequireForTargets())
                graphSummary.getTargetsSummaryMap().get(currentTarget.getTargetName()).setSkipped(false);

            if(result < getTargetParameters().get(target).getSuccessWithWarnings())
            {
                target.setResultStatus(Target.ResultStatus.Warning);
                graphSummary.getTargetsSummaryMap().get(target.getTargetName()).setResultStatus(Target.ResultStatus.Warning);
            }
            else
            {
                target.setResultStatus(Target.ResultStatus.Success);
                graphSummary.getTargetsSummaryMap().get(target.getTargetName()).setResultStatus(Target.ResultStatus.Success);
            }
        }
        else //The task failed
        {
            target.setResultStatus(Target.ResultStatus.Failure);
            graphSummary.setAllRequiredForAsSkipped(target);
//            TargetSummary currentTargetSummary = graphSummary.getTargetsSummaryMap().get(target.getTargetName());
//            currentTargetSummary.setSkippedTargets(graphSummary.setAllRequiredForTargetsOnSkipped(target));
        }

        target.setRuntimeStatus(Target.RuntimeStatus.Finished);

        try {
            Thread.sleep(getTargetsParameters().get(target).getProcessingTime().toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getTargetsParameters().get(target).stopTheClock();
    }

    public void executeTask(Graph graph, Boolean fromScratch, GraphSummary graphSummary) throws OpeningFileCrash, FileNotFound {
        TaskParameters taskParameters = requirements.getTaskParametersFromUser();
        Path directoryPath = taskOutput.createNewDirectoryOfTaskLogs("Simulation Task");
        Path filePath;

        //Update target parameters
        for(Target currentTarget : graph.getGraphTargets().values())
        {
            if(!(currentTarget.getResultStatus().equals(Target.ResultStatus.Success)
                    || currentTarget.getResultStatus().equals(Target.ResultStatus.Warning)))
            {
                getTargetParameters().put(currentTarget, taskParameters);
                graphSummary.getTargetsSummaryMap().get(currentTarget.getTargetName()).setTime(taskParameters.getProcessingTime());
            }
        }

        //Make a set of executable targets
        Set<Target> executableTargets = makeExecutableTargetsSet(graph, graphSummary, fromScratch);

        //Starting task on graph
        requirements.printStartOfTaskOnGraph(graph);
        graphSummary.startTheClock();

        Set<Target> cloneSet = new HashSet<>(), returnedSet;
        while(executableTargets.size() != 0)
        {
            //Copying the executable targets to a clone set
            for(Target currTarget : executableTargets)
                cloneSet.add(currTarget);

            executableTargets.clear();

            for(Target currentTarget : cloneSet)
            {
                //Creating a file for each target
                filePath = Paths.get(directoryPath + "\\" + currentTarget.getTargetName() + ".log");
                try {
                    Files.createFile(filePath);
                } catch (IOException e) {
                    throw new OpeningFileCrash(filePath.getFileName().toString());
                }
                TargetSummary currentTargetSummary = graphSummary.getTargetsSummaryMap().get(currentTarget.getTargetName());
                //Print start of task on current target to target's file and to console
                try {
                    taskOutput.outputStartingTaskOnTarget(new FileOutputStream(filePath.toString()),
                            currentTargetSummary);
                    taskOutput.outputStartingTaskOnTarget(new PrintStream(System.out),
                            currentTargetSummary);
                } catch (FileNotFoundException e) {
                    throw new FileNotFound(filePath.getFileName().toString());
                }

                executeTaskOnTarget(currentTarget, graphSummary);
                updateGraphSummary(graphSummary, currentTarget, taskParameters);
                returnedSet = addNewTargetsToExecutableSet(currentTarget);

                for(Target newExecutable : returnedSet)
                    executableTargets.add(newExecutable);

                currentTargetSummary = graphSummary.getTargetsSummaryMap().get(currentTarget.getTargetName());
                //Print target summary to target's file and to console
                try {
                    taskOutput.outputEndingTaskOnTarget(new FileOutputStream(filePath.toString(), true),
                            currentTargetSummary);
                    taskOutput.outputEndingTaskOnTarget(new PrintStream(System.out),
                            currentTargetSummary);
                } catch (FileNotFoundException e) {
                    throw new FileNotFound(filePath.getFileName().toString());
                }
            }
            cloneSet.clear();
        }

        //Task stopped
        filePath = Paths.get(directoryPath + "\\Graph Summary.log");
        try {
            Files.createFile(filePath);
        } catch (IOException e) {
            throw new OpeningFileCrash(filePath.getFileName().toString());
        }

        graphSummary.stopTheClock();
        graphSummary.calculateResults();

        try {
            taskOutput.outputGraphSummary(new FileOutputStream(filePath.toString(), true),
                    graphSummary);
            taskOutput.outputGraphSummary(new PrintStream(System.out),
                    graphSummary);
        } catch (FileNotFoundException e) {
            throw new FileNotFound(filePath.getFileName().toString());
        }
    }

    public void updateGraphSummary(GraphSummary graphSummary, Target target, TaskParameters targetTaskParameters) {
        Duration time = targetTaskParameters.getProcessingTime();

        graphSummary.getTargetsSummaryMap().get(target.getTargetName()).setTime(time);
    }

    @Override
    public Set<Target> makeExecutableTargetsSet(Graph graph, GraphSummary graphSummary, Boolean fromScratch)
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
                if(currentTarget.getResultStatus().equals(Target.ResultStatus.Failure)
                && !graphSummary.getTargetsSummaryMap().get(currentTarget.getTargetName()).isSkipped())
                {
                    currentTarget.setRuntimeStatus(Target.RuntimeStatus.Waiting);
                    currentTarget.setAllRequiredForTargetsToChosenStatus(Target.RuntimeStatus.Frozen, Target.ResultStatus.Failure);
                    set.add(currentTarget);
                }
            }
        }

        return set;
    }
}