package userInterface;

import graphAnalyzers.PathFinder;
import myExceptions.EmptyGraph;
import myExceptions.NoFailedTargets;
import myExceptions.NoGraphExisted;
import myExceptions.WorkingDirectoryNotFound;
import resources.checker.ResourceChecker;
import target.Graph;
import target.Target;
import graphAnalyzers.CircleFinder;
import task.SimulationTask;
import task.Task;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class UserInteractions implements OutputInterface, InputInterface
{
    //--------------------------------------------------Members-----------------------------------------------------//
    static Scanner scanner = new Scanner(System.in);
    Graph graph;
    Task TaskExecuting = new SimulationTask();
    GraphSummary graphSummary;
    static final Integer MAX_CHOICE = 9;
    private final CircleFinder circleFinder = new CircleFinder();
    private final PathFinder pathFinder = new PathFinder();
    private final ResourceChecker resourceChecker = new ResourceChecker();
    private String workingDirectory;

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
                    case 1: { loadFile(); break; }
                    case 2: { printGraphInformation(); break; }
                    case 3: { printTargetInformation(); break; }
                    case 4: { printTargetConnectionStatus(); break; }
                    case 5: {
                        incremental = askForIncremental();
                        TaskExecuting.executeTask(graph, incremental, graphSummary, new File(workingDirectory).toPath());
                        graphSummary.setFirstRun(false);
                        break;
                    }
                    case 6: { exitFromSystem(); break; }
                    case 7: { saveSystemStatus(); break; }
                    case 8: { loadSystemStatus(); break; }
                    case 9: { checkIfTargetContainedInACircle(); break; }
                    default: { System.out.println("Please enter an option between 1-" + MAX_CHOICE + "!\n"); break; }
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
        System.out.println("1. Load system details from file.");
        System.out.println("2. Show graph details.");
        System.out.println("3. Show target details.");
        System.out.println("4. Find connection between 2 targets.");
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
                System.out.println("Please enter an integer between 1 and " + UserInteractions.MAX_CHOICE + ".");
                scanner.nextLine();
            }
        }
    }

    //------------------------------------------------Option 1------------------------------------------------------//
    @Override
    public void loadFile() {
        Boolean tryAgain = true;

        while (tryAgain) {
            try {
                System.out.println("Please enter the full path of the file you would like to load:");
                String filePath = scanner.nextLine();
                filePath = filePath.trim();

                Path xmlFilePath = Paths.get(filePath);
                graph = resourceChecker.extractFromXMLToGraph(xmlFilePath);
                workingDirectory = resourceChecker.getWorkingDirectoryPath();

                System.out.println("Graph " + graph.getGraphName() + " loaded successfully from " + xmlFilePath.getFileName().toString() + " !");
                graphSummary = new GraphSummary(graph);
                return;
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println("Would you like to try again? (y/n)");
                tryAgain = yesOrNo();
            }
        }
    }

    //------------------------------------------------Option 2------------------------------------------------------//
    public void printGraphInformation() throws NoGraphExisted {
        try {
            if (graph.isEmpty()) {
                System.out.println("The graph is empty!");
                return;
            }

            Map<String, Target> graphTargets = graph.getGraphTargets();
            System.out.println("Number of targets : " + graphTargets.size());
            System.out.println("Number of leaf targets : " + graph.numberOfTargetsByProperty(Target.TargetProperty.LEAF));
            System.out.println("Number of root targets : " + graph.numberOfTargetsByProperty(Target.TargetProperty.ROOT));
            System.out.println("Number of middle targets : " + graph.numberOfTargetsByProperty(Target.TargetProperty.MIDDLE));
            System.out.println("Number of independent targets : " + graph.numberOfTargetsByProperty(Target.TargetProperty.INDEPENDENT));
        } catch (NullPointerException ex) {
            throw new NoGraphExisted();
        }
    }

    //------------------------------------------------Option 3------------------------------------------------------//
    @Override
    public void printTargetInformation() throws NoGraphExisted {
        try {
            if (graph.isEmpty()) {
                System.out.println("The graph is empty!");
                return;
            }

            Boolean tryAgain = true;

            while (tryAgain) {
                if (graph.getGraphTargets().size() == 0) {
                    System.out.println("There are no targets on the graph!");
                    System.out.println("Please load a graph from file.");
                    return;
                }

                System.out.print("Enter target's name: ");
                String targetName = scanner.next();
                Target selectedTarget = graph.getTarget(targetName);

                if (selectedTarget != null)
                {
                    System.out.println("Target's name: " + selectedTarget.getTargetName());
                    System.out.println("Target's property: " + selectedTarget.getTargetProperty());

                    printDependsOnTargets(selectedTarget);
                    printRequiredForTargets(selectedTarget);
                    printTargetExtraInformation(selectedTarget);
                    break;
                }

                System.out.println("There's no target named " + targetName + " in graph.");
                System.out.println("Would you like to try again? (y/n)");
                tryAgain = yesOrNo();
            }
        } catch (NullPointerException ex) {
            throw new NoGraphExisted();
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
    public void printTargetConnectionStatus() throws NoGraphExisted {
        try {
            if (graph.isEmpty()) {
                System.out.println("The graph is empty!");
                return;
            }

            String sourceTargetName, destTargetName;
            Target sourceTarget, destTarget;
            Target.Connection connection;
            Character connectionChoice;

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

                System.out.println("Would you like to find other connections? (y/n)");
                if (!yesOrNo())
                    return;
            }
        } catch (NullPointerException ex) {
            throw new NoGraphExisted();
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
            System.out.println("Would you like to try again? (y/n)");
            if (!yesOrNo())
                return null;
        }
    }

    //------------------------------------------------Option 5------------------------------------------------------//
    public Boolean askForIncremental() throws EmptyGraph, NoGraphExisted, NoFailedTargets {
        try {
            if (graph.isEmpty())
                throw new EmptyGraph();

            if (graphSummary.getFirstRun())
            { //Making a brand-new graph summary for first run
                graphSummary = new GraphSummary(graph);
                return true;
            }

            System.out.println("Would you like to start from scratch? (y/n)");
            Boolean fromScratch = yesOrNo();

            if (fromScratch)
                graphSummary = new GraphSummary(graph);
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
        } catch (NullPointerException ex) {
            throw new NoGraphExisted();
        }
    }

    //------------------------------------------------Option 6------------------------------------------------------//
    @Override
    public void exitFromSystem() {
        System.exit(0);
    }

    //------------------------------------------------Option 7------------------------------------------------------//
    @Override
    public void saveSystemStatus() throws NoGraphExisted {
        if (graph == null)
            throw new NoGraphExisted();

        while (true) {
            System.out.println("Enter the absolute path of the file you'd like to save the system to: ");
            String savePath = scanner.nextLine();

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(savePath))) {
                out.writeObject(graph);
                out.writeObject(graphSummary);
                out.flush();
                out.close();

                //Check if the saving succeeded
                System.out.println("File saved successfully!");
                return;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                System.out.println("Would you like to try again? (y/n)");

                if (!yesOrNo())
                    return;
            }
        }
    }

    //------------------------------------------------Option 8------------------------------------------------------//
    @Override
    public void loadSystemStatus() {
        while (true) {
            System.out.println("Enter the absolute path of the file you'd like to load: ");
            String loadPath = scanner.nextLine();

            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(loadPath))) {
                graph = (Graph) in.readObject();
                graphSummary = (GraphSummary) in.readObject();

                //Check if the loading succeeded
                System.out.println("Loaded " + graphSummary.getGraphName() + " graph successfully from file!");
                return;
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println("Would you like to try again? (y/n)");

                if (!yesOrNo())
                    return;
            }
        }
    }

    //------------------------------------------------Option 9------------------------------------------------------//
    private void checkIfTargetContainedInACircle() throws NoGraphExisted, EmptyGraph {
        try {
            if (graph.isEmpty()) {
                throw new EmptyGraph();
            }

            Target target;
            String targetName;

            System.out.println("Please enter the target you would like to check if it is in the circle :");
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
        } catch (NullPointerException ex) {
            throw new NoGraphExisted();
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