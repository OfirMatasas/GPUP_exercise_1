package task;

import target.Target;
import userInterface.GraphSummary;
import userInterface.TargetSummary;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import myExceptions.*;

public class TaskOutput
{
    String directoryPath;

    public void outputStartingTaskOnTarget(OutputStream os, TargetSummary targetSummary)
    {
        try
        {
            Duration time = targetSummary.getTime();

            String targetName, targetExtraInfo, totalTimeFormatted;

            targetName = "Task on target " + targetSummary.getTargetName() + " just started.\r\n";
            os.write(targetName.getBytes(StandardCharsets.UTF_8));

            if(targetSummary.getExtraInformation() != null)
            {
                targetExtraInfo = "Target's extra information: " + targetSummary.getExtraInformation() +"\r\n";
                os.write(targetExtraInfo.getBytes(StandardCharsets.UTF_8));
            }

            totalTimeFormatted = String.format("The system is going to sleep for %02d:%02d:%02d\r\n",
                    time.toHours(), time.toMinutes(), time.getSeconds());
            os.write(totalTimeFormatted.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            System.out.println("Couldn't write to file " + targetSummary.getTargetName() + ".log");
        }
    }

    public void outputEndingTaskOnTarget(OutputStream os, TargetSummary targetSummary)
    {
        try
        {
            Duration time = targetSummary.getTime();

            String targetName, totalTimeFormatted, result;

            targetName = "Task on target " + targetSummary.getTargetName() + " ended.\n";
            os.write(targetName.getBytes(StandardCharsets.UTF_8));

            totalTimeFormatted = String.format("The system went to sleep for %02d:%02d:%02d\n",
                    time.toHours(), time.toMinutes(), time.getSeconds());
            os.write(totalTimeFormatted.getBytes(StandardCharsets.UTF_8));

            result = "The result: " + targetSummary.getResultStatus().toString() + ".\n";
            os.write(result.getBytes(StandardCharsets.UTF_8));
            os.write("------------------------------------------\n".getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            System.out.println("Couldn't write to file " + targetSummary.getTargetName() + ".log");
        }
    }

    public void outputGraphSummary(OutputStream os, GraphSummary graphSummary)
    {
        try
        {
            Duration time = graphSummary.getTime();
            os.write("Graph task summary:\n".getBytes(StandardCharsets.UTF_8));

            String timeSpentFormatted = String.format("Total time spent on task: %02d:%02d:%02d\n",
            time.toHours(), time.toMinutes(), time.getSeconds());
            os.write(timeSpentFormatted.getBytes(StandardCharsets.UTF_8));

            Map<Target.ResultStatus, Integer> results = graphSummary.getAllResultStatus();
            String succeeded, warnings, failed, frozen;

            succeeded = "Number of targets succeeded: " + results.get(Target.ResultStatus.Success) + "\n";
            os.write(succeeded.getBytes(StandardCharsets.UTF_8));

            warnings = "Number of targets succeeded with warnings: " + results.get(Target.ResultStatus.Warning) + "\n";
            os.write(warnings.getBytes(StandardCharsets.UTF_8));

            failed = "Number of targets failed: " + results.get(Target.ResultStatus.Failure) + "\n";
            os.write(failed.getBytes(StandardCharsets.UTF_8));

            frozen = "Number of targets frozen: " + results.get(Target.ResultStatus.Frozen) + "\n";
            os.write(frozen.getBytes(StandardCharsets.UTF_8));

            for(TargetSummary currentTarget : graphSummary.getTargetsSummaryMap().values())
                outputTargetTaskSummary(os, currentTarget);
            os.write("----------------------------------\n".getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            System.out.println("Couldn't write to " + graphSummary.getGraphName() + ".log");
        }
    }

    public void printGraphTaskSummary(OutputStream os, TargetSummary targetSummary)
    {
        try
        {
            Duration time = targetSummary.getTime();

            String timeSpentFormatted = String.format("The system went to sleep for %02d:%02d:%02d\n",
                    time.toHours(), time.toMinutes(), time.getSeconds());
            os.write(timeSpentFormatted.getBytes(StandardCharsets.UTF_8));

            String targetTasked, result;

            targetTasked = "Task on target " + targetSummary.getTargetName() + " ended.\n";
            os.write(targetTasked.getBytes(StandardCharsets.UTF_8));

            result = "The result: " + targetSummary.getResultStatus().toString() + ".\n";
            os.write(result.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            System.out.println("Couldn't write to file " + targetSummary.getTargetName() + ".log");
        }
    }

    public void outputTargetTaskSummary(OutputStream os, TargetSummary targetSummary)
    {
        try {
            Duration time = targetSummary.getTime();

            os.write("-----------------------\n".getBytes(StandardCharsets.UTF_8));

            String targetName, result, timeSpentFormatted;

            targetName = "Target's name :" + targetSummary.getTargetName() + "\n";
            os.write(targetName.getBytes(StandardCharsets.UTF_8));

            result = "Target's result status :" + targetSummary.getResultStatus() + "\n";
            os.write(result.getBytes(StandardCharsets.UTF_8));

            timeSpentFormatted = String.format("Target's running time: %02d:%02d:%02d\n", time.toHours(), time.toMinutes(), time.getSeconds());
            os.write(timeSpentFormatted.getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Path createNewDirectoryOfTaskLogs(String taskName) throws OpeningFileCrash {
        directoryPath = "C:\\JavaProjects\\GPUP_exercise_1-master (1)\\GPUP_exercise_1-master\\SystemEngine\\src\\resources\\Schema and xml";
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
        Date date = new Date();

        directoryPath += "\\" + taskName + " - " + formatter.format(date).toString();

        Path path = Paths.get(directoryPath);
        try {
            Files.createDirectories(path);
            return path;
        } catch (IOException e) {
            throw new OpeningFileCrash(directoryPath);
        }
    }
}
