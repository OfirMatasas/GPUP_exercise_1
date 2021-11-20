package task;

import myExceptions.*;
import resources.checker.ResourceChecker;
import resources.generated.GPUPDescriptor;
import target.Graph;
import target.Target;
import userInterface.GraphSummary;
import userInterface.TargetSummary;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public abstract class Task {
    protected Map<Target, TaskParameters> targetsParameters;
    protected Random rand;
    protected TaskOutput taskOutput;
    protected Graph graph;
    protected GraphSummary graphSummary;

    public Task() {
        this.rand = new Random();
        this.taskOutput = new TaskOutput();
    }

    public Map<Target, TaskParameters> getTargetParameters()
    {
        return this.targetsParameters;
    }

    public void makeNewTargetParameters()
    {
        this.targetsParameters = new HashMap<>();
    }

    public Map<Target, TaskParameters> getTargetsParameters() {
        return targetsParameters;
    }

    abstract public void executeTaskOnTarget(Target target);
    abstract public void executeTask(Graph graph, Boolean fromScratch, GraphSummary graphSummary, Path xmlFilePath) throws OpeningFileCrash, FileNotFound, NoFailedTargets;
    abstract public Set<Target> makeExecutableTargetsSet(Boolean fromScratch);

    public void preparationsForTask()
    {
        TargetSummary currentTargetSummary;

        for(Target currentTarget : graph.getGraphTargets().values())
        {
            currentTargetSummary = graphSummary.getTargetsSummaryMap().get(currentTarget.getTargetName());

            currentTargetSummary.setRunning(false);
        }
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

    public GPUPDescriptor fromXmlFileToObject(Path fileName)
    {
        GPUPDescriptor descriptor = null;
        try
        {
            File file = new File(fileName.toString());
            JAXBContext jaxbContext = JAXBContext.newInstance(GPUPDescriptor.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            descriptor = (GPUPDescriptor)jaxbUnmarshaller.unmarshal(file);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }

        return descriptor;
    }

    public Graph extractFromXMLToGraph(Path path) throws NotXMLFile, FileNotFound, DoubledTarget, InvalidConnectionBetweenTargets, EmptyGraph {
        if(!path.getFileName().toString().endsWith(".xml"))
        {
            throw new NotXMLFile(path.getFileName().toString());
        }
        else if(!Files.isExecutable(path))
        {
            throw new FileNotFound(path.getFileName().toString());
        }
        //The file can be executed
        GPUPDescriptor descriptor = fromXmlFileToObject(path);
        ResourceChecker checker = new ResourceChecker();
        Graph graph = checker.checkResource(descriptor);

        if(graph != null)
            graph.calculateProperties();

        return graph;
    }
}