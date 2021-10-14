package com.overtime.controller;

import com.overtime.readfolders.ReadFolders;
import com.overtime.Overtime;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Vrinceanu Vladut 01-04-2021
 * Time 17:03
 */

public class OvertimeController implements Initializable {

    private final String NIGHT_FLAG = " <NIGHT>";
    private final String DAY_FLAG = " <DAY>";
    private final String REGISTER_TIME_FLAG = " <REGISTER TIME>";
    @FXML Text dev;
    @FXML ImageView imageContinental;
    @FXML ImageView radioButton;
    @FXML Text labelOvertime;
    @FXML BorderPane borderPane;
    @FXML TreeView<String> foldersTreeView;
    @FXML HBox hBox = new HBox();
    @FXML TextArea notepadText = new TextArea();
    @FXML ImageView tray = new ImageView();
    @FXML ImageView minimize = new ImageView();
    @FXML ImageView exit = new ImageView();
    @FXML ImageView icon = new ImageView();
    @FXML ImageView sendEmailToggle = new ImageView();
    @FXML private Image sendEmailImage;
    @FXML private Image continentalImageIntermediate;
    @FXML private ImageView refresh = new ImageView();
    private Glow glowEffect = new Glow();
    private DropShadow dropShadowEffect = new DropShadow();
    public static boolean sendEmail = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String readStructureFoldersPath = Overtime.path + "\\";
        ReadFolders.getMainFolders(readStructureFoldersPath);
        ReadFolders.getStructure(readStructureFoldersPath, ReadFolders.root);
        foldersTreeView.setRoot(ReadFolders.root);

