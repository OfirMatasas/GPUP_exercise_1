package userInterface;

import Summaries.GraphSummary;
import Summaries.TargetSummary;
import graphAnalyzers.PathFinder;
import myExceptions.EmptyGraph;
import myExceptions.NoFailedTargets;
import myExceptions.NoGraphExisted;
import resources.checker.ResourceChecker;
import target.Graph;
import target.Target;
import graphAnalyzers.CircleFinder;
import task.SimulationTask;
import task.Task;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class UserInteractions implements OutputInterface, InputInterface
{
    //--------------------------------------------------Members-----------------------------------------------------//
    static Scanner scanner = new Scanner(System.in);
    static final Integer MAX_CHOICE = 9;
    Task runningTask = new SimulationTask();
    Graph graph;
    GraphSummary graphSummary;

    //----------------------------------------------------Menu------------------------------------------------------//
    public void SystemExecute() {

        System.out.println("Welcome to our project!");
        Integer userSelection;
        Boolean incremental;
//        initGraph();
        while (true) {
            try {
                printMenu();
                userSelection = getUserSelectionFromMenu();

                switch (userSelection) {
                    case 1: { loadXMLFile(); break; }
                    case 2: { printGraphInformation(); break; }
                    case 3: { printTargetInformation(); break; }
                    case 4: { printTargetConnectionStatus(); break; }
                    case 5: {
                        incremental = askForIncremental();
                        runningTask.execute(graph, incremental, graphSummary);
                        graphSummary.setFirstRun(false);
                        System.out.println("\nReturning to main menu.\n");
                        break;
                    }
                    case 6: { exitFromSystem(); break; }
                    case 7: { saveSystemStatus(); break; }
                    case 8: { loadSystemStatus(); break; }
                    case 9: { checkIfTargetContainedInACircle(); break; }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println("Returning to main menu.\n");
            }
        }
    }

    @Override
    public void printMenu() {
        System.out.println("Please choose from the options below:");
        System.out.println("1. Load graph from XML file.");
        System.out.println("2. Show graph details.");
        System.out.println("3. Show target details.");
        System.out.println("4. Find connections between 2 targets.");
        System.out.println("5. Execute task.");
        System.out.println("6. Exit.");
        System.out.println("\nBonuses:");
        System.out.println("7. Save system status.");
        System.out.println("8. Load system status.");
        System.out.println("9. Check if target is in a circle.");
    }

    @Override
    public Integer getUserSelectionFromMenu() {
        Integer selection;

        while (true) {
            try {
                selection = scanner.nextInt();
                scanner.nextLine();

                if (selection > 0 && selection <= MAX_CHOICE)
                    return selection;
                System.out.println("Please choose between 1 and " + UserInteractions.MAX_CHOICE + ".");
            } catch (InputMismatchException ex) {
                System.out.println("Please enter an integer between 1 and " + UserInteractions.MAX_CHOICE + ".\n");
                printMenu();
                scanner.nextLine();
            }
        }
    }

    //------------------------------------------------Option 1------------------------------------------------------//
    @Override
    public void loadXMLFile() {
        while (true) {
            try {
                System.out.println("\nPlease enter the full path of the xml file you would like to load:");
                String filePath = scanner.nextLine();
                filePath = filePath.trim();
                ResourceChecker resourceChecker = new ResourceChecker();

                Path xmlFilePath = Paths.get(filePath);
                graph = resourceChecker.extractFromXMLToGraph(xmlFilePath);

                System.out.println("Graph " + graph.getGraphName() + " loaded successfully from " + xmlFilePath.getFileName().toString() + " !");
                graphSummary = new GraphSummary(graph,resourceChecker.getWorkingDirectoryPath());
                runningTask.setTargetsParameters(null);
                System.out.println("\nReturning to main menu.\n");
                return;
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println("Would you like to try again? (y/n)");
                if(!yesOrNo())
                {
                    System.out.println("\nReturning to main menu.\n");
                    return;
                }
            }
        }
    }

    //------------------------------------------------Option 2------------------------------------------------------//
    public void printGraphInformation(){
        if (checkForExistedOrEmptyGraph()) return;

        Map<String, Target> graphTargets = graph.getGraphTargets();
        System.out.println("\nGraph " + graph.getGraphName() + " details:");
        System.out.println("Number of targets: " + graphTargets.size());
        System.out.println("Number of leaf targets: " + graph.numberOfTargetsByProperty(Target.TargetProperty.LEAF));
        System.out.println("Number of root targets: " + graph.numberOfTargetsByProperty(Target.TargetProperty.ROOT));
        System.out.println("Number of middle targets: " + graph.numberOfTargetsByProperty(Target.TargetProperty.MIDDLE));
        System.out.println("Number of independent targets: " + graph.numberOfTargetsByProperty(Target.TargetProperty.INDEPENDENT));
        System.out.println("\nReturning to main menu.\n");
    }

    //------------------------------------------------Option 3------------------------------------------------------//
    @Override
    public void printTargetInformation()
    {
        if (checkForExistedOrEmptyGraph()) return;

        while (true)
        {
            System.out.print("\nEnter target's name: ");
            String targetName = scanner.next();
            Target selectedTarget = graph.getTarget(targetName);

            if (selectedTarget != null)
            {
                System.out.println("Target's name: " + selectedTarget.getTargetName());
                System.out.println("Target's property: " + selectedTarget.getTargetProperty());

                printDependsOnTargets(selectedTarget);
                printRequiredForTargets(selectedTarget);
                printTargetExtraInformation(selectedTarget);
            }
            else
                System.out.println("There's no target named " + targetName + " in graph.");

            System.out.println("\nWould you like check for another target (y/n)?");
            if (!yesOrNo())
            {
                System.out.println("\nReturning to main menu.\n");
                return;
            }
        }
    }

    @Override
    public void printTargetExtraInformation(Target target) {
        if (target.getExtraInformation() != null)
            System.out.println("Target's extra information: " + target.getExtraInformation());
        else
            System.out.println("There's no extra information about this target.");
    }

    @Override
    public void printDependsOnTargets(Target target) {
        if (target.getDependsOnTargets().size() == 0)
            System.out.println("The target has no depends-on-targets.");
        else {
            System.out.println("List of directly depends-on-targets: ");
            for (Target currentTarget : target.getDependsOnTargets())
                System.out.print(currentTarget.getTargetName() + " ");
            System.out.println();
        }
    }

    @Override
    public void printRequiredForTargets(Target target) {
        if (target.getRequiredForTargets().size() == 0)
            System.out.println("The target has no required-for-targets.");
        else {
            System.out.println("List of directly required-for-targets: ");
            for (Target currentTarget : target.getRequiredForTargets())
                System.out.print(currentTarget.getTargetName() + " ");
            System.out.println();
        }
    }

    //------------------------------------------------Option 4------------------------------------------------------//
    @Override
    public void printTargetConnectionStatus() {
        if (checkForExistedOrEmptyGraph()) return;

        PathFinder pathFinder = new PathFinder();
        String sourceTargetName, destTargetName;
        Target sourceTarget, destTarget;
        Target.Connection connection;
        Character connectionChoice;

        System.out.println();
        while (true) {
            sourceTarget = getTargetFromUser("source");
            if (sourceTarget == null)
                return;

            destTarget = getTargetFromUser("destination");
            if (destTarget == null)
                return;

            sourceTargetName = sourceTarget.getTargetName();
            destTargetName = destTarget.getTargetName();

            if (!pathFinder.prechecksForTargetsConnection(sourceTargetName, destTargetName, graph)) {
                System.out.println("There are no paths between " + sourceTargetName + " and " + destTargetName);
            } else {
                while (true) {
                    System.out.println("Please enter the connection you'd like to see between the source and destination targets: ");
                    System.out.println("D - Source depends on destination, R - Source required for destination");

                    connectionChoice = scanner.next().charAt(0);
                    scanner.nextLine();

                    if (connectionChoice.toString().equalsIgnoreCase("R")) {
                        connection = Target.Connection.REQUIRED_FOR;
                        break;
                    } else if (connectionChoice.toString().equalsIgnoreCase("D")) {
                        connection = Target.Connection.DEPENDS_ON;
                        break;
                    } else
                        System.out.print("Please enter a valid choice (D/R): ");
                }

                ArrayList<String> paths = pathFinder.getPathsFromTargets(sourceTarget, destTarget, connection);

                if (paths.size() == 0)
                    System.out.println("There are no paths between " + sourceTargetName + " and " + destTargetName + " as required.");
                else {
                    System.out.println("There are " + paths.size() + " paths between " + sourceTargetName + " and " + destTargetName + ":");
                    paths.forEach(System.out::println);
                }
            }

            System.out.println("\nWould you like to find other connections? (y/n)");
            if (!yesOrNo()) {
                System.out.println("\nReturning to main menu.\n");
                return;
            }

        }
    }

    public Target getTargetFromUser(String srcOrDest) {
        Target target;
        String targetName;

        while (true) {
            System.out.print("Please enter the name of the " + srcOrDest + " target: ");
            targetName = scanner.nextLine();
            target = graph.getTarget(targetName);
            if (target != null)
                return target;

            System.out.println("There's no " + targetName + " target in the graph.");
            System.out.println("\nWould you like to try another target? (y/n)");
            if (!yesOrNo())
            {
                System.out.println("\nReturning to main menu.\n");
                return null;
            }
        }
    }

    //------------------------------------------------Option 5------------------------------------------------------//
    public Boolean askForIncremental() throws EmptyGraph, NoGraphExisted, NoFailedTargets {
        if(graph == null)
            throw new NoGraphExisted();
        else if (graph.isEmpty())
            throw new EmptyGraph();

        if (graphSummary.getFirstRun())
        { //Making a brand-new graph summary for first run
            return true;
        }

        System.out.println("\nWould you like to start from scratch? (y/n)");
        Boolean fromScratch = yesOrNo();

        if (fromScratch)
            graphSummary = new GraphSummary(graph, graphSummary.getWorkingDirectory());
        else
        {
            //If all the targets in the graph are already succeeded - no need for running a new task
            if(graphSummary.getTargetsSummaryMap()
                    .values()
                    .stream()
                    .noneMatch(ts -> ts.getResultStatus().equals(TargetSummary.ResultStatus.Failure)))
            {
                throw new NoFailedTargets();
            }
        }

        return fromScratch;
    }

    //------------------------------------------------Option 6------------------------------------------------------//
    @Override
    public void exitFromSystem()
    {
        System.out.println("Exiting the project...");
        System.exit(0);
    }

    //------------------------------------------------Option 7------------------------------------------------------//
    @Override
    public void saveSystemStatus() {
        if (checkForExistedOrEmptyGraph()) return;

        while (true) {
            System.out.println("\nEnter the name of the file you would like to save: ");
            System.out.println("(If entered the file's name only, the file will be saved in the application folder)");
            System.out.println("(If entered full path, Make sure the directory of the file is already existed)");
            String savePath = scanner.nextLine();
            Path filePath = Paths.get(savePath);
            Boolean invalid = true;
            String errorMessage = null;

            if(Files.isDirectory(filePath))
                errorMessage = "Invalid file: " + savePath +" is a path to a directory." +
                        "\nWould you like to try again (y/n)?";
            else if(Files.exists(filePath))
                errorMessage = "The file " + filePath.getFileName() +
                        " is already exist.\nWould you like to overwrite (y/n)?";
            else invalid = false;

            if(invalid)
            {
                System.out.println(errorMessage);
                if (!yesOrNo()) {
                    System.out.println("\nReturning to main menu.\n");
                    return;
                }
            }

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(savePath))) {
                out.writeObject(graph);
                out.writeObject(graphSummary);

                //Check if the saving succeeded
                System.out.println("File saved successfully!");
                System.out.println("\nReturning to main menu.\n");
                return;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                System.out.println("Would you like to try again? (y/n)");

                if (!yesOrNo())
                {
                    System.out.println("\nReturning to main menu.\n");
                    return;
                }
            }
        }
    }

    //------------------------------------------------Option 8------------------------------------------------------//
    @Override
    public void loadSystemStatus() {
        while (true)
        {
            System.out.println("\nEnter the name of the file (that you've saved before) you would like to load: ");
            System.out.println("(If entered the file's name only, the system will try to load the file from the current folder)");
            System.out.println("(If entered full path, Make sure the file and the directory of the file are already existed)");
            String loadPath = scanner.nextLine();
            Path filePath = Paths.get(loadPath);
            String errorMessage = null;
            Boolean invalid = true;


            if(!Files.exists(filePath))
                errorMessage = "Invalid path: file not existed.\n" +
                        "Would you like to try again (y/n)?";
            else if(Files.isDirectory(filePath))
                errorMessage = "Invalid file : " + loadPath + " is a path to a directory.\n" +
                        "Would you like to try again (y/n)?";
            else invalid = false;

            if(invalid)
            {
                System.out.println(errorMessage);
                if (!yesOrNo()) {
                    System.out.println("\nReturning to main menu.\n");
                    return;
                }
                continue;
            }

            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(loadPath))) {
                graph = (Graph) in.readObject();
                graphSummary = (GraphSummary) in.readObject();
                //Check if the loading succeeded
                System.out.println("Loaded " + graphSummary.getGraphName() + " graph successfully from file!");
                System.out.println("\nReturning to main menu.\n");
                runningTask.setTargetsParameters(null);
                return;
            }
            catch(StreamCorruptedException ex)
            {
                System.out.println("The file does not contain any graph!");
                System.out.println("\nWould you like to try again (y/n)?");
                if (!yesOrNo()) {
                    System.out.println("\nReturning to main menu.\n");
                    return;
                }
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println("\nWould you like to try again? (y/n)");

                if (!yesOrNo()) {
                    System.out.println("\nReturning to main menu.\n");
                    return;
                }
            }
        }
    }

    //------------------------------------------------Option 9------------------------------------------------------//
    private void checkIfTargetContainedInACircle() {
        if (checkForExistedOrEmptyGraph()) return;

        Target target;
        String targetName;
        CircleFinder circleFinder = new CircleFinder();

        while(true) {

            System.out.println("\nPlease enter the target you would like to check if it is in the circle:");
            targetName = scanner.nextLine();
            target = graph.getTarget(targetName);

            if (target != null) {
                circleFinder.checkIfCircled(target);
                if (circleFinder.getCircled())
                    System.out.println(circleFinder.getCirclePath());
                else
                    System.out.println("The target " + targetName + " is not in a circle.");
            } else
                System.out.println("There is no " + targetName + " in the graph!");

            System.out.println("\nWould you like to check for more circles (y/n)?");
            if(!yesOrNo()) {
                System.out.println("\nReturning to main menu.\n");
                return;
            }
        }
    }

    //-------------------------------------------------Generic------------------------------------------------------//
    public static Boolean yesOrNo() {
        Character userSelection;
        while (true) {
            userSelection = scanner.next().charAt(0);
            scanner.nextLine();

            if (userSelection.toString().equalsIgnoreCase("Y"))
                return true;
            else if (userSelection.toString().equalsIgnoreCase("N"))
                return false;

            System.out.println("Invalid selection. Try again (y/n): ");
        }
    }

    private boolean checkForExistedOrEmptyGraph() {
        if (graph == null) {
            System.out.println("Please load a graph first!");
            System.out.println("\nReturning to main menu.\n");
            return true;
        }
        else if (graph.isEmpty()) {
            System.out.println("The graph is empty!");
            System.out.println("\nReturning to main menu.\n");
            return true;
        }
        return false;
    }

    //-------------------------------------------------Graphs-------------------------------------------------------//

