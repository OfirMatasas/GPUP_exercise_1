package task;

import myExceptions.*;
import target.Graph;
import target.Target;
import userInterface.GraphSummary;
import userInterface.TargetSummary;
import java.nio.file.Path;
import java.util.*;

public abstract class Task {
    //--------------------------------------------------Members-----------------------------------------------------//
    protected Map<Target, TaskParameters> targetsParameters;
    protected Random rand;
    protected TaskOutput taskOutput;
    protected Graph graph;
    protected GraphSummary graphSummary;
    protected Path workingDirectory;

    //------------------------------------------------Constructors--------------------------------------------------//
    public Task() {
        this.rand = new Random();
        this.taskOutput = new TaskOutput();
    }

    //--------------------------------------------------Getters-----------------------------------------------------//
    public Map<Target, TaskParameters> getTargetParameters()
    {
        return this.targetsParameters;
    }

    public Map<Target, TaskParameters> getTargetsParameters() {
        return targetsParameters;
    }

    //--------------------------------------------------Setters-----------------------------------------------------//
    public void makeNewTargetParameters()
    {
        this.targetsParameters = new HashMap<>();
    }

    public void setWorkingDirectory(Path workingDirectory) { this.workingDirectory = workingDirectory; }
    //----------------------------------------------Abstract Methods------------------------------------------------//
    abstract public void executeTaskOnTarget(Target target);

    abstract public void executeTask(Graph graph, Boolean fromScratch, GraphSummary graphSummary, Path xmlFilePath) throws OpeningFileCrash, FileNotFound, NoFailedTargets;

    abstract public Set<Target> makeExecutableTargetsSet(Boolean fromScratch);

    //--------------------------------------------------Methods-----------------------------------------------------//
    public void preparationsForTask()
    {
        TargetSummary currentTargetSummary;

        for(Target currentTarget : graph.getGraphTargets().values())
        {
            currentTargetSummary = graphSummary.getTargetsSummaryMap().get(currentTarget.getTargetName());

            currentTargetSummary.setRunning(false);
        }

        graphSummary.setSkippedTargetsToZero();
    }

    public Set<Target> addNewTargetsToExecutableSet(Target lastTargetFinished)
    {
        Set<Target> returnedSet = new HashSet<>();
        Boolean addable = true;
        TargetSummary candidateTargetSummary, candidateDependsOnSummary;

        //Check every required-for-target of the last target tasked
        for(Target candidateTarget : lastTargetFinished.getRequiredForTargets())
        {
            addable = true;
            candidateTargetSummary = graphSummary.getTargetsSummaryMap().get(candidateTarget.getTargetName());

            //If the current candidate is already skipped - break
            if(candidateTargetSummary.getRuntimeStatus().equals(TargetSummary.RuntimeStatus.Skipped))
                break;
            //Check every target the candidate depends on
            for(Target candidateDependsOn : candidateTarget.getDependsOnTargets())
            {
                candidateDependsOnSummary = graphSummary.getTargetsSummaryMap().get(candidateDependsOn.getTargetName());
                //If the candidate's depends-on-target is not succeeded (even with warning) - break
                if(candidateDependsOnSummary.getResultStatus().equals(TargetSummary.ResultStatus.Failure))
                    addable = false;
            }
            //The candidate is not skipped and all the targets it depends on are succeeded
            if(addable)
            {
                returnedSet.add(candidateTarget);
                candidateTargetSummary.setSkipped(false);
                candidateTargetSummary.setRuntimeStatus(TargetSummary.RuntimeStatus.Waiting);
            }
        }
        return returnedSet;
    }
}