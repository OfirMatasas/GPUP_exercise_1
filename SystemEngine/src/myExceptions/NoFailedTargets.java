package myExceptions;

public class NoFailedTargets extends Exception {

    public NoFailedTargets()
    {
        super("All targets are already passed the tasks!");
    }
}
