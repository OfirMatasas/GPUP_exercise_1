package userInterface;

import task.TaskParameters;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TaskRequirements {

    Scanner scanner = new Scanner(System.in);

    public Boolean reuseTaskParameters()
    {
        System.out.println("There are existing parameters for this task.");
        System.out.println("Would you like to re-use them (y/n)?");
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

        taskParameters.setProcessingTime(processingTime);
        taskParameters.setRandom(isRandom);
        taskParameters.setSuccessRate(successRate);
        taskParameters.setSuccessWithWarnings(successWithWarnings);

        return taskParameters;
    }
}
