package resources.checker;

import myExeptions.DoubledTarget;
import myExeptions.InvalidConnectionBetweenTargets;
import resources.generated.GPUPDescriptor;
import resources.generated.GPUPTarget;
import resources.generated.GPUPTargetDependencies;
import target.Graph;
import target.Target;
import java.util.*;

public class ResourceChecker{

    enum DependencyType { requiredFor, dependsOn};

    public Graph checkResource(GPUPDescriptor descriptor) throws InvalidConnectionBetweenTargets, DoubledTarget {
        List<GPUPTarget> gpupTargetsAsList = descriptor.getGPUPTargets().getGPUPTarget();
        Graph graph = FillTheGraphWithTargets(gpupTargetsAsList);
        Target currentTarget, secondTarget;
        String currentTargetName, secondTargetName;

        for(GPUPTarget currentgpupTarget : gpupTargetsAsList)
        {
            currentTargetName = currentgpupTarget.getName();
            currentTarget = graph.getGraphTargets().get(currentTargetName);

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

    public Graph FillTheGraphWithTargets(List<GPUPTarget> lst) throws DoubledTarget {
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

    public Boolean checkValidConnectionBetweenTwoTargets(
            Target target1, Target target2,String depType)
    {
        String target1Name = target1.getTargetName(), target2Name = target2.getTargetName();

        if(depType.equals(DependencyType.requiredFor.toString()))
        {
            if(target2.getRequireForTargets().contains(target1))
                return false;
            else//Valid connection between the targets
            {
                target1.addToRequiredFor(target2);
                target2.addToDependsOn(target1);
            }
        }
        else // dep.getType().equals(DependencyType.DependsOn)
        {
            if(target2.getDependsOnTargets().contains(target1)) {
                return false;
            }
            else
            {
                target1.addToDependsOn(target2);
                target2.addToRequiredFor(target1);
            }
        }
        return true;
    }
}