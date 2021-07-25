package com.overtime;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Created by Vrinceanu Vladut
 * Date: 13-05-2021
 * Time: 09:10
 * OS: Windows
 */

/*
    Class used to write / read overtime txt file in folder structure path
    - used in Main & in CountingHours
 */

class RegisterClosingTimeAsTxt {

    public static LocalTime closingTime;
    public static LocalTime openTime;
    public static FileWriter fileWriter;
    public static String stringToWrite;
    public static File txtFile;
    private LocalDate localDate = LocalDateTime.now().toLocalDate();

    RegisterClosingTimeAsTxt(String path){
        String rootFolderPath = path + "\\overtime";
        File folderStructurePath = new File(rootFolderPath);
        File txtFilePath = new File(rootFolderPath + "\\" +
                localDate.getYear() + "\\" +
                localDate.format(DateTimeFormatter.ofPattern("MMM")).toLowerCase());
        if(!createFullPath(folderStructurePath)){
            System.out.println("Error at folder structure creation.");
        }
        if(!createTodayFile(txtFilePath)){
            System.out.println("Time file was not created, already exists.");
        }
    }

    static LocalTime getClosingTime() {
        if(closingTime == null){
            try {
                Scanner scanner = new Scanner(txtFile);
                StringBuilder writeString = new StringBuilder();
                LocalTime localTime = null;
                while (scanner.hasNextLine()){
                    String data = scanner.nextLine();
                    writeString.append("\n").append(data);
                    if(data.contains("Closing  time:")){
                        String[] dataSplit = data.split("e: ");
                        localTime =  LocalTime.parse(dataSplit[1]);
                    }
                }
                scanner.close();
                stringToWrite = writeString.replace(0,1,"").toString();

                //Remove last line from txt file in case Overtime line exists.
                if(stringToWrite.contains("Overtime:")){
                    String[] strings = stringToWrite.split("\nOvertime");
                    stringToWrite = strings[0];
                }
                closingTime = localTime;
                return localTime;
            } catch (IOException e){
                System.out.println("File " + txtFile + " missing. Error: " + e.getMessage());
            }
        }
        return closingTime;
    }

    private boolean createFullPath(File folderPath){
        if(folderPath == null){
            return false;
        }
        boolean result = false;

        //Create root folder
        if(!folderPath.exists()){
            System.out.println("Path: " + folderPath + " is missing.\n" +
                    "Folder will be created");
            result = folderPath.mkdir();
        }

        //Create year folder
        folderPath = new File(folderPath + "\\" + localDate.getYear());
        if(!folderPath.exists()){
            System.out.println("Path: " + folderPath + " is missing.\n" +
                    "Folder will be created");
            result = folderPath.mkdir();
        }

        //Create month folder
        folderPath = new File(folderPath + "\\" +
                localDate.format(DateTimeFormatter.ofPattern("MMM")).toLowerCase());
        if(!folderPath.exists()){
            System.out.println("Path: " + folderPath + " is missing.\n" +
                    "Folder will be created");
            result = folderPath.mkdir();
        }

        return result;
    }

    private boolean createTodayFile(File filePath){
        if(filePath == null){
            return false;
        }
        boolean result = false;
        txtFile = new File(filePath + "\\" +
                localDate.format(DateTimeFormatter.ofPattern("dd-MMM-yy")) +
                ".time");
        if(txtFile.exists()){
            return false;
        }
        try {
            result = txtFile.createNewFile();
        } catch (IOException e){
            System.out.println("Error at file creation: " + e.getMessage());
        }
        try {
            fileWriter = new FileWriter(txtFile);
            openTime = LocalTime.now();
            closingTime = openTime.plusHours(8).plusMinutes(40);
            DateTimeFormatter newSimpleDateFormat = DateTimeFormatter.ofPattern("HH:mm");
            stringToWrite = "Date: " +
                    localDate.format(DateTimeFormatter.ofPattern("dd-MMM-yy")) +
                    "\nWorking hours: 8:40 (including launch break)" +
                    "\n\nOpen  time: " + openTime.format(newSimpleDateFormat) +
                    "\nClosing  time: " + closingTime.format(newSimpleDateFormat);
            fileWriter.write(stringToWrite);
            fileWriter.flush();
        } catch (IOException e){
            System.out.println("File not found: " + e.getMessage());
        }

        return result;
    }
}