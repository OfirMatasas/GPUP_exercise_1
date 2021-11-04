package userInterface;

import target.Graph;
import target.Target;

public interface OutputInterface {
    public void printMenu();
    public void printTargetInformation();
    public void printDependsOnTargets(Target target);
    public void printRequiredForTargets(Target target);
    public void printTargetExtraInformation(Target target);
    public void printGraphInformation();
    public void printTargetConnectionStatus();
    public void printExceptionInformation(String message);
}
