package sample.GUI;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
 import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;
import sample.controller.GameTableController;

import javax.swing.*;

public class GameTable {

    private Stage rootStage;
    private Stage stage;

    public void show(Stage rootStage, BufferedReader in, PrintWriter out, BufferedReader inGate, PrintWriter outGate, BufferedReader inRespond, PrintWriter outRespond, String name) {
        this.rootStage = rootStage;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/fxml/GameTable.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            this.stage = stage;

            stage.setTitle("The GAME! playing as " + name);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/sample/fxml/cards/icon.png")));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();

//            Platform.runLater(() -> {
//                SocketClient socketClient = new SocketClient();
//                socketClient.startConnection("127.0.0.1", 4321);
//
//                AnchorPane pane = new AnchorPane();
//                socketClient.setAnchorPane(pane);
//                pane.setId("112");
//                Stage stage2 = new Stage();
//                stage2.setScene(new Scene(pane, 600, 400));
//                VBox vBox = new VBox();
//                vBox.setPrefSize(600, 400);
//                TextArea textArea = new TextArea();
//                textArea.setPrefSize(600, 300);
//                textArea.setId("113");
//                textArea.setEditable(false);
//                vBox.getChildren().add(textArea);
//                HBox hBox = new HBox();
//                hBox.setPrefSize(600, 100);
//                TextField field = new TextField();
//                field.setPrefSize(500, 80);
//                field.setId("114");
//                Button button = new Button();
//                button.setText("Send");
//                button.setOnMouseClicked(e -> {
//                    socketClient.sendMessage(field.getText());
//                    field.setText("");
//                });
//                hBox.getChildren().add( field);
//                hBox.getChildren().add(button);
//                vBox.getChildren().add(hBox);
//                pane.getChildren().add(vBox);
//                stage2.show();
//            });


            GameTableController cntl = loader.getController();
            cntl.setIO(in, out, inGate, outGate, inRespond, outRespond, name, stage);

            Checker connectionChecking = new Checker(inGate, outGate, cntl);
            connectionChecking.start();

            stage.setOnCloseRequest(event -> {
                if (ExitWindow.display("Завершение программы", "Вы действительно хотите выйти из программы?")) {
                    stage.close();
                }
                event.consume();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        rootStage.close();
    }

    class Checker extends Thread{
        BufferedReader in;
        PrintWriter out;
        GameTableController cntl;
        Checker(BufferedReader in, PrintWriter out, GameTableController cntl) {
            this.in = in;
            this.out = out;
            this.cntl = cntl;
        }

        public void run() {
            try {
                while(true) {
                    System.out.println("Trying to gate");
                    String pol = in.readLine();
                    System.out.println("gate " + pol);
                    if(pol.startsWith("OPEN")) {
                        cntl.openConnection();
                    } else if(pol.startsWith("CLOSE")){
                        cntl.closeConnection();
                    }
                }
            } catch (Exception e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame, "Connection lost. Close the program!");
                frame.dispose();
                cntl.setToCloseTheWindow();
                try {
                    throw e;
                } catch (IOException e1) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.println("Thread end");
        }
    }
}