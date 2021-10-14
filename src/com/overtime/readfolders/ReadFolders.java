package com.overtime.readfolders;

import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.*;

/**
 * Created by Vrinceanu Vladut 01-04-2021
 * Time 20:03
 */

/*
    Class used to read overtime folder structure
 */

public class ReadFolders {

    public static TreeItem<String> root = new TreeItem<>("Root");
    public static Map<String, String> txtFiles = new HashMap<>();
    public static Set<String> mainFolders = new HashSet<>();

    public static void getMainFolders(String folderPath){
        File actualFolderPath = new File(folderPath);
        File[] getFirstFolders = actualFolderPath.listFiles();

        for(File folder: getFirstFolders){
            if(folder.isDirectory()){
                mainFolders.add(folder.getName());
            }
        }
    }

    private static TreeItem<String> addTreeItemToRoot(String item, TreeItem<String> root) {
         TreeItem<String> actualItem = new TreeItem<>(item);
         root.getChildren().add(actualItem);
         return actualItem;
    }


    public static void getStructure(String folderPath, TreeItem<String> rootItem) {

        File actualFolderPath = new File(folderPath);
        File[] getFirstFolders = actualFolderPath.listFiles();

        try {
            for (File folder : getFirstFolders) {
                if(folder.getName().contains(".time") || folder.getName().contains(".txt") || folder.isDirectory()){
                    if(folder.getName().contains(".time") || folder.getName().contains(".txt")){
                        txtFiles.put(folder.getName(), folder.getAbsolutePath());
                    }
                    TreeItem<String> actualItem = addTreeItemToRoot(folder.getName(), rootItem);
                    getStructure(folder.getAbsolutePath(), actualItem);}
            }
        } catch (NullPointerException e) {
        }
    }
}
