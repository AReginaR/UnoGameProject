package main.core;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class Game {

    private static ArrayList<Card> createStack(){
        ArrayList<Card> deck = new ArrayList<>();

        for(int k = 0; k < 2; k++) {
            for (int i = k; i <= 12; i++) {
                deck.add(new Card(i, Card.ColorRed));
                deck.add(new Card(i, Card.ColorGreen));
                deck.add(new Card(i, Card.ColorBlue));
                deck.add(new Card(i, Card.ColorYellow));
            }
        }

        return deck;
    }

    private ArrayList<Card> stack; //колода
    private ArrayList<Player> players;
    private Integer playerCount;
    private Integer actualPlayer;
    private Integer directory;
    private final static Integer StartingCards = 7;
    private final static Integer MinPlayers = 2;
    private final static Integer MaxPlayers = 2;

    private Integer actualNumber;
    private Integer actualColor;
    private Integer cardsToDraw = 0;


    private BufferedReader[] inputFromP;
    private BufferedReader[] inputFromPGate;
    private BufferedReader[] inputFromPRespond;

    private PrintWriter[] outputToP;
    private PrintWriter[] outputToPGate;
    private PrintWriter[] outputToPRespond;

    private Card takeFromStack(){
        Card toReturn = stack.get(0);
        stack.remove(0);
        return toReturn;
    }

    private void addToStack(Card cardToAdd) {
        stack.add(cardToAdd);
    }


    public Game(BufferedReader[] inP, PrintWriter[] outP, BufferedReader[] inPG, PrintWriter[] outPG, BufferedReader[] inPR, PrintWriter[] outPR) throws Exception {

        inputFromP = inP;
        outputToP = outP;
        inputFromPGate = inPG;
        outputToPGate = outPG;
        inputFromPRespond = inPR;
        outputToPRespond = outPR;


        stack = createStack();
        players = new ArrayList<>();
        Collections.shuffle(stack);

        printInitMessage();

        playerCount = 2;
        actualPlayer = 0;
        directory = 1;

        if( playerCount < MinPlayers || MaxPlayers < playerCount){
            throw new Exception("Bad player count!");
        }

        for(int i = 0; i < playerCount; i++){
            System.out.print("Player number " + (i + 1) + " name: ");

            outputToP[i].println("NAME");
            String playerName = inputFromP[i].readLine();
            System.out.println(playerName);

            players.add(new Player(playerName));
        }

        for(int i = 0; i < StartingCards; i++){
            for(Player player : players){
                Card c = takeFromStack();
                player.addCard(c);
                outputToP[players.indexOf(player)].println("ADD " + c.getNumber() + " " + c.getColor());
                outputToP[players.indexOf(player)].println("ADD_OPP");
            }
        }

        System.out.println("The game is ready!");
    }

    private void printInitMessage() {
        System.out.println("Hello in UNO!");
        System.out.print("Players: ");
    }

    private void nextPlayer(){
        actualPlayer = (actualPlayer + directory + playerCount) % playerCount;
    }

    private void reverse(){
        if(directory == 1) directory = playerCount - 1;
        else directory = 1;
    }

    public void play(){

        try {
            Card startingCard = takeFromStack();

            while(!startingCard.isNormalCard()){
                addToStack(startingCard);
                startingCard = takeFromStack();
            } //карта должна быть циферкой

            System.out.println(startingCard);

            actualColor = startingCard.getColor();
            actualNumber = startingCard.getNumber();

            for (int i = 0; i < playerCount ; i++) {
                outputToP[i].println("STARTING " + actualNumber + " " + actualColor);
            }

            while(true) {
                while(cardsToDraw --> 0){
                    addCardToActualPlayerFromStackAndReloadOpponent();
                }

                System.out.println("Player " + players.get(actualPlayer).getPlayerName() + " (" + (actualPlayer + 1) + ")" + " turn!");
                players.get(actualPlayer).printPlayersDeck();

                outputToPGate[actualPlayer].println("OPEN");
                String playersChoice = inputFromP[actualPlayer].readLine();
                System.out.println(playersChoice);
                outputToPGate[actualPlayer].println("CLOSE");

                if(playersChoice.equals("draw")){
                    addCardToActualPlayerFromStackAndReloadOpponent();

                    System.out.println("Player " + players.get(actualPlayer).getPlayerName() + " (" + (actualPlayer + 1) + ")" + " turn!");
                    players.get(actualPlayer).printPlayersDeck();


                    outputToPGate[actualPlayer].println("OPEN");
                    String playersChoice2 = inputFromP[actualPlayer].readLine();
                    System.out.println(playersChoice2);
                    outputToPGate[actualPlayer].println("CLOSE");
                    if(playersChoice2.startsWith("play")){

                        if(players.get(actualPlayer).getDeck().size() == 0){
                            outputToP[actualPlayer].println("MESSAGE_END You WON!");
                            outputToP[(actualPlayer + 1) % playerCount].println("MESSAGE_END " + players.get(actualPlayer).getPlayerName() + " WON!");
                            System.out.println(players.get(actualPlayer).getPlayerName() + " WON!");
                            break;

                        }
                        play1(playersChoice2);
                        nextPlayer();
                    } else if(playersChoice2.equals("skip")){
                        nextPlayer();
                    }

                } else if(playersChoice.startsWith("play")){
                    int cardNumber = Integer.parseInt(playersChoice.split(" ")[1]);
                    Card chosenCard = players.get(actualPlayer).getDeck().get(cardNumber);
                    if(!canPlayCard(chosenCard)) {
                        outputToPRespond[actualPlayer].println("NOT");
                        continue;
                    }

                    outputToPRespond[actualPlayer].println("OK");

                    outputToP[(actualPlayer + 1) % 2].println("STARTING " + chosenCard.getNumber() + " " + chosenCard.getColor());

                    players.get(actualPlayer).removeCard(cardNumber);
                    outputToP[(actualPlayer + 1) % playerCount].println("REM_OPP");

                    if(players.get(actualPlayer).getDeck().size() == 0){
                        outputToP[actualPlayer].println("MESSAGE_END You WON!");
                        outputToP[(actualPlayer + 1) % playerCount].println("MESSAGE_END " + players.get(actualPlayer).getPlayerName() + " WON!");
                        System.out.println(players.get(actualPlayer).getPlayerName() + " WON!");
                        break;
                    }

                    addToStack(chosenCard);
                    if (chosenCard.getAction() == 1){
                        actualNumber = chosenCard.getNumber();
                        actualColor = chosenCard.getColor();
                    }else if (chosenCard.getAction() == 2) {
                        nextPlayer();
                    } else if (chosenCard.getAction() == 3) {
                        reverse();
                    } else if (chosenCard.getAction() == 4) {
                        cardsToDraw = 2;
                    }
                    nextPlayer();
                } else {
                    System.out.println("Bad command!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addCardToActualPlayerFromStackAndReloadOpponent() {
        Card c = takeFromStack();
        players.get(actualPlayer).addCard(c);
        outputToP[actualPlayer].println("ADD " + c.getNumber() + " " + c.getColor());
        outputToP[(actualPlayer + 1) % playerCount].println("ADD_OPP");
    }

    private boolean canPlayCard(Card card) {

        if(actualNumber == null && card.getColor().equals(actualColor)){
            return true;
        }
        return card.getNumber().equals(actualNumber) || card.getColor().equals(actualColor);
    }

    private void play1(String choice) {
        int cardNumber = Integer.parseInt(choice.split(" ")[1]);
        Card chosenCard = players.get(actualPlayer).getDeck().get(cardNumber);
        if(!canPlayCard(chosenCard)) {
            outputToPRespond[actualPlayer].println("NOT");
        }

        outputToPRespond[actualPlayer].println("OK");

        outputToP[(actualPlayer + 1) % 2].println("STARTING " + chosenCard.getNumber() + " " + chosenCard.getColor());

        players.get(actualPlayer).removeCard(cardNumber);
        outputToP[(actualPlayer + 1) % playerCount].println("REM_OPP");

        addToStack(chosenCard);
        if (chosenCard.getAction() == 1){
            actualNumber = chosenCard.getNumber();
            actualColor = chosenCard.getColor();
        }
        if (chosenCard.getAction() == 2) {
            nextPlayer();
        }
        if (chosenCard.getAction() == 3) {
            reverse();
        }
        if (chosenCard.getAction() == 4) {
            cardsToDraw = 2;
        }

    }

}