        try {
            if(readConfig().contains(NIGHT_FLAG)){
                nightMode();
            }
            if(readConfig().contains(REGISTER_TIME_FLAG)){
                sendEmailImage = new Image("/com/resources/send_email.png");
                sendEmailToggle.setImage(sendEmailImage);
                sendEmail = true;
            }
        } catch (IOException ignored) {
            File config = new File("config.ini");
            try {
                config.createNewFile();
                writeConfig("day");
            } catch (IOException e) {

            }
        }
    }

    @FXML
    public void selectedTxtFile() {
        for(String name: ReadFolders.txtFiles.keySet()){
            try {
                if (name.equals(foldersTreeView.getSelectionModel().getSelectedItem().getValue())) {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(ReadFolders.txtFiles.get(name)));
                        notepadText.clear();
                        String line = bufferedReader.readLine();
                        StringBuilder stringBuilder = new StringBuilder();
                        while (line != null) {
                            stringBuilder.append(line);
                            stringBuilder.append("\n");
                            line = bufferedReader.readLine();
                        }
                        bufferedReader.close();
                        notepadText.setText(stringBuilder.toString());
                    } catch (IOException ignore) {

                    }
                }
            }
            catch (NullPointerException ignore){

            }
        }
    }

    @FXML
    public void nightMode() {
        try {
            if (borderPane.styleProperty().toString().contains("F5F1F0")) {
                continentalImageIntermediate = new Image("/com/resources/overtimeorange.PNG");
                imageContinental.setImage(continentalImageIntermediate);
                imageContinental.setFitWidth(300);
                imageContinental.setFitHeight(85);
                imageContinental.blendModeProperty().setValue(BlendMode.SRC_OVER);
                borderPane.setStyle("-fx-background-color: #19181F; -fx-border-color: #AEAEAE");
                Platform.runLater(() -> notepadText.lookup(".content").setStyle("-fx-background-color: #23222E"));
                notepadText.setStyle("-fx-text-fill: white");
                labelOvertime.setStyle("-fx-fill: orange");
                dev.setStyle("-fx-fill: orange");
                radioButton.setStyle("-fx-text-fill: orange");
                foldersTreeView.getStylesheets().add("/com/resources/style.css");
                if (readConfig().contains(REGISTER_TIME_FLAG)) {
                    writeConfig(NIGHT_FLAG + " " + REGISTER_TIME_FLAG);
                } else {
                    writeConfig(NIGHT_FLAG);
                }


            } else {
                borderPane.setStyle("-fx-background-color: #F5F1F0; -fx-border-color: black");
                notepadText.lookup(".content").setStyle("-fx-background-color: white");
                notepadText.setStyle("-fx-text-fill: black");
                dev.setStyle("-fx-fill: black");
                notepadText.setStyle("-fx-text-fill: black");
                labelOvertime.setStyle("-fx-fill: black");
                radioButton.setStyle("-fx-text-fill: black");
                foldersTreeView.getStylesheets().remove(0);
                continentalImageIntermediate = new Image("/com/resources/overtimeblack.PNG");
                imageContinental.setImage(continentalImageIntermediate);
                imageContinental.setFitWidth(300);
                imageContinental.setFitHeight(85);
                imageContinental.blendModeProperty().setValue(BlendMode.DARKEN);
                if (readConfig().contains(REGISTER_TIME_FLAG)) {
                    writeConfig(DAY_FLAG + " " + REGISTER_TIME_FLAG);
                } else {
                    writeConfig(DAY_FLAG);
                }

            }
        } catch (IOException ignore){}
    }

    @FXML
    public void exit(){
        Platform.runLater(() -> Overtime.stage.setOpacity(0));
    }

    @FXML void resize(){
        if(Overtime.stage.isMaximized()){
            Overtime.stage.setMaximized(false);
        } else {
        Overtime.stage.setMaximized(true);}
        Overtime.root.requestFocus();
    }

    @FXML void tray(){
        if(Overtime.stage.isIconified()){
            Overtime.stage.setIconified(false);
        } else {
            Overtime.stage.setIconified(true);
        }
        Overtime.root.requestFocus();
    }

    private void writeConfig(String meassage) throws IOException {
        File configFile = new File("config.ini");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile));
        bufferedWriter.write(meassage);
        bufferedWriter.close();
    }

    private void appendConfig(String meassage) throws IOException {
        File configFile = new File("config.ini");
        FileWriter fileWriter = new FileWriter(configFile, true);
        fileWriter.write(meassage);
        fileWriter.close();
    }

    @FXML public void activateRegisterTime() {
        try {
            if (!readConfig().contains(REGISTER_TIME_FLAG)) {
                appendConfig(REGISTER_TIME_FLAG);
                sendEmailImage = new Image("/com/resources/send_email.png");
                sendEmail = true;
            }
        else {
                if (readConfig().contains(NIGHT_FLAG)){
                    writeConfig(NIGHT_FLAG);
                } else {
                    writeConfig(DAY_FLAG);
                }
                sendEmail = false;
                sendEmailImage = new Image("/com/resources/not_send_email.png");
            }
            sendEmailToggle.setImage(sendEmailImage);
        }
    catch (IOException ignore){}
    }

    private String readConfig() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("config.ini"));
        String mode = bufferedReader.readLine();
        bufferedReader.close();
        return mode;
    }

    public void setEffect() {
        setOrDisableGlowOrDropShadowEffects(labelOvertime, glowEffect);
        setOrDisableGlowOrDropShadowEffects(icon, glowEffect);
        setOrDisableGlowOrDropShadowEffects(dev, glowEffect);
        setOrDisableGlowOrDropShadowEffects(imageContinental, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(radioButton, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(tray, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(minimize, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(exit, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(notepadText, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(foldersTreeView, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(refresh, dropShadowEffect);
        setOrDisableGlowOrDropShadowEffects(sendEmailToggle, dropShadowEffect);
    }

    private void setOrDisableGlowOrDropShadowEffects(Object objectType, Object effectType){
        dropShadowEffect.setColor(Color.color(0.4, 0.5, 0.5));
        ((Node) objectType).setOnMouseEntered(mouseEvent -> ((Node) objectType)
                .setEffect(effectType instanceof Glow ? (Glow) effectType : (DropShadow) effectType));
        ((Node) objectType).setOnMouseExited(mouseEvent -> ((Node) objectType)
                .setEffect(null));

    }

}
