package main;

import java.time.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Instant start = Instant.now();
        Thread.sleep(5000);
        Instant end = Instant.now();
        Duration between = Duration.between(start, end);
        System.out.format("%02d:%02d:%02d \n", between.toHours(), between.toMinutes(), between.getSeconds());

    }
}
