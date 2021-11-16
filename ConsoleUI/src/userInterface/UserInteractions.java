package userInterface;

import myExceptions.EmptyGraph;
import myExceptions.FileNotFound;
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
    Graph graph = new Graph();
    Task TaskExecuting = new SimulationTask();
    Boolean firstRun = true;
    GraphSummary graphSummary;
    static final Object EXIT = null;
    static final Integer MAX_CHOICE = 8;

    public void SystemExecute() {

        System.out.println("Welcome to our project!");
        Integer userSelection = 0;
        Boolean incremental;
        try {
            while (userSelection != 6) {

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
                        try {
                            TaskExecuting.executeTask(graph, incremental, graphSummary);
                        } catch (OpeningFileCrash | FileNotFound e) {
                            System.out.println(e.getMessage());
                        }
                        firstRun = false;
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
        }
        catch (InputMismatchException ex) {
            System.out.println(ex.getMessage());
        }
        catch (EmptyGraph ex)
        {
            System.out.println(ex.getMessage());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Boolean askForIncremental() {
        if(firstRun)
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
                scanner.nextLine();
                String filePath = scanner.nextLine();
                filePath = filePath.trim();

                Path path = Paths.get(filePath);
                graph = TaskExecuting.extractFromXMLToGraph(path);
                System.out.println("Graph loaded successfully from " + path.getFileName().toString() + " !");
                firstRun = true;
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
        Character userSelection = 'j';
        while (!userSelection.equals('y') && (!userSelection.equals('Y')) && (!userSelection.equals('n')) && (!userSelection.equals('N')))
        {
            userSelection = scanner.next().charAt(0);

            if (userSelection.toString().toUpperCase().equals("Y"))
                return true;
            else if (userSelection.toString().toUpperCase().equals("N"))
                return false;
            else
            {
                System.out.println("Invalid selection. Try again (y/n): ");
                scanner.nextLine();
            }
        }
        return userSelection.equals('y') || userSelection.equals('Y');
    }

    @Override
    public void printTargetInformation()
    {
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
        try {
            Integer selection = 0;

            while(selection <= 0 || selection > MAX_CHOICE)
            {
                selection = scanner.nextInt();

                if(selection <= 0 || selection > MAX_CHOICE)
                    System.out.println("Please choose between 1 and " + UserInteractions.MAX_CHOICE + ".");

            }

            return selection;
        }
        catch(InputMismatchException ex)
        {
            System.out.println("Please enter an integer between 1 and " + UserInteractions.MAX_CHOICE + ".");
            return getUserSelectionFromMenu();
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
    public void printTargetConnectionStatus() throws EmptyGraph
    {
        if (graph.getGraphTargets().size() == 0)
            throw new EmptyGraph();

        else {
            String sourceTargetName = null, destTargetName = null;
            Target sourceTarget = null, destTarget = null;
            Target.Connection connection = Target.Connection.DEPENDS_ON;

            int toContinueTmp = 1;
            int connectionChoice = 0;

            while (toContinueTmp != 0) {
                System.out.print("Please enter the name of the source target: ");
                sourceTargetName = scanner.next();
                System.out.print("Please enter the name of the destination target: ");
                destTargetName = scanner.next();
                sourceTarget = graph.getGraphTargets().get(sourceTargetName);
                destTarget = graph.getGraphTargets().get(destTargetName);

                if (sourceTarget == null || destTarget == null) {
                    if (graph.getGraphTargets().get(sourceTargetName) == null)
                        System.out.println("The source target you've entered doesn't exist in the graph!");
                    else
                        System.out.println("The destination target you've entered doesn't exist in the graph!");

                    System.out.println("Would you like to try again? (0 - no, 1 - yes)");
                    toContinueTmp = scanner.nextInt();

                    if (toContinueTmp == 0)
                        return;
                } else
                    break;
            }

            if (!graph.prechecksForTargetsConnection(sourceTargetName, destTargetName)) {
                System.out.println("There're no paths between " + sourceTargetName + " and " + destTargetName);
                return;
            }

            System.out.print("Please enter the connection you'd like to see between the source and destination targets: ");
            System.out.println("0 - Source depends on destination, anything else - Source required for destination");
            connectionChoice = scanner.nextInt();
            if (connectionChoice != 0)
                connection = Target.Connection.REQUIRED_FOR;

            ArrayList<String> paths = graph.getPathsFromTargets(sourceTarget, destTarget, connection);

            if (paths.size() == 0)
                System.out.println("There're no paths between " + sourceTargetName + " and " + destTargetName + " as required.");
            else {
                System.out.println("There're " + paths.size() + " paths between " + sourceTargetName + " and " + destTargetName + ":");
                for (String currentPath : paths)
                    System.out.println(currentPath);
            }
        }
    }

    @Override
    public void exitFromSystem() {
        System.exit(0);
    }

    @Override
    public void saveSystemStatus() throws FileNotFoundException {
        String savePath= getPathFromUser();
        try(ObjectOutputStream out= new ObjectOutputStream(new FileOutputStream(savePath)))
        {
            out.writeObject(graphSummary);
            out.flush();
            //check if was written succesfully
        } catch (IOException ex)
        {
           ex.getMessage();
        }

    }

    @Override
    public void loadSystemStatus() throws FileNotFoundException
    {
        String loadPath= getPathFromUser();
        try(ObjectInputStream in= new ObjectInputStream(new FileInputStream(loadPath)))
        {
           graph= (Graph)in.readObject();

            //check if was written succesfully
        } catch (IOException | ClassNotFoundException ex)
        {
            ex.getMessage();
        }
    }

    public String getPathFromUser()
    {
        System.out.println("Please enter the path you would like to save system status : ");
        return scanner.nextLine();
    }

    public void printTotalExecutionTime()
    {
        Duration totalTimeOfTask = TaskExecuting.getTotalTimeSpentOnTask();
        System.out.format("Total execution time for the task: %02d:%02d:%02d\n", totalTimeOfTask.toHours(), totalTimeOfTask.toMinutes(), totalTimeOfTask.getSeconds());
    }

    public void printGraphInformation() throws EmptyGraph {
        if(graph.getGraphTargets().size()==0)
            throw new EmptyGraph();

        else
        {
            Map<String, Target> graphTargets = graph.getGraphTargets();

            System.out.println("Number of targets : " + graphTargets.size());
            System.out.println("Number of leaf targets : " + graph.numberOfTargetsByProperty(Target.TargetProperty.LEAF));
            System.out.println("Number of root targets : " + graph.numberOfTargetsByProperty(Target.TargetProperty.ROOT));
            System.out.println("Number of middle targets : " + graph.numberOfTargetsByProperty(Target.TargetProperty.MIDDLE));
            System.out.println("Number of independent targets : " + graph.numberOfTargetsByProperty(Target.TargetProperty.INDEPENDENT));
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