package target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Graph {
    private Map<String, Target> graphTargets;

    public Map<Target.TargetProperty, Integer> getGraphDetails() {
        return graphDetails;
    }

    private Map<Target.TargetProperty, Integer> graphDetails;

    public Map<String, Target> getGraphTargets() {
        return graphTargets;
    }

    public Graph() {
        this.graphTargets = new HashMap<>();
        graphDetails.put(Target.TargetProperty.ROOT,0);
        graphDetails.put(Target.TargetProperty.LEAF,0);
        graphDetails.put(Target.TargetProperty.MIDDLE,0);
        graphDetails.put(Target.TargetProperty.INDEPENDENT,0);
    }

    public void countPropertiesInGraph()
    {// remember to add condition of if invalid file
        Integer curValue=0;
        for(Target curTarget : graphTargets.values())
        {
            switch (curTarget.getTargetProperty()) {
                case LEAF:
                    curValue = graphDetails.get(Target.TargetProperty.LEAF);
                    break;
                case ROOT:
                    curValue = graphDetails.get(Target.TargetProperty.ROOT);
                    break;
                case MIDDLE:
                    curValue = graphDetails.get(Target.TargetProperty.MIDDLE);
                    break;
                case INDEPENDENT:
                    curValue = graphDetails.get(Target.TargetProperty.INDEPENDENT);
                    break;
            }
            curValue++;
        }
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
}