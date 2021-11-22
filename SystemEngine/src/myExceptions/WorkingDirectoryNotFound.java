package myExceptions;

public class WorkingDirectoryNotFound extends Exception {

    public WorkingDirectoryNotFound(String workingDirectoryPath) {
        super("Working directory " + workingDirectoryPath + " not found ");
    }
}