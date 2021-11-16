package userInterface;

import com.sun.javafx.tk.Toolkit;
import myExceptions.EmptyGraph;
import target.Graph;
import target.Target;
import task.Task;

import java.io.FileNotFoundException;

public interface OutputInterface {
    public void printMenu();
    public void printTargetInformation();
    public void printDependsOnTargets(Target target);
    public void printRequiredForTargets(Target target);
    public void printTargetExtraInformation(Target target);
    public void printGraphInformation() throws EmptyGraph;
    public void printTargetConnectionStatus() throws EmptyGraph;
    public void exitFromSystem();
    public void saveSystemStatus() throws FileNotFoundException;
    public void loadSystemStatus() throws FileNotFoundException;
}
