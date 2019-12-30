package sample.controller;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.GUI.GameTable;

public class MainScreenController {

    private AnchorPane rootPane;
    private Stage rootStage;

    @FXML
    private ImageView titleImage;

    @FXML
    private Button ButtonJoin;

    @FXML
    private TextField TextFieldAddress;

    @FXML
    private TextField TextFieldUser;

    public void setAll(AnchorPane root, Stage rootS) {
        rootPane = root;
        rootStage = rootS;
    }

    @FXML
    void JoinButtonAction(ActionEvent event) {

        String playerName = TextFieldUser.getText();
        String adress = TextFieldAddress.getText();

        if (playerName == null)
            return;

        final Integer PORT = 8910;
        final Integer GatePORT = 8911;
        final Integer RespondPORT = 8912;

        final int TIME_OUT = 3600 * 1000;

        try {

            System.out.println("Подключение");
            Socket socket = new Socket(adress, PORT);
            socket.setSoTimeout(TIME_OUT);
            Socket gate = new Socket(adress, GatePORT);
            gate.setSoTimeout(TIME_OUT);
            Socket respond = new Socket(adress, RespondPORT);
            respond.setSoTimeout(TIME_OUT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader inGate = new BufferedReader(new InputStreamReader(gate.getInputStream()));
            BufferedReader inRespond = new BufferedReader(new InputStreamReader(respond.getInputStream()));

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            PrintWriter outGate = new PrintWriter(gate.getOutputStream(), true);
            PrintWriter outRespond = new PrintWriter(respond.getOutputStream(), true);

            GameTable gameTable = new GameTable();
            gameTable.show(rootStage, in, out, inGate, outGate, inRespond, outRespond, playerName);


            rootStage.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}