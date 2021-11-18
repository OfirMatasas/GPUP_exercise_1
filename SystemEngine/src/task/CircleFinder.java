package task;

import target.Graph;
import target.Target;

public class CircleFinder {
    private Boolean circled = false;
    static private String circlePath;

    public void checkIfCircled(Graph graph, Target target)
    {
        for(Target current : target.getDependsOnTargets())
        {
            checkIfCircledRec(target, current);

            if(circled)
            {
                circlePath = target.getTargetName() + " -> " + circlePath;
                break;
            }
        }
    }

    private void checkIfCircledRec(Target origin, Target current)
    {
        if(current.equals(origin))
        {
            circled = true;
            circlePath = current.getTargetName();
        }

        for(Target nextTarget : current.getDependsOnTargets())
        {
            checkIfCircledRec(origin, nextTarget);
            if(circled)
            {
                circlePath =  nextTarget.getTargetName() +  " -> " + circlePath;
                break;
            }
        }
    }
}