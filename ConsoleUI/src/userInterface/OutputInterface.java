package userInterface;

import myExceptions.EmptyGraph;
import myExceptions.NoGraphExisted;
import target.Target;
import java.io.FileNotFoundException;

public interface OutputInterface {
    void printMenu();
    void printTargetInformation() throws NoGraphExisted;
    void printDependsOnTargets(Target target);
    void printRequiredForTargets(Target target);
    void printTargetExtraInformation(Target target);
    void printGraphInformation() throws EmptyGraph, NoGraphExisted;
    void printTargetConnectionStatus() throws EmptyGraph, NoGraphExisted;
    void exitFromSystem();
    void saveSystemStatus() throws FileNotFoundException, NoGraphExisted;
    void loadSystemStatus() throws FileNotFoundException;
}
