package target;

import java.util.*;

public class Graph {
    private Map<String, Target> graphTargets;
    private Map<Target.TargetProperty, Set<Target>> targetsByProperties;

    public Graph() {
        this.graphTargets = new HashMap<>();
        this.targetsByProperties = new HashMap<>();
        targetsByProperties.put(Target.TargetProperty.LEAF, new HashSet<>());
        targetsByProperties.put(Target.TargetProperty.INDEPENDENT, new HashSet<>());
        targetsByProperties.put(Target.TargetProperty.ROOT, new HashSet<>());
        targetsByProperties.put(Target.TargetProperty.MIDDLE, new HashSet<>());
    }

    public int numberOfTargetsByProperty(Target.TargetProperty property)
    {
        return targetsByProperties.get(property).size();
    }

    public Map<String, Target> getGraphTargets() {
        return graphTargets;
    }

    public void addNewTargetToTheGraph(Target... newTargets) {
        for(Target currentTarget : newTargets)
            graphTargets.put(currentTarget.getTargetName(), currentTarget);

        calculateProperties();
    }

    public Boolean prechecksForTargetsConnection(String src, String dest)
    {
        Target source ,destination;
        source= graphTargets.get(src);
        destination= graphTargets.get(dest);

        if (source.getTargetProperty().equals(destination.getTargetProperty()))
        {
            if(source.getTargetProperty()== Target.TargetProperty.ROOT||source.getTargetProperty() == Target.TargetProperty.LEAF)
                return  false;
        }
        else if(source.getTargetProperty().equals(Target.TargetProperty.INDEPENDENT)||destination.getTargetProperty().equals(Target.TargetProperty.INDEPENDENT))
            return  false;

        return  true;
    }

    public ArrayList<String> getPathsFromTargets(Target source, Target dest, Target.Connection connection)
    {
        ArrayList<String> currentPath = new ArrayList<>();
        ArrayList<String> returnedPaths;
        Set<Target> nextTargetsOnCurrentPath;

        if(source.equals(dest))
        {
            currentPath.add(source.getTargetName());
            return currentPath;
        }
        else if(source.getWasVisited().equals(true))
            return currentPath;
        else if (connection.equals(Target.Connection.DEPENDS_ON))
            nextTargetsOnCurrentPath = source.getDependsOnTargets();
        else // (connection.equals(Target.Connection.REQUIRED_FOR))
            nextTargetsOnCurrentPath = source.getRequireForTargets();

        if(nextTargetsOnCurrentPath.size() == 0)
            return currentPath;

        for(Target currentTarget : nextTargetsOnCurrentPath)
        {
            returnedPaths = getPathsFromTargets(currentTarget, dest, connection);

            for(String path : returnedPaths)
                currentPath.add(source.getTargetName() + " -> " + path);
        }

        return currentPath;
    }

    private void calculateProperties()
    {
        clearTargetsByProperties();
        Integer valCount = 0;
        Target.TargetProperty currentTargetProperty;

        for(Target currentTarget : graphTargets.values())
        {
            if(currentTarget.getRequireForTargets().size() == 0 && currentTarget.getDependsOnTargets().size() == 0)
            {//Independent
                currentTargetProperty = Target.TargetProperty.INDEPENDENT;
            }
            else if(currentTarget.getRequireForTargets().size() == 0)
            {//Leaf
                currentTargetProperty = Target.TargetProperty.LEAF;
            }
            else if(currentTarget.getDependsOnTargets().size() == 0)
            {//Root
                currentTargetProperty = Target.TargetProperty.ROOT;
            }
            else
            {//Middle
                currentTargetProperty = Target.TargetProperty.MIDDLE;
            }
            targetsByProperties.get(currentTargetProperty).add(currentTarget);
            currentTarget.setTargetProperty(currentTargetProperty);
        }

    }

    private void clearTargetsByProperties()
    {
        targetsByProperties.get(Target.TargetProperty.ROOT).clear();
        targetsByProperties.get(Target.TargetProperty.MIDDLE).clear();
        targetsByProperties.get(Target.TargetProperty.LEAF).clear();
        targetsByProperties.get(Target.TargetProperty.INDEPENDENT).clear();
    }

    public void setAllTargetResultStatusToDefault()
    {
        for(Target currentTarget : graphTargets.values())
            currentTarget.setResultStatus(Target.ResultStatus.Frozen);
    }

    public void setAllTargetruntimeStatusToDefault()
    {
        for(Target currentTarget : graphTargets.values())
            currentTarget.setRuntimeStatus(Target.RuntimeStatus.Waiting);
    }

    public void setAllTargetWasVisitedToDefault()
    {
        for(Target currentTarget : graphTargets.values())
            currentTarget.setWasVisited(false);
    }
}