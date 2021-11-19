package userInterface;

import task.TaskParameters;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class TaskRequirements {

    Scanner scanner = new Scanner(System.in);

    public Boolean reuseTaskParameters()
    {
        System.out.println("There are existing parameters for this task.");
        System.out.println("Would you like to re-use them?");
        return UserInteractions.yesOrNo();
    }

    public TaskParameters getTaskParametersFromUser()
    {
        Duration processingTime = null;
        long timeInMS = -1;
        Boolean isRandom = true;
        Double successRate = -1.0, successWithWarnings = -1.0;
        TaskParameters taskParameters = new TaskParameters();


        System.out.print("Enter the processing time (in m/s) for each task: ");
        while(true)
        {
            try {
                timeInMS = scanner.nextLong();
                scanner.nextLine();

                if(timeInMS < 0)
                    System.out.print("invalid input. Enter a positive integer: ");
                else
                    break;
            }
            catch(InputMismatchException ex)
            {
                System.out.print("invalid input.\nEnter an integer as the time in m/s of task duration: ");
                scanner.nextLine();
            }
        }
        processingTime = Duration.of(timeInMS, ChronoUnit.MILLIS);

        System.out.print("Choose if the processing time is limited by the value you just entered, or permanent (y - limited, n - permanent): ");
        isRandom = UserInteractions.yesOrNo();

        System.out.print("Enter the success rate of the task (value between 0 and 1): ");

        while(true)
        {
            try {
                successRate = scanner.nextDouble();
                scanner.nextLine();

                if(successRate < 0.0 || successRate > 1.0)
                    System.out.print("invalid input.\nEnter the success rate of the task (value between 0 and 1): ");
                else
                    break;
            }
            catch(InputMismatchException ex)
            {
                System.out.print("invalid input.\nEnter the success rate of the task (value between 0 and 1): ");
                scanner.nextLine();
            }
        }

        System.out.print("If the task ended successfully, what is the chance that it ended with warnings? (value between 0 and 1): ");

        while(true)
        {
            try {
                successWithWarnings = scanner.nextDouble();
                scanner.nextLine();

                if(successWithWarnings < 0.0 || successWithWarnings > 1.0)
                    System.out.print("invalid input.\nEnter the success rate with warnings of the task (value between 0 and 1): ");
                else
                    break;
            }
            catch(InputMismatchException ex)
            {
                System.out.print("invalid input.\nEnter the success rate with warnings of the task (value between 0 and 1): ");
                scanner.nextLine();
            }
        }

        if(isRandom)
        {
            long newTime = (long)(Math.random() * (processingTime.toMillis())) + 1;
            processingTime = Duration.of(newTime, ChronoUnit.MILLIS);
        }

        taskParameters.setProcessingTime(processingTime);
        taskParameters.setRandom(isRandom);
        taskParameters.setSuccessRate(successRate);
        taskParameters.setSuccessWithWarnings(successWithWarnings);

        return taskParameters;
    }

//    public void printStartOfTaskOnTarget(Task task, Target target)
//    {
//        Duration processingTime = task.getTargetsParameters().get(target).getProcessingTime();
//        System.out.println("------------------------------------------");
//        System.out.println("Task on target " + target.getTargetName() + " just started.");
//        System.out.println("Target's extra information: " + target.getExtraInformation());
//        System.out.format("The system is going to sleep for %02d:%02d:%02d\n", processingTime.toHours(), processingTime.toMinutes(), processingTime.getSeconds());
//    }
//
//    public void printEndOfTaskOnTarget(Task task, TargetSummary target)
//    {
//        Duration processingTime = task.getTargetsParameters().get(target).getProcessingTime();
//        System.out.format("The system went to sleep for %02d:%02d:%02d\n", processingTime.toHours(), processingTime.toMinutes(), processingTime.getSeconds());
//
//        System.out.println("Task on target " + target.getTargetName() + " ended.");
//        System.out.println("The result: " + target.getResultStatus().toString() + ".");
//        System.out.println("The runtime status: " + target.getRuntimeStatus().toString() + ".");
//    }
//
//    public void printStartOfTaskOnGraph(Graph graph)
//    {
//        System.out.println("Task started on graph!");
//    }
//
//    public void printGraphTaskSummary(GraphSummary graphSummary)
//    {
//        Duration time = graphSummary.getTime();
//        System.out.println("------------------------------------------");
//        System.out.println("Task on graph ended!!!");
//        System.out.format("Total time spent on task: %02d:%02d:%02d\n",
//                time.toHours(), time.toMinutes(), time.getSeconds());
//
//        Map<TargetSummary.ResultStatus, Integer> results = graphSummary.getAllResultStatus();
//        System.out.println("Number of targets succeeded: " + results.get(TargetSummary.ResultStatus.Success));
//        System.out.println("Number of targets succeeded with warnings: " + results.get(TargetSummary.ResultStatus.Warning));
//        System.out.println("Number of targets failed: " + results.get(TargetSummary.ResultStatus.Failure));
//
//        for(TargetSummary currentTarget : graphSummary.getTargetsSummaryMap().values())
//            printTargetTaskSummary(currentTarget);
//        System.out.println("----------------------------------");
//    }
//
//    public void printTargetTaskSummary(TargetSummary targetSummary)
//    {
//        Duration time = targetSummary.getTime();
//
//        System.out.println("-----------------------");
//        System.out.println("Target's name :" + targetSummary.getTargetName());
//        System.out.println("Target's result status :" + targetSummary.getResultStatus());
//        System.out.format("Target's running time: %02d:%02d:%02d\n", time.toHours(), time.toMinutes(), time.getSeconds());
//    }
}
