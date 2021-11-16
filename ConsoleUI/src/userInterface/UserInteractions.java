package userInterface;

import com.sun.scenario.effect.impl.prism.ps.PPSBlend_SRC_OUTPeer;
import myExceptions.EmptyGraph;
import myExceptions.FileNotFound;
import myExceptions.NoGraphExisted;
import myExceptions.OpeningFileCrash;
import target.Graph;
import target.Target;
import task.SimulationTask;
import task.Task;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

public class UserInteractions implements OutputInterface, InputInterface {

    static Scanner scanner = new Scanner(System.in);
    Graph graph;
    Task TaskExecuting = new SimulationTask();
    GraphSummary graphSummary;
    static final Integer MAX_CHOICE = 8;
    static final Integer EXIT_PROGRAM = 6;

    public void SystemExecute() {

        System.out.println("Welcome to our project!");
        Integer userSelection = 0;
        Boolean incremental;
        while (true) {
            try {
                printMenu();
                userSelection = getUserSelectionFromMenu();

                switch (userSelection) {
                    case 1: {
                        loadFile();
                        break;
                    }
                    case 2: {
                        printGraphInformation();
                        break;
                    }
                    case 3: {
                        printTargetInformation();
                        break;
                    }
                    case 4: {
                        printTargetConnectionStatus();
                        break;
                    }
                    case 5: {
                        incremental = askForIncremental();
                        TaskExecuting.executeTask(graph, incremental, graphSummary);
                        graphSummary.setFirstRun(false);
                        break;
                    }
                    case 6: {
                        exitFromSystem();
                        break;
                    }
                    case 7: {
                        saveSystemStatus();
                        break;
                    }
                    case 8: {
                        loadSystemStatus();
                        break;
                    }
                    default: {
                        System.out.println("Please enter an option between 1-7!\n");
                        break;
                    }
                }
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }

    public Boolean askForIncremental() throws EmptyGraph, NoGraphExisted {
        try {
            if(graph.isEmpty())
                throw new EmptyGraph();

            if(graphSummary.getFirstRun())
            {
                graphSummary = new GraphSummary(graph);
                return true;
            }

            System.out.println("Would you like to start from scratch? (y/n)");
            Boolean fromScratch = yesOrNo();

            if(fromScratch)
            {
                graphSummary = new GraphSummary(graph);
                TaskExecuting.clearTaskHistory(graph);
            }

            return fromScratch;
        }
        catch(NullPointerException ex)
        {
            throw new NoGraphExisted();
        }

    }

    public void printTargetTaskSummary(TargetSummary targetSummary)
    {
        Duration time = targetSummary.getTime();

        System.out.println("-----------------------");
        System.out.println("Target's name :" + targetSummary.getTargetName());
        System.out.println("Target's result status :" + targetSummary.getResultStatus());
        System.out.format("Target's running time: %02d:%02d:%02d\n", time.toHours(), time.toMinutes(), time.getSeconds());
    }

    @Override
    public void loadFile()
    {
        Boolean tryAgain = true;

        while(tryAgain)
        {
            try {
                System.out.println("Please enter the full path of the file you would like to load:");
                String filePath = scanner.nextLine();
                filePath = filePath.trim();

                Path path = Paths.get(filePath);
                graph = TaskExecuting.extractFromXMLToGraph(path);
                System.out.println("Graph " + graph.getGraphName() + " loaded successfully from " + path.getFileName().toString() + " !");
                graphSummary = new GraphSummary(graph);
                return;
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
                System.out.println("Would you like to try again? (y/n)");
                tryAgain = yesOrNo();
            }
        }
    }

    public static Boolean yesOrNo()
    {
        Character userSelection;
        while (true)
        {
            userSelection = scanner.next().charAt(0);
            scanner.nextLine();

            if (userSelection.toString().toUpperCase().equals("Y"))
                return true;
            else if (userSelection.toString().toUpperCase().equals("N"))
                return false;

            System.out.println("Invalid selection. Try again (y/n): ");
        }
    }

    @Override
    public void printTargetInformation() throws NoGraphExisted {
        try {
            if(graph.isEmpty())
            {
                System.out.println("The graph is empty!");
                return;
            }

            Boolean tryAgain = true;

            while(tryAgain)
            {
                if(graph.getGraphTargets().size() == 0)
                {
                    System.out.println("There're no targets on the graph!");
                    System.out.println("Please load a graph from file.");
                    return;
                }

                System.out.print("Enter target's name: ");
                String targetName = scanner.next();
                if(graph.getGraphTargets().containsKey(targetName))
                {
                    Target selectedTarget = graph.getGraphTargets().get(targetName);
                    System.out.println("Target's name: " + targetName);
                    System.out.println("Target's property: " + selectedTarget.getTargetProperty());

                    printRequiredForTargets(selectedTarget);
                    printDependsOnTargets(selectedTarget);
                    printTargetExtraInformation(selectedTarget);
                    break;
                }

                System.out.println("There's no target named " + targetName + " in graph.");
                System.out.println("Would you like to try again? (y/n)");
                tryAgain = yesOrNo();
            }
        }
        catch(NullPointerException ex)
        {
            throw new NoGraphExisted();
        }

    }

    @Override
    public void printDependsOnTargets(Target target) {
        if(target.getDependsOnTargets().size() == 0)
            System.out.println("The target has no depends-on-targets.");
        else
        {
            System.out.println("List of depends-on-targets: ");
            for(Target currentTarget : target.getDependsOnTargets())
                System.out.print(currentTarget.getTargetName() + " ");
            System.out.println();
        }
    }

    @Override
    public void printRequiredForTargets(Target target) {
        if(target.getRequireForTargets().size() == 0)
            System.out.println("The target has no required-for-targets.");
        else
        {
            System.out.println("List of required-for-targets: ");
            for(Target currentTarget : target.getRequireForTargets())
                System.out.print(currentTarget.getTargetName() + " ");
            System.out.println();
        }
    }

    @Override
    public void printTargetExtraInformation(Target target) {
        if(target.getExtraInformation() != null)
            System.out.println("Target's extra information: " + target.getExtraInformation());
    }

    @Override
    public Integer getUserSelectionFromMenu()
    {
        Integer selection = 0;

        while(true)
        {
            try {
                selection = scanner.nextInt();
                scanner.nextLine();

                if (selection > 0 && selection <= MAX_CHOICE)
                    return selection;
                System.out.println("Please choose between 1 and " + UserInteractions.MAX_CHOICE + ".");
            }
            catch (InputMismatchException ex)
            {
                System.out.println("Please enter an integer between 1 and " + UserInteractions.MAX_CHOICE + ".");
                scanner.nextLine();
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
        System.out.println("7. Save system status.");
        System.out.println("8. Load system status.");
    }

    @Override
    public void printTargetConnectionStatus() throws NoGraphExisted {
        try {
            if(graph.isEmpty())
            {
                System.out.println("The graph is empty!");
                return;
            }

            String sourceTargetName = null, destTargetName = null;
            Target sourceTarget = null, destTarget = null;
            Target.Connection connection;
            Character connectionChoice;

            while (true)
            {
                while (true)
                {
                    System.out.print("Please enter the name of the source target: ");
                    sourceTargetName = scanner.nextLine();
                    sourceTarget = graph.getGraphTargets().get(sourceTargetName);
                    if (sourceTarget != null)
                        break;

                    System.out.println("There's no " + sourceTargetName + " target in the graph.");
                    System.out.println("Would you like to try again? (y/n)");
                    if(!yesOrNo())
                        return;
                }

                while (true)
                {
                    System.out.print("Please enter the name of the destination target: ");
                    destTargetName = scanner.nextLine();
                    destTarget = graph.getGraphTargets().get(destTargetName);
                    if (destTarget != null)
                        break;

                    System.out.println("There's no " + destTargetName + " target in the graph.");
                    System.out.println("Would you like to try again? (y/n)");
                    if(!yesOrNo())
                        return;
                }

                if (!graph.prechecksForTargetsConnection(sourceTargetName, destTargetName)) {
                    System.out.println("There're no paths between " + sourceTargetName + " and " + destTargetName);
                    return;
                }

                while(true)
                {
                    System.out.println("Please enter the connection you'd like to see between the source and destination targets: ");
                    System.out.println("d - Source depends on destination, r - Source required for destination");

                    connectionChoice = scanner.next().charAt(0);
                    scanner.nextLine();

                    if (connectionChoice.toString().toUpperCase().equals("R"))
                    {
                        connection = Target.Connection.REQUIRED_FOR;
                        break;
                    }
                    else if(connectionChoice.toString().toUpperCase().equals("D"))
                    {
                        connection = Target.Connection.DEPENDS_ON;
                        break;
                    }
                    else
                        System.out.print("Please enter a valid choice (d/r): ");
                }

                ArrayList<String> paths = graph.getPathsFromTargets(sourceTarget, destTarget, connection);

                if (paths.size() == 0)
                    System.out.println("There're no paths between " + sourceTargetName + " and " + destTargetName + " as required.");
                else
                {
                    System.out.println("There're " + paths.size() + " paths between " + sourceTargetName + " and " + destTargetName + ":");
                    for (String currentPath : paths)
                        System.out.println(currentPath);
                }

                System.out.println("Would you like to find more connections? (y/n)");
                if(!yesOrNo())
                    return;
            }
        }
        catch(NullPointerException ex)
        {
            throw new NoGraphExisted();
        }
    }

    @Override
    public void exitFromSystem() {
        System.exit(0);
    }

    @Override
    public void saveSystemStatus() throws NoGraphExisted {
        if(graph == null)
            throw new NoGraphExisted();

        while (true) {
            System.out.println("Enter the absolute path of the file you'd like to save the system to: ");
            String savePath = scanner.nextLine();

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(savePath))) {
                out.writeObject(graph);
                out.writeObject(graphSummary);
                out.flush();
                out.close();
                //check if was written successfully
                System.out.println("File saved successfully!");
                return;
            } catch (IOException ex) {
                ex.getMessage();

                System.out.println("Would you like to try again? (y/n)");

                if (!yesOrNo())
                    return;
            }
        }
    }

    @Override
    public void loadSystemStatus()
    {
        while(true)
        {
            System.out.println("Enter the absolute path of the file you'd like to load: ");
            String loadPath = scanner.nextLine();

            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(loadPath))) {
                graph = (Graph)in.readObject();
                graphSummary = (GraphSummary)in.readObject();
                //check if was written successfully
                System.out.println("Loaded " + graphSummary.getGraphName() + " graph successfully from file!");
                return;
            } catch (Exception ex) {
                ex.getMessage();
                System.out.println("Would you like to try again? (y/n)");

                if(!yesOrNo())
                    return;
            }
        }
    }

