package main;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.core.Game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Start {

    private static final Integer PORT = 8910;
    private static final Integer GatePORT = 8911;
    private static final Integer RespondPORT = 8912;

    private static final Integer TIME_OUT = 3600 * 1000;


    public static void main(String[] args) {
        startServer();
    }

    private static void startServer() {

        System.out.println("Starting!");
        try {

            ServerSocket listener = new ServerSocket(PORT);
            ServerSocket listenerGate = new ServerSocket(GatePORT);
            ServerSocket listenerRespond = new ServerSocket(RespondPORT);
//            Server server = new Server();
//            server.start(4321);

            Socket[] player = {listener.accept(), listener.accept()};
            Socket[] playerGate = {listenerGate.accept(), listenerGate.accept()};
            Socket[] playerRespond = {listenerRespond.accept(), listenerRespond.accept()};

            for (int i = 0; i < 2; i++) {
                player[i].setSoTimeout(TIME_OUT);
                playerGate[i].setSoTimeout(TIME_OUT);
                playerRespond[i].setSoTimeout(TIME_OUT);
            }


            BufferedReader[] inputFromP = {
                    new BufferedReader(
                            new InputStreamReader(player[0].getInputStream())),
                    new BufferedReader(
                            new InputStreamReader(player[1].getInputStream()))
            };
            BufferedReader[] inputFromPGate = {
                    new BufferedReader(new InputStreamReader(playerGate[0].getInputStream())),
                    new BufferedReader(new InputStreamReader(playerGate[1].getInputStream()))
            };
            BufferedReader[] inputFromPRespond = {
                    new BufferedReader(new InputStreamReader(playerRespond[0].getInputStream())),
                    new BufferedReader(new InputStreamReader(playerRespond[1].getInputStream()))
            };

            PrintWriter[] outputToP = {
                    new PrintWriter(player[0].getOutputStream(), true),
                    new PrintWriter(player[1].getOutputStream(), true)
            };
            PrintWriter[] outputToPGate = {
                    new PrintWriter(playerGate[0].getOutputStream(), true),
                    new PrintWriter(playerGate[1].getOutputStream(), true)
            };
            PrintWriter[] outputToPRespond = {
                    new PrintWriter(playerRespond[0].getOutputStream(), true),
                    new PrintWriter(playerRespond[1].getOutputStream(), true)
            };

            Game game = new Game(inputFromP, outputToP, inputFromPGate, outputToPGate, inputFromPRespond, outputToPRespond);
            game.play();


        } catch (SocketException e) {
            System.out.println("The game not started! :(");
            System.out.println(e.getMessage());
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("The End...");
    }
}