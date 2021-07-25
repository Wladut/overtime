package com.overtime;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/*
    2 parameters for args:
    - args[0] -> folder where overtime path will be created
    - args[1] -> outlook e-mail (only outlook can be used because in SysTray is VBScript)
 */

public class Main extends Application {
    static Stage stage;
    static Parent root;
    static double x,y = 0;
    static CountingHours countingHours;
    static String path;
    static String email;
    SysTray sysTray = new SysTray();

    public static void main(String[] args) {
        path = args[0] + "\\overtime";
        email = args[1];
        RegisterClosingTimeAsTxt registerClosingTimeAsTxt = new RegisterClosingTimeAsTxt(args[0]);
        countingHours = new CountingHours();
        countingHours.start();
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Overtime.fxml"));
        mainStage.initStyle(StageStyle.UTILITY);
        mainStage.setOpacity(0);
        mainStage.show();
        stage = new Stage();
        stage.initOwner(mainStage);
        stage.setTitle("Overtime tracker");
        stage.initStyle(StageStyle.UNDECORATED);
        Main.root = root;

        root.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        root.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() - x);
            stage.setY(mouseEvent.getScreenY() - y);
        });


        stage.setScene(new Scene(root, 919, 698));
        stage.show();
        stage.requestFocus();
    }
}
