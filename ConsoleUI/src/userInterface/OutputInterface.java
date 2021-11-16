package userInterface;

import com.sun.javafx.tk.Toolkit;
import myExceptions.EmptyGraph;
import myExceptions.NoGraphExisted;
import target.Graph;
import target.Target;
import task.Task;

import java.io.FileNotFoundException;

public interface OutputInterface {
    public void printMenu();
    public void printTargetInformation() throws NoGraphExisted;
    public void printDependsOnTargets(Target target);
    public void printRequiredForTargets(Target target);
    public void printTargetExtraInformation(Target target);
    public void printGraphInformation() throws EmptyGraph, NoGraphExisted;
    public void printTargetConnectionStatus() throws EmptyGraph, NoGraphExisted;
    public void exitFromSystem();
    public void saveSystemStatus() throws FileNotFoundException, NoGraphExisted;
    public void loadSystemStatus() throws FileNotFoundException;
}
