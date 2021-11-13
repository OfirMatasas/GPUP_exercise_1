package main;

import myExceptions.OpeningFileCrash;
import userInterface.UserInteractions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        UserInteractions testing = new UserInteractions();
        testing.SystemExecute();


//        String directoryPath = "C:\\JavaProjects\\GPUP_exercise_1-master (1)\\GPUP_exercise_1-master\\SystemEngine\\src\\resources\\Schema and xml";
//        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
//        Date date = new Date();
//
//        directoryPath += "\\test - " + formatter.format(date).toString();
////Created a directory
//        Path path = Paths.get(directoryPath);
//        try {
//            Files.createDirectories(path);
//        } catch (IOException e) {
//            System.out.println("Couldn't open directory");;
//        }
//
////Created a file
//        Path filePath = Paths.get(directoryPath + "\\file.log");
//        try {
//            Files.createFile(filePath);
//        } catch (IOException e) {
//            System.out.println("Couldn't open file");;
//        }
//
////Printing to the file created
//        OutputStream out = null;
//        try {
//            out = new FileOutputStream(filePath.toString());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        String str = "check";
//        try {
//            out.write(str.getBytes(StandardCharsets.UTF_8));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        out = new PrintStream(System.out);
//        try {
//            out.write(str.getBytes(StandardCharsets.UTF_8));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        Instant start = Instant.now();
//        Thread.sleep(5000);
//        Instant end = Instant.now();
//        Duration between = Duration.between(start, end);
//        System.out.format("%02d:%02d:%02d \n", between.toHours(), between.toMinutes(), between.getSeconds());

    }
}