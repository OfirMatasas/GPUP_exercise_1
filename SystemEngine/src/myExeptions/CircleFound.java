package myExeptions;

public class CircleFound extends Exception {

    public CircleFound()
    {
        super("There's a circle in the graph.");
    }
}