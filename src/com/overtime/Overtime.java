package com.overtime;

import com.overtime.countinghours.CountingHours;
import com.overtime.createtxt.RegisterClosingTimeAsTxt;
import com.overtime.tray.SysTray;
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

public class Overtime extends Application {
    public static Stage stage;
    public static Parent root;
    public static double x,y = 0;
    public static CountingHours countingHours;
    public static String path;
    public static String email;
    public SysTray sysTray = new SysTray();

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
        Parent root = FXMLLoader.load(getClass().getResource("fxml/Overtime.fxml"));
        mainStage.initStyle(StageStyle.UTILITY);
        mainStage.setOpacity(0);
        mainStage.show();
        stage = new Stage();
        stage.initOwner(mainStage);
        stage.setTitle("Overtime tracker");
        stage.initStyle(StageStyle.UNDECORATED);
        Overtime.root = root;

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
