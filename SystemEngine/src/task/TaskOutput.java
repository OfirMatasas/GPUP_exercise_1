package task;

import target.Graph;
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
import userInterface.UserInteractions;

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
                targetExtraInfo = "Target's extra information: " + targetSummary.getExtraInformation() +"\n";
                os.write(targetExtraInfo.getBytes(StandardCharsets.UTF_8));
            }

            totalTimeFormatted = String.format("The system is going to sleep for %02d:%02d:%02d\n",
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

//            if(!targetSummary.isSkipped() && targetSummary.getResultStatus().equals(Target.ResultStatus.Failure))
//            {
//                os.write("Targets that have been skipped: ".getBytes(StandardCharsets.UTF_8));
//                for(String skippedTargetName : targetSummary.getSkippedTargets())
//                {
//                    String skippedTargetNameSpaced = skippedTargetName + " ";
//                    os.write(skippedTargetNameSpaced.getBytes(StandardCharsets.UTF_8));
//                }
//                os.write(("\n").getBytes(StandardCharsets.UTF_8));
//            }

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

            Map<TargetSummary.ResultStatus, Integer> results = graphSummary.getAllResultStatus();
            String succeeded, warnings, failed, frozen;

            succeeded = "Number of targets succeeded: " + results.get(TargetSummary.ResultStatus.Success) + "\n";
            os.write(succeeded.getBytes(StandardCharsets.UTF_8));

            warnings = "Number of targets succeeded with warnings: " + results.get(TargetSummary.ResultStatus.Warning) + "\n";
            os.write(warnings.getBytes(StandardCharsets.UTF_8));

            failed = "Number of targets failed: " + results.get(TargetSummary.ResultStatus.Failure) + "\n";
            os.write(failed.getBytes(StandardCharsets.UTF_8));

            for(TargetSummary currentTarget : graphSummary.getTargetsSummaryMap().values())
                outputTargetTaskSummary(os, currentTarget);
            os.write("----------------------------------\n".getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            System.out.println("Couldn't write to " + graphSummary.getGraphName() + ".log");
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

            if(!targetSummary.isSkipped())
            {
                timeSpentFormatted = String.format("Target's running time: %02d:%02d:%02d\n", time.toHours(), time.toMinutes(), time.getSeconds());
                os.write(timeSpentFormatted.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Path createNewDirectoryOfTaskLogs(String taskName, Path xmlFilePath) throws OpeningFileCrash {
        directoryPath = xmlFilePath.getParent().toString();
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

    public void printStartOfTaskOnGraph(String graphName) {
        System.out.println("Task started on graph " + graphName + "!");
    }
}
