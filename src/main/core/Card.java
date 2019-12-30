package main.core;

public class Card {

    final static public Integer ColorBase = 2010;
    final static public Integer ColorRed = ColorBase + 1;
    final static public Integer ColorGreen = ColorBase + 2;
    final static public Integer ColorBlue = ColorBase + 3;
    final static public Integer ColorYellow = ColorBase + 4;

    final static String[] ColorNames = { "Red", "Green", "Blue", "Yellow" };
    private final static String[] CardNames = { "Skip", "Reverse", "DrawTwo", "Wild", "WildDraw" };

    private final static Integer NumberOfCards = 14;

    private Integer number;
    private Integer color;
    private Integer action;

    public Card(Integer cardId, Integer cardColor) {
        try {
            number = cardId;
            color = cardColor;
            action = readActionForNumber(cardId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private Integer readActionForNumber(Integer cardId) throws Exception {
        if (cardId < 0 || cardId > NumberOfCards) {
            throw new Exception("Bad Card Number!");
        }
        if (cardId <= 9) {
            return 1;
        } else {
            return cardId - 8;
        }
    }

    public Integer getNumber() {
        return number;
    }

    public Integer getColor() {
        return color;
    }

    String getColorName() {
        if (color.equals(ColorBase))
            return null;
        return ColorNames[color - ColorBase - 1];
    }

    String getCardName() {
        if (number < 10) {
            return String.valueOf(number);
        } else {
            return CardNames[number - 10];
        }
    }

    static String getColorName(Integer color) {
        if (color <= ColorBase || ColorYellow < color)
            return null;
        return ColorNames[color - ColorBase - 1];
    }

    static String getCardName(Integer cardId) {
        if (cardId < 0 || cardId > NumberOfCards) {
            return null;
        }
        if (cardId < 10) {
            return String.valueOf(cardId);
        } else {
            return CardNames[cardId - 10];
        }
    }

    Integer getAction() {
        return action;
    }

    boolean hasColor() {
        return (color != null);
    }

    boolean isNormalCard() {
        return (action == 1);
    }

    @Override
    public String toString() {
        if (!hasColor())
            return getCardName();
        return getCardName() + " " + getColorName();
    }
}