    public void printTotalExecutionTime()
    {
        Duration totalTimeOfTask = TaskExecuting.getTotalTimeSpentOnTask();
        System.out.format("Total execution time for the task: %02d:%02d:%02d\n", totalTimeOfTask.toHours(), totalTimeOfTask.toMinutes(), totalTimeOfTask.getSeconds());
    }

    public void printGraphInformation() throws NoGraphExisted {
        try {
            if(graph.isEmpty())
            {
                System.out.println("The graph is empty!");
                return;
            }

            Map<String, Target> graphTargets = graph.getGraphTargets();
            System.out.println("Number of targets : " + graphTargets.size());
            System.out.println("Number of leaf targets : " + graph.numberOfTargetsByProperty(Target.TargetProperty.LEAF));
            System.out.println("Number of root targets : " + graph.numberOfTargetsByProperty(Target.TargetProperty.ROOT));
            System.out.println("Number of middle targets : " + graph.numberOfTargetsByProperty(Target.TargetProperty.MIDDLE));
            System.out.println("Number of independent targets : " + graph.numberOfTargetsByProperty(Target.TargetProperty.INDEPENDENT));
        }
        catch(NullPointerException ex)
        {
            throw new NoGraphExisted();
        }
    }

//    public void initGraph()
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
////        target6.addToDependsOn(target4);
////        target4.addToRequiredFor(target6);
//
//        graph.addNewTargetToTheGraph(target1, target2, target3, target4, target5, target6);
//    }
}