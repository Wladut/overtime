package com.overtime;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;

/**
 * Created by Vrinceanu Vladut 13-05-2021
 * Time 16:08
 */

/*
    Thread used to count overtime if current time exceed closing time each 30 minutes
    - instantiated in Main class
 */

public class CountingHours extends Thread {
    boolean overtimeFlag = false;
    String writeMessageToTxt;
    int hours;
    String minutesString;
    @Override
    public void run() {
        while (true){
            if(LocalTime.now().isBefore(RegisterClosingTimeAsTxt.getClosingTime())){
                try {
                    synchronized (Thread.currentThread()){
                        Thread.currentThread().wait(1800000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                overtimeFlag = true;
                try {
                    RegisterClosingTimeAsTxt.fileWriter = new FileWriter(RegisterClosingTimeAsTxt.txtFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hours = LocalTime.now().getHour() - RegisterClosingTimeAsTxt.closingTime.getHour();
                int minutes = LocalTime.now().getMinute() - RegisterClosingTimeAsTxt.closingTime.getMinute();
                minutesString = minutes < 30? "0" : "5";
                writeMessageToTxt = RegisterClosingTimeAsTxt.stringToWrite  + "\nOvertime: " + hours + "," + minutesString;
                try {
                    RegisterClosingTimeAsTxt.fileWriter.write(RegisterClosingTimeAsTxt.stringToWrite + "\nOvertime: " + hours + "," + minutesString);
                    RegisterClosingTimeAsTxt.fileWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    synchronized (Thread.currentThread()){
                        Thread.currentThread().wait(1800000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
