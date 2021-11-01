package userInterface;

import target.Graph;
import target.Target;

public interface OutputInterface {
    public void PrintMenu();
    public void PrintTargetInformation();
    public void PrintDependsOnTargets(Target target);
    public void PrintRequiredForTargets(Target target);
    public void PrintTargetExtraInformation(Target target);
    public void PrintGraphInformation();
    public void PrintTargetConnectionStatus(String target1, String target2);
    public void PrintExceptionInformation(String message);
}
