package myExeptions;

public class InvalidConnectionBetweenTargets extends Exception {

    public InvalidConnectionBetweenTargets(String target1, String target2)
    {
        super("Unsynchronized connection between target "
                + target1 + " and target " + target2 + ".");
    }
}