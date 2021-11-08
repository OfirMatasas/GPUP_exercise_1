package userInterface;

import target.Graph;
import target.Target;
import task.Task;
import task.TaskParameters;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Scanner;

public class TaskRequirements {

    Scanner scanner = new Scanner(System.in);

    public TaskParameters getTaskParametersFromUser()
    {
        Duration processingTime=null;
        long timeInMS = 0;
        Boolean isRandom = true;
        int randomTmp;
        Double successRate = 0.0, successWithWarnings = 0.0;
        TaskParameters taskParameters = new TaskParameters();

        System.out.print("Enter the processing time (in m/s) for each task: ");
        timeInMS = scanner.nextInt();
        processingTime = Duration.of(timeInMS, ChronoUnit.MILLIS);

        System.out.print("Choose if the processing time is limited by the value you just entered, or permanent (0 - limited, 1 - permanent): ");
        randomTmp=scanner.nextInt();

        if(randomTmp==0)
            isRandom=true;
        else
            isRandom=false;

        System.out.print("Enter the success rate of the task (value between 0 and 1): ");
        successRate = scanner.nextDouble();

        System.out.print("If the task ended successfully, what is the chance that it ended with warnings? (value between 0 and 1): ");
        successWithWarnings = scanner.nextDouble();

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

    public void printStartOfTaskOnTarget(Task task, Target target)
    {
        Duration processingTime = task.getTargetsParameters().get(target).getProcessingTime();
        System.out.println("------------------------------------------");
        System.out.println("Task on target " + target.getTargetName() + " just started.");
        System.out.println("Target's extra information: " + target.getExtraInformation());
        System.out.format("The system is going to sleep for %02d:%02d:%02d\n", processingTime.toHours(), processingTime.toMinutes(), processingTime.getSeconds());
    }

    public void printEndOfTaskOnTarget(Task task, Target target)
    {
        Duration processingTime = task.getTargetsParameters().get(target).getProcessingTime();
        System.out.format("The system went to sleep for %02d:%02d:%02d\n", processingTime.toHours(), processingTime.toMinutes(), processingTime.getSeconds());

        System.out.println("Task on target " + target.getTargetName() + " ended.");
        System.out.println("The result: " + target.getResultStatus().toString() + ".");
    }

    public void printStartOfTaskOnGraph(Graph graph)
    {
        System.out.println("Task started on graph!");
    }

    public void printGraphTaskSummary(Task task, Graph graph)
    {
        Duration time = task.getTotalTimeSpentOnTask();
        System.out.println("------------------------------------------");
        System.out.println("Task on graph ended!!!");
        System.out.format("Total time spent on task: %02d:%02d:%02d\n",
                time.toHours(), time.toMinutes(), time.getSeconds());

        Map<Target.ResultStatus, Integer> results = graph.calculateResultResults();
        System.out.println("Number of targets succeeded: " + results.get(Target.ResultStatus.Success));
        System.out.println("Number of targets succeeded with warnings: " + results.get(Target.ResultStatus.Warning));
        System.out.println("Number of targets failed: " + results.get(Target.ResultStatus.Failure));
        System.out.println("Number of targets frozen: " + results.get(Target.ResultStatus.Frozen));

        for(Target currentTarget : graph.getGraphTargets().values())
            printTargetTaskSummary(task, currentTarget);
        System.out.println("----------------------------------");
    }

    public void printTargetTaskSummary(Task task, Target target)
    {
        Duration time;
        time = task.getTargetsParameters().get(target).getProcessingTime();

        System.out.println("-----------------------");
        System.out.println("Target's name :" + target.getTargetName());
        System.out.println("Target's result status :" + target.getResultStatus());
        System.out.format("Target's running time: %02d:%02d:%02d\n", time.toHours(), time.toMinutes(), time.getSeconds());
    }
}
