package task;

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
import java.util.HashSet;
import java.util.Set;

public class SimulationTask extends Task{

    TaskRequirements requirements = new TaskRequirements();

    public void executeTaskOnTarget(Target target)
    {
        TargetSummary targetSummary = graphSummary.getTargetsSummaryMap().get(target.getTargetName());
        targetSummary.startTheClock();
        targetSummary.setRuntimeStatus(TargetSummary.RuntimeStatus.InProcess);
        String TargetName = target.getTargetName();

        Double result = Math.random();

        if(result <= getTargetParameters().get(target).getSuccessRate()) //Task succeeded
        {
            if(result <= getTargetParameters().get(target).getSuccessWithWarnings())
                targetSummary.setResultStatus(TargetSummary.ResultStatus.Warning);
            else
                targetSummary.setResultStatus(TargetSummary.ResultStatus.Success);
        }
        else //Task failed
        {
            targetSummary.setResultStatus(TargetSummary.ResultStatus.Failure); //Already defined as "Failure", just to be on the safe side

            //If first time failed - making a new set of the targets became skipped
            if(!targetSummary.checkIfFailedBefore())
                graphSummary.setAllRequiredForTargetsOnSkipped(target, targetSummary);

        }

        targetSummary.setRuntimeStatus(TargetSummary.RuntimeStatus.Finished);
        targetSummary.checkForOpenTargets(target, graphSummary);

        //Going to sleep
        try {
            Thread.sleep(getTargetsParameters().get(target).getProcessingTime().toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        graphSummary.getTargetsSummaryMap().get(TargetName).stopTheClock();
    }

    public void executeTask(Graph graph, Boolean fromScratch, GraphSummary graphSummary, Path xmlFilePath) throws OpeningFileCrash, FileNotFound {
        Path directoryPath = taskOutput.createNewDirectoryOfTaskLogs("Simulation Task", xmlFilePath);
        Path filePath;
        this.graph = graph;
        this.graphSummary = graphSummary;
        preparationsForTask();

        //Check if there are any task parameters saved from last execution
        if(targetsParameters == null || !requirements.reuseTaskParameters())
        {
            TaskParameters taskParameters = requirements.getTaskParametersFromUser();

            //Update target parameters
            updateTaskParameters(taskParameters);
        }

        //Make a set of executable targets
        Set<Target> executableTargets = makeExecutableTargetsSet(fromScratch);

        //Starting task on graph
        taskOutput.printStartOfTaskOnGraph(graph.getGraphName());
        graphSummary.startTheClock();

        Set<Target> cloneSet = new HashSet<>(), returnedSet;
        while(executableTargets.size() != 0)
        {
            //Copying the executable targets to a clone set
            cloneSet.addAll(executableTargets);
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

                executeTaskOnTarget(currentTarget);
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
        filePath = Paths.get(directoryPath + "\\" +  graph.getGraphName() + " Graph Summary.log");
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

    private void updateTaskParameters(TaskParameters taskParameters)
    {
        Target currentTarget;
        String currentTargetName;
        makeNewTargetParameters();

        for(TargetSummary currentTargetSummary : graphSummary.getTargetsSummaryMap().values())
        {
            currentTargetName = currentTargetSummary.getTargetName();
            currentTarget = graph.getTarget(currentTargetName);

            getTargetParameters().put(currentTarget, taskParameters);
            currentTargetSummary.setPredictedTime(taskParameters.getProcessingTime());
        }
    }

    @Override
    public Set<Target> makeExecutableTargetsSet(Boolean fromScratch)
    {
        Set<Target> executableTargets = new HashSet<>();
        TargetSummary currentTargetSummary;

        for(Target currentTarget : graph.getGraphTargets().values())
        {
            currentTargetSummary = graphSummary.getTargetsSummaryMap().get(currentTarget.getTargetName());

            if(fromScratch)
            {
                Target.TargetProperty prop = currentTarget.getTargetProperty();

                if(prop.equals(Target.TargetProperty.INDEPENDENT)
                        || prop.equals(Target.TargetProperty.LEAF))
                {
                    executableTargets.add(currentTarget);
                    graphSummary.setRunningTargets(currentTarget, true);
                }
            }
            else
            { //Starting from last stop
                if(currentTargetSummary.getResultStatus().equals(TargetSummary.ResultStatus.Failure)
                && !graphSummary.getTargetsSummaryMap().get(currentTarget.getTargetName()).isSkipped())
                {
                    currentTargetSummary.setRuntimeStatus(TargetSummary.RuntimeStatus.Waiting);

                    currentTargetSummary.setAllRequiredForTargetsRuntimeStatus(currentTarget, graphSummary, TargetSummary.RuntimeStatus.Frozen);
                    executableTargets.add(currentTarget);
                    graphSummary.setRunningTargets(currentTarget, true);
                }
            }
        }

        return executableTargets;
    }
}