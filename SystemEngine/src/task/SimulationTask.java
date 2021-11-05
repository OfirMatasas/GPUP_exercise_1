package task;

import target.Target;

import java.text.Format;
import java.text.SimpleDateFormat;

public class SimulationTask extends Task{
    private Format timeFormat;

    public SimulationTask() {
        this.timeFormat = new SimpleDateFormat("hh:mm:ss");
    }

    public void executeTaskOnTarget(Target target)
    {
        try {
            Thread.sleep(getTargetsParameters().get(target).getProcessingTime().toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}