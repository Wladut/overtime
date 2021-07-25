package com.overtime;

import javafx.application.Platform;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

/**
 * Created by Vrinceanu Vladut 13-05-2021
 * Time 13:27
 */

/*
    Tray class -> at close starts thread to write excel & send outlook e-mail
 */

public class SysTray {
    static TrayIcon trayIcon;

    SysTray(){
        show();
    }

    public static void show(){
        if (!java.awt.SystemTray.isSupported()){
            System.exit(0);
        }

        trayIcon = new TrayIcon(new ImageIcon(SysTray.class.getResource("/com/resources/clock.png")).getImage());
        trayIcon.setToolTip("Overtime tracking");
        final SystemTray tray = SystemTray.getSystemTray();

        final PopupMenu menu = new PopupMenu();
        MenuItem about = new MenuItem("About");
        MenuItem exit = new MenuItem("Exit");
        MenuItem open = new MenuItem("Open");
        menu.add(open);
        menu.addSeparator();
        menu.add(about);
        menu.addSeparator();
        menu.add(exit);

        trayIcon.setPopupMenu(menu);

        open.addActionListener(actionEvent -> {
            Platform.runLater(() ->{
                Main.stage.setOpacity(1);
                Main.stage.requestFocus();
            });
        });

        about.addActionListener(actionEvent -> {
            JOptionPane.showMessageDialog(null,
                    "Overtime tracking\nAuthor: vladut.2.vrinceanu@continental-corporation.com");
        });

        exit.addActionListener(actionEvent -> {
            if (Main.countingHours.overtimeFlag && Overtime.sendEmail) {
                    //Get overtime
                    WritingExcelOvertimeStatus writingExcelOvertimeStatus = new WritingExcelOvertimeStatus(Main.path);
                    double lastOverallValue = writingExcelOvertimeStatus.lastOverallValue();
                    double actualOvertime = Double.parseDouble(Main.countingHours.hours + "." + Main.countingHours.minutesString);
                    double overall = actualOvertime + lastOverallValue;
                    writingExcelOvertimeStatus.writeLine(actualOvertime, overall);
                    //Send email
                    File sendEmailVbaScript = new File((Paths.get("").toAbsolutePath().toString()) + "\\SendEmail.vbs");
                    if (sendEmailVbaScript.exists()) {
                        sendEmailVbaScript.delete();
                    }
                    try {
                        String emailMessage = Main.countingHours.writeMessageToTxt + "\n\n" + "Overall:" + overall;
                        String[] emailMessageList = emailMessage.split("\n");
                        emailMessage = "";
                        for(String string : emailMessageList){
                            emailMessage += string + "\" & vbNewLine &\"";
                        }
                        Thread.sleep(1000);
                        sendEmailVbaScript.createNewFile();
                        FileWriter fileWriterVbaScript = new FileWriter(sendEmailVbaScript);
                        fileWriterVbaScript.write("Sub sendEmail()\n" +
                                "    'Check whether outlook is open, if it is use get object, if not use create object\n" +
                                "    On Error Resume Next\n" +
                                "    Set olApp = GetObject(, \"Outlook.Application\")\n" +
                                "    On Error GoTo 0\n" +
                                "    If olApp Is Nothing Then\n" +
                                "        Set olApp = CreateObject(\"Outlook.Application\")\n" +
                                "    End If\n" +
                                "    \n" +
                                "    Set objNS = olApp.GetNamespace(\"MAPI\")\n" +
                                "    objNS.Logon\n" +
                                "    \n" +
                                "    'Prepare the mail object\n" +
                                "    Set objMail = olApp.CreateItem(olMailItem)\n" +
                                "    \n" +
                                "    With objMail\n" +
                                "        .To = \"" + Main.email + "\"\n" +
                                "        .Subject = \"Status Overtime\"\n" +
                                "        .Body = \"" + emailMessage + "\"\n" +
                                "        .Display\n" +
                                "    End With\n" +
                                "    \n" +
                                "    'Give outlook some time to display the message\n" +
                                "    WScript.Sleep 6000\n" +
                                "    \n" +
                                "    'Get a reference the inspector obj (the window the mail item is displayed in)\n" +
                                "    Set myInspector = objMail.GetInspector\n" +
                                "    \n" +
                                "    'Activate the window that the mail item is in and use sendkeys to send the message\n" +
                                "SET shell = WScript.CreateObject(\"WScript.Shell\")\n" +
                                "    myInspector.Activate\n" +
                                "    shell.SendKeys \"%s\", True\n" +
                                "End Sub\n" +
                                "\n" +
                                "sendEmail");
                        fileWriterVbaScript.flush();
                        fileWriterVbaScript.close();
                        Thread.sleep(1000);
                        String command = "cmd.exe /c cd \"" + (Paths.get("").toAbsolutePath().toString()) + "\\\" & start SendEmail.vbs";
                        System.out.println(command);
                        Thread sendEmail = new Thread() {
                            @Override
                            public synchronized void start() {
                                try {
                                    Process sendEmailProcess = Runtime.getRuntime().exec(command);
                                    sendEmailProcess.waitFor(6000, TimeUnit.MILLISECONDS);
                                    Thread.sleep(6000);
                                } catch (IOException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        sendEmail.start();
                        Thread.sleep(8000);
                        sendEmailVbaScript.delete();
                    } catch (IOException | InterruptedException ignore) {

                    }
                }
            System.exit(0);
        });

        try{
            tray.add(trayIcon);
        } catch (Exception ignore){

        }
    }
}
