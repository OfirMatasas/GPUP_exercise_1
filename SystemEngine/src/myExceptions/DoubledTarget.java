package myExceptions;

public class DoubledTarget extends Exception {

    public DoubledTarget(String targetName)
    {
        super("The target " + targetName + " appear at least 2 times in the file.");
    }
}