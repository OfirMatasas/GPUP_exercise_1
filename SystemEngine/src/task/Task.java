package task;

import myExceptions.DoubledTarget;
import myExceptions.FileNotFound;
import myExceptions.InvalidConnectionBetweenTargets;
import myExceptions.NotXMLFile;
import resources.checker.ResourceChecker;
import resources.generated.GPUPDescriptor;
import target.Graph;
import target.Target;
import userInterface.GraphSummary;

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

    abstract public void executeTaskOnTarget(Target target);

    abstract public void executeTask(Graph graph, Boolean fromScratch, GraphSummary graphSummary);
    abstract public Set<Target> makeExecutableTargetsSet(Graph graph, Boolean fromScratch);

    public Set<Target> addNewTargetsToExecutableSet(Target lastTargetFinished)
    {
        Set<Target> returnedSet = new HashSet<>();
        Boolean addable = true;

        //Check every required-for-target of the last target tasked
        for(Target candidateTarget : lastTargetFinished.getRequireForTargets())
        {
            addable = true;
            //If the current candidate is already skipped - break
            if(candidateTarget.getRuntimeStatus().equals(Target.RuntimeStatus.Skipped))
                break;
            //Check every target the candidate depends on
            for(Target candidateDependsOn : candidateTarget.getDependsOnTargets())
            {
                //If the candidate's depends-on-target is not succeeded (even with warning) - break
                if(candidateDependsOn.getResultStatus().equals(Target.ResultStatus.Failure) ||
                        candidateDependsOn.getResultStatus().equals(Target.ResultStatus.Frozen) )
                    addable = false;
            }
            //The candidate is not skipped and all of the targets it depends on are succeeded
            if(addable)
                returnedSet.add(candidateTarget);
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

    public Graph extractFromXMLToGraph(Path path) throws NotXMLFile, FileNotFound, DoubledTarget, InvalidConnectionBetweenTargets {
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
        graph.calculateProperties();

        return graph;
    }
}