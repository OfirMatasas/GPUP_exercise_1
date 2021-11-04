package userInterface;

import target.Graph;
import target.Target;
import task.SimulationTask;
import task.Task;
import task.TaskParameters;

import javax.sound.midi.Soundbank;
import java.sql.SQLOutput;
import java.sql.Time;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class UserInteractions implements OutputInterface, InputInterface {
    Scanner scanner = new Scanner(System.in);
    Graph targetsGraph;
    Task runningTask = new SimulationTask();

    public void SystemExecute()
    {
        System.out.println("Welcome to our project!");
        int userSelection = 0;

        do {
            printMenu();
            userSelection = getUserSelectionFromMenu();

            switch (userSelection)
            {
                case 1:
                {
                    loadFileAndSaveGraph();
                    break;
                }
                case 2:
                {
                    printGraphInformation();
                    break;
                }
                case 3:
                {
                    printTargetInformation();
                    break;
                }
                case 4:
                {
                    printTargetConnectionStatus();
                    break;
                }
                case 5:
                {
                    executeTask();
                    break;
                }


            }
            System.out.println("1. Load system details from file.");
            System.out.println("2. Show graph details.");
            System.out.println("3. Show target details.");
            System.out.println("4. Find connection between 2 targets.");
            System.out.println("5. Execute task.");
            System.out.println("6. Exit.");

        }while (userSelection != 0);
    }

    private void executeTask()
    {
        getTaskParametersFromUser();

        for(Target currentTarget : targetsGraph.getGraphTargets().values())
        {
            System.out.println("Task on target " + currentTarget.getTargetName() + " just started.");
            System.out.println("Target's extra information: " + currentTarget.getExtraInformation());
            executeTaskOnTarget(currentTarget);
            System.out.println("Task on target " + currentTarget.getTargetName() + " just ended.");
        }


    }

    private void executeTaskOnTarget(Target target)
    {
        TaskParameters taskParameters = runningTask.getTargetsParameters().get(target);
        Duration processingTime = null;
        Random rand = new Random();

        if(taskParameters.getRandom())
        {
            long originalTime = taskParameters.getProcessingTime().toMillis();
            long newTime = (long)(Math.random() * (taskParameters.getProcessingTime().toMillis())) + 1;

            processingTime = Duration.of(newTime, ChronoUnit.MILLIS);
        }

        System.out.println("Starting task on target " + target.getTargetName());

        System.out.println("The system is going to sleep for " + processingTime.toMillis() + "m/s.");
        runningTask.executeTaskOnTarget(target);
        System.out.println("The system went to sleep for " + processingTime.toMillis() + "m/s.");

        System.out.println("Task on target " + target.getTargetName() + " ended.");
        System.out.println("The result: " + target.getResultStatus().toString() + ".");

    }

    private void getTaskParametersFromUser() {
        Duration processingTime = null;
        long timeInMS = 0;
        Boolean isRandom = true;
        Double successRate = 0.0, successWithWarnings = 0.0;
        TaskParameters taskParameters = null;

        System.out.print("Enter the processing time (in secs) for each task: ");
        timeInMS = scanner.nextInt();
        processingTime = Duration.of(timeInMS, ChronoUnit.MILLIS);

        System.out.print("Choose if the processing time is limited by the value you just entered, or permanent (0 - limited, 1 - permanent): ");
        isRandom = scanner.nextBoolean();

        System.out.print("Enter the success rate of the task (value between 0 and 1): ");
        successRate = scanner.nextDouble();

        System.out.print("If the task ended successfully, what is the chance that it ended with warnings? (value between 0 and 1): ");
        successWithWarnings = scanner.nextDouble();

        taskParameters.setProcessingTime(processingTime);
        taskParameters.setRandom(isRandom);
        taskParameters.setSuccessRate(successRate);
        taskParameters.setSuccessWithWarnings(successWithWarnings);

        for(Target currentTarget : targetsGraph.getGraphTargets().values())
            runningTask.getTargetsParameters().put(currentTarget, taskParameters);
    }

    public void loadFileAndSaveGraph()
    {

    }

    @Override
    public void printTargetInformation()
    {
        if(targetsGraph.getGraphTargets().size() == 0)
        {
            System.out.println("There're no targets on the graph!");
            System.out.println("Please load a graph from file.");
            return;
        }

        System.out.print("Enter target's name: ");
        String targetName = scanner.nextLine();
        if(targetsGraph.getGraphTargets().containsKey(targetName))
        {
            Target selectedTarget = targetsGraph.getGraphTargets().get(targetName);
            System.out.println("Target's name: " + targetName);
            System.out.println("Target's property: " + selectedTarget.getTargetProperty());

            printDependsOnTargets(selectedTarget);
            printRequiredForTargets(selectedTarget);
            printTargetExtraInformation(selectedTarget);
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
        }
    }

    @Override
    public void printTargetExtraInformation(Target target) {
        if(target.getExtraInformation() == null)
            System.out.println("There's no extra information about the selected target.");
        else
            System.out.println("Target's extra information: " + target.getExtraInformation());
    }

    @Override
    public int getUserSelectionFromMenu() {
        try{
            int selection = scanner.nextInt();
            return selection;
        }
        catch(ArithmeticException ex)
        {
            printExceptionInformation("You entered non-integer. Please try again.");
            return 0;
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
    }

    @Override
    public void printTargetConnectionStatus()
    {
        String sourceTargetName = null, destTargetName = null;
        Target sourceTarget = null, destTarget = null;
        Target.Connection connection = Target.Connection.DEPENDS_ON;
        Boolean toContinue = true;
        int connectionChoice = 0;

        while(toContinue)
        {
            System.out.print("Please enter the name of the source target: ");
            sourceTargetName = scanner.nextLine();
            System.out.print("Please enter the name of the destination target: ");
            destTargetName = scanner.nextLine();

            sourceTarget = targetsGraph.getGraphTargets().get(sourceTargetName);
            destTarget = targetsGraph.getGraphTargets().get(destTargetName);

            if(sourceTarget == null || destTarget == null)
            {
                if(targetsGraph.getGraphTargets().get(sourceTargetName) == null)
                    System.out.println("The source target you've entered doesn't exist in the graph!");
                else
                    System.out.println("The destination target you've entered doesn't exist in the graph!");

                System.out.println("Would you like to try again? (0 - no, 1 - yes");
                    toContinue = scanner.nextBoolean();

                    if(!toContinue)
                        return;
                }
            }

        if(!targetsGraph.prechecksForTargetsConnection(sourceTargetName, destTargetName))
        {
            System.out.println("There're no paths between " + sourceTargetName + "and " + destTargetName);
            return;
        }

        System.out.print("Please enter the connection you'd like to see between the source and destination targets: ");
        System.out.println("0 - Source depends on destination, anything else - Source required for destination");
        connectionChoice = scanner.nextInt();
        if(connectionChoice != 0)
            connection = Target.Connection.REQUIRED_FOR;

        ArrayList<String> paths = targetsGraph.getPathsFromTargets(sourceTarget, destTarget, connection);

        if(paths.size() == 0)
            System.out.println("There're no paths between " + sourceTargetName + "and " + destTargetName + "as required.");
        else
        {
            System.out.println("There're " + paths.size() + " paths between " +  sourceTargetName + "and " + destTargetName + ":");
            for(String currentPath : paths)
                System.out.println(currentPath);
        }
    }

    @Override
    public void printExceptionInformation(String message) {
    }

    public void printGraphInformation()
    {// remember to add condition of if invalid file

        Map<Target.TargetProperty, Integer> graphDetails = targetsGraph.getGraphDetails();
        Map<String,Target> graphTargets = targetsGraph.getGraphTargets();

        System.out.println("Number of targets : " + graphTargets.size());
        System.out.println("Number of leaves targets : " + graphDetails.get(Target.TargetProperty.LEAF));
        System.out.println("Number of roots targets : " + graphDetails.get(Target.TargetProperty.ROOT));
        System.out.println("Number of middles targets : " + graphDetails.get(Target.TargetProperty.MIDDLE));
        System.out.println("Number of independents targets : " + graphDetails.get(Target.TargetProperty.INDEPENDENT));

    }


}