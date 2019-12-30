package sample.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.core.Card;

public class GameTableController {

    private static final Double cardWidth = 72d;
    private static final Double cardHeigth = 96d;

    private BufferedReader in = null;
    private PrintWriter out = null;
    private BufferedReader inGate = null;
    private PrintWriter outGate = null;
    private BufferedReader inRaspond = null;
    private PrintWriter outRespond = null;
    private Stage rootStage = null;

    private boolean isConnectionOpenned = false;
    private boolean hasToBeClosed = false;

    @FXML
    private Text CardLabel;

    @FXML
    private AnchorPane PlayerUpDeck, PlayerBottomDeck;

    private ArrayList<Card> playersCards = new ArrayList<>();
    private Integer oponentCount = 0;
    private Card actualCard = null;

    @FXML
    private ImageView cardOnTable;

    @FXML
    private Button drawButton, reloadButton;

    @FXML
    private Label messageLabel;

    @FXML
    void drawButtonOnAction(ActionEvent event) {
        if (!isConnectionOpenned)
            return;
        out.println("draw");
    }

    @FXML
    void skipButtonOnAction(ActionEvent event) {
        if (!isConnectionOpenned)
            return;
        out.println("skip");
    }

    @FXML
    void reloadButtonOnAction(ActionEvent event) {
        if (actualCard == null)
            return;
        sync();
    }

    @FXML
    void mouseMoved(MouseEvent event) {
        if (actualCard == null)
            return;
        sync();
    }

    private void addCardToBottomDeck(Card card) {
        Image cardImg = cardToImage(card);
        ImageView cardView = new ImageView();
        cardView.setImage(cardImg);
        cardView.setOnMouseClicked(event -> {
            if (canPlayCard(playersCards.get(PlayerBottomDeck.getChildren().indexOf(cardView)))) {
                ChangeCardOnTop(playersCards.get(PlayerBottomDeck.getChildren().indexOf(cardView)));
                playersCards.remove(PlayerBottomDeck.getChildren().indexOf(cardView));
                PlayerBottomDeck.getChildren().remove(cardView);
                shiftHorizontalImage(PlayerBottomDeck);
            }
        });
        PlayerBottomDeck.getChildren().add(cardView);
        shiftHorizontalImage(PlayerBottomDeck);
    }


    private boolean canPlayCard(Card card) {

        System.out.println("trying to play");

        if (!isConnectionOpenned)
            return false;

        out.println("play " + playersCards.indexOf(card));

        try {
            String respond = inRaspond.readLine();
            System.out.println("respond " + respond);
            if (respond.startsWith("OK")) {
                return true;
            } else if (respond.startsWith("NOT")) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    //добавление карт оппонентам
    private void addCardToUpDeck() {
        Image img = new Image(getClass().getResourceAsStream("/sample/fxml/cards/uno.png"), cardWidth, cardHeigth, false, false);
        ImageView cardView = new ImageView();
        cardView.setImage(img);
        PlayerUpDeck.getChildren().add(cardView);
        shiftHorizontalImage(PlayerUpDeck);
    }


    private void shiftHorizontalImage(AnchorPane anchorPane) {
        if (anchorPane.getChildren().size() == 1) {
            AnchorPane.setRightAnchor(anchorPane.getChildren().get(0), 0d);
            return;
        }
        for (int i = 0; i < anchorPane.getChildren().size(); i++) {
            AnchorPane.setRightAnchor(anchorPane.getChildren().get(i),
                    i * (anchorPane.getWidth() - cardWidth) / (anchorPane.getChildren().size() - 1));
        }
    }


    private Image cardToImage(Card card) {
        Integer cardNumber = card.getNumber();
        Integer cardColor = card.getColor();

        BufferedImage bufferedImage = new BufferedImage(cardWidth.intValue(), cardHeigth.intValue(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, cardWidth.intValue(), cardHeigth.intValue());

        Image image = SwingFXUtils.toFXImage(bufferedImage, null);

        try {
            image = new Image(getClass().getResourceAsStream("/sample/fxml/cards/" + cardNumber + "-" + cardColor + ".png"), cardWidth, cardHeigth, false, false);
        } catch (Exception e) {
            System.out.println("Error loading img: " + e.getMessage());
        }

        return image;
    }

    private void ChangeCardOnTop(Card card) {
        cardOnTable.setImage(cardToImage(card));
        actualCard = card;
    }


    public void setIO(BufferedReader in, PrintWriter out, BufferedReader inGate, PrintWriter outGate,
                      BufferedReader inRespond, PrintWriter outRespond, String name, Stage stage) {
        this.in = in;
        this.out = out;
        this.inGate = inGate;
        this.outGate = outGate;
        this.inRaspond = inRespond;
        this.outRespond = outRespond;
        String name1 = name;

        Commands commands = new Commands(in, out, this, name);
        commands.start();

        rootStage = stage;
    }


    public void openConnection() {
        isConnectionOpenned = true;
    }


    public void closeConnection() {
        isConnectionOpenned = false;
    }

    private void sync() {

        if (hasToBeClosed)
            rootStage.close();

        int n = PlayerBottomDeck.getChildren().size();
        while (n-- > 0) {
            PlayerBottomDeck.getChildren().remove(0);
        }

        for (Card ca : playersCards) {
            addCardToBottomDeck(ca);
        }
        ChangeCardOnTop(actualCard);

        n = PlayerUpDeck.getChildren().size();
        while (n-- > 0) {
            PlayerUpDeck.getChildren().remove(0);
        }

        for (int i = 0; i < oponentCount; i++) {
            addCardToUpDeck();
        }

    }

    public void setToCloseTheWindow() {
        hasToBeClosed = true;
    }


    private void setTopCard(Card card) {
        actualCard = card;
    }


    private void addOpponent() {
        oponentCount++;
    }


    private void removeOpponent() {
        oponentCount--;
    }


    class Commands extends Thread {
        BufferedReader in;
        PrintWriter out;
        GameTableController cntl;
        String name;

        Commands(BufferedReader in, PrintWriter out, GameTableController cntl, String name) {
            this.in = in;
            this.out = out;
            this.cntl = cntl;
            this.name = name;
        }


        public void run() {
            try {
                while (true) {
                    System.out.println("Getting command: ");
                    String line = in.readLine();
                    System.out.println(line);
                    if (line.startsWith("NAME")) {
                        out.println(name);
                    } else if (line.startsWith("ADD ")) {
                        String[] arr = line.split(" ");
                        Integer id = Integer.parseInt(arr[1]);
                        Integer color = Integer.parseInt(arr[2]);
                        cntl.playersCards.add(new Card(id, color));

                    } else if (line.startsWith("STARTING")) {
                        String[] arr = line.split(" ");
                        Integer id = Integer.parseInt(arr[1]);
                        Integer color = Integer.parseInt(arr[2]);
                        System.out.println(id + " " + color);
                        cntl.setTopCard(new Card(id, color));
                    } else if (line.startsWith("MESSAGE ")) {
                        String message = line.replace("MESSAGE ", "");
//                         messageLabel.setText(message);
                    } else if (line.startsWith("MESSAGE_END ")) {
                        String message = line.replace("MESSAGE_END ", "");
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame, message, "The end!", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                        setToCloseTheWindow();

                    } else if (line.startsWith("ADD_OPP")) {
                        cntl.addOpponent();
                    } else if (line.startsWith("REM_OPP")) {
                        cntl.removeOpponent();}
                }
            } catch (Exception e) {
                System.out.println("Bad command: " + e.getMessage());
                setToCloseTheWindow();
            }
            System.out.println("Thread the end");
        }
    }
}