//    public void initGraph() { //Circled
//        Target target1 = new Target(), target2 = new Target(), target3 = new Target();
//        Target target4 = new Target(), target5 = new Target(), target6 = new Target();
//
//        target1.setTargetName("A");
//        target2.setTargetName("B");
//        target3.setTargetName("C");
//        target4.setTargetName("D");
//        target5.setTargetName("E");
//        target6.setTargetName("F");
//
//
//        target1.addToDependsOn(target2);
//        target2.addToDependsOn(target3);
//        target3.addToDependsOn(target4);
//        target4.addToDependsOn(target5);
//        target5.addToDependsOn(target6);
//        target6.addToDependsOn(target1);
//
//        graph = new Graph();
//        graph.addNewTargetToTheGraph(target1, target2, target3, target4, target5, target6);
//    }
//
//
//    public void initGraph() //Generic graph
//    {
//        Target target1 = new Target(), target2 = new Target(), target3 = new Target(), target4 = new Target();
//        Target target5 = new Target(), target6 = new Target();
//
//        target1.setTargetName("A");
//        target1.setExtraInformation("The root of the graph.");
//
//        target2.setTargetName("B");
//        target2.setExtraInformation("First son of A");
//
//        target3.setTargetName("C");
//        target3.setExtraInformation("Second son of A");
//
//        target4.setTargetName("D");
//        target4.setExtraInformation("Leaf in the graph");
//
//        target5.setTargetName("E");
//        target5.setExtraInformation("Independent target");
//
//        target6.setTargetName("F");
//        target6.setExtraInformation("Leaf in the graph");
//
//        target6.addToRequiredFor(target2);
//        target3.addToDependsOn(target4);
//        target1.addToDependsOn(target3);
//        target4.addToRequiredFor(target2);
//        target4.addToRequiredFor(target3);
//        target2.addToRequiredFor(target1);
//        target2.addToDependsOn(target6);
//        target2.addToDependsOn(target4);
//        target3.addToRequiredFor(target1);
//        target1.addToDependsOn(target2);
//
//        graph = new Graph();
//        graph.addNewTargetToTheGraph(target1, target2, target3, target4, target5, target6);
//    }
}