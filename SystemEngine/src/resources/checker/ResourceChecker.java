package resources.checker;

import myExceptions.*;
import resources.generated.GPUPDescriptor;
import resources.generated.GPUPTarget;
import resources.generated.GPUPTargetDependencies;
import target.Graph;
import target.Target;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ResourceChecker{

    private enum DependencyType { requiredFor, dependsOn};

    public Graph extractFromXMLToGraph(Path path) throws NotXMLFile, FileNotFound, DoubledTarget, InvalidConnectionBetweenTargets, EmptyGraph {
        if(!path.getFileName().toString().endsWith(".xml"))
            throw new NotXMLFile(path.getFileName().toString());
        else if(!Files.isExecutable(path))
            throw new FileNotFound(path.getFileName().toString());

        //The file can be executed
        GPUPDescriptor descriptor = fromXmlFileToObject(path);
        Graph graph = checkResource(descriptor);

        if(graph != null)
            graph.calculateProperties();

        return graph;
    }

    private GPUPDescriptor fromXmlFileToObject(Path fileName)
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

    private Graph checkResource(GPUPDescriptor descriptor) throws InvalidConnectionBetweenTargets, DoubledTarget, EmptyGraph {
        List<GPUPTarget> gpupTargetsAsList = descriptor.getGPUPTargets().getGPUPTarget();
        Graph graph = FillTheGraphWithTargets(gpupTargetsAsList);
        graph.setGraphName(descriptor.getGPUPConfiguration().getGPUPGraphName());
        Target currentTarget, secondTarget;
        String currentTargetName, secondTargetName;

        if(descriptor.getGPUPTargets() == null || descriptor.getGPUPTargets().getGPUPTarget() == null)
            throw new EmptyGraph();

        for(GPUPTarget currentgpupTarget : gpupTargetsAsList)
        {
            currentTargetName = currentgpupTarget.getName();
            currentTarget = graph.getGraphTargets().get(currentTargetName);

            if(currentgpupTarget.getGPUPTargetDependencies() == null)
                continue;

            for(GPUPTargetDependencies.GPUGDependency dep :
                    currentgpupTarget.getGPUPTargetDependencies().getGPUGDependency())
            {
                secondTarget = graph.getGraphTargets().get(dep.getValue());
                if(secondTarget == null)
                    throw new InvalidConnectionBetweenTargets(currentTargetName, dep.getValue());

                secondTargetName = secondTarget.getTargetName();

                if(!checkValidConnectionBetweenTwoTargets(currentTarget, secondTarget, dep.getType()))
                {
                    throw new InvalidConnectionBetweenTargets(currentTargetName, secondTargetName);
                }
            }
        }

        return graph;
    }

    private Graph FillTheGraphWithTargets(List<GPUPTarget> lst) throws DoubledTarget {
        Graph graph = new Graph();
        Target newTarget;

        for(GPUPTarget currentTarget : lst)
        {
            if(graph.getGraphTargets().get(currentTarget.getName()) != null)
                throw new DoubledTarget(currentTarget.getName());

            newTarget = new Target();
            newTarget.setTargetName(currentTarget.getName());
            newTarget.setExtraInformation(currentTarget.getGPUPUserData());

            graph.addNewTargetToTheGraph(newTarget);
        }

        return graph;
    }

    private Boolean checkValidConnectionBetweenTwoTargets(Target target1, Target target2,String depType)
    {
        String target1Name = target1.getTargetName(), target2Name = target2.getTargetName();

        if(depType.equals(DependencyType.dependsOn.toString()))
        {
            if(target2.getDependsOnTargets().contains(target1))
                return false;
            else//Valid connection between the targets
            {
                target1.addToDependsOn(target2);
                target2.addToRequiredFor(target1);
            }
        }
        else // dep.getType().equals(DependencyType.DependsOn)
        {
            if(target2.getRequiredForTargets().contains(target1)) {
                return false;
            }
            else
            {
                target1.addToRequiredFor(target2);
                target2.addToDependsOn(target1);
            }
        }
        return true;
    }
}