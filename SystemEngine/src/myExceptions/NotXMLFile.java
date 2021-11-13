package myExceptions;

public class NotXMLFile extends Exception{

    public NotXMLFile(String fileName)
    {
        super("File " + fileName + " is not xml type.");
    }
}