package task;

import target.Graph;
import target.Target;
import userInterface.GraphSummary;
import userInterface.TargetSummary;
import java.io.*;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

public class TaskOutput
{
    public void outputStartingTaskOnTarget(OutputStream os, Path filePath, Target target, Duration time)
    {
        try (Writer outputDest = new BufferedWriter(
                new OutputStreamWriter(os)))
        {
            outputDest.write("Task on target " + target.getTargetName() + " just started.\r\n");
            outputDest.write("Target's extra information: " + target.getExtraInformation() +"\r\n");
            outputDest.write(String.format("The system is going to sleep for %02d:%02d:%02d\r\n",
                    time.toHours(), time.toMinutes(), time.getSeconds()));
        }
        catch (Exception e) {
            System.out.println("Couldn't write to file " + filePath.getFileName().toString());
        }
    }

    public void outputEndingTaskOnTarget(OutputStream os, Path filePath, Target target, Duration time)
    {
        try (Writer outputDest = new BufferedWriter(
                new OutputStreamWriter(os)))
        {
            outputDest.write(String.format("The system went to sleep for %02d:%02d:%02d\n",
                    time.toHours(), time.toMinutes(), time.getSeconds()));
            outputDest.write("Task on target " + target.getTargetName() + " ended.");
            outputDest.write("The result: " + target.getResultStatus().toString() + ".");
            outputDest.write("The runtime status: " + target.getRuntimeStatus().toString() + ".");
        }
        catch (Exception e) {
            System.out.println("Couldn't write to file " + filePath.getFileName().toString());
        }
    }

    public void outputGraphSummary(BufferedOutputStream os, GraphSummary graphSummary)
    {
        try (Writer outputDest = new BufferedWriter(
                new OutputStreamWriter(os)))
        {
            Duration time = graphSummary.getTime();
            System.out.println("------------------------------------------");
            System.out.println("Task on graph ended!!!");
            System.out.format("Total time spent on task: %02d:%02d:%02d\n",
                    time.toHours(), time.toMinutes(), time.getSeconds());

            Map<Target.ResultStatus, Integer> results = graphSummary.getAllResultStatus();
            System.out.println("Number of targets succeeded: " + results.get(Target.ResultStatus.Success));
            System.out.println("Number of targets succeeded with warnings: " + results.get(Target.ResultStatus.Warning));
            System.out.println("Number of targets failed: " + results.get(Target.ResultStatus.Failure));
            System.out.println("Number of targets frozen: " + results.get(Target.ResultStatus.Frozen));

            for(TargetSummary currentTarget : graphSummary.getTargetsSummaryMap().values())
                printTargetTaskSummary(currentTarget);
            System.out.println("----------------------------------");
        }
        catch (Exception e) {
            System.out.println("Couldn't write to " + os.toString());
        }
    }

    public void printGraphTaskSummary(Task task, Graph graph)
    {

    }

    public void printTargetTaskSummary(TargetSummary targetSummary)
    {
        Duration time = targetSummary.getTime();

        System.out.println("-----------------------");
        System.out.println("Target's name :" + targetSummary.getTargetName());
        System.out.println("Target's result status :" + targetSummary.getResultStatus());
        System.out.format("Target's running time: %02d:%02d:%02d\n", time.toHours(), time.toMinutes(), time.getSeconds());
    }
}
