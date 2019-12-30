package main.core;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

public class CardTest {

    @Test
    public void getCardNameTest() {
        boolean isCardName = false;
        Card card = new Card(11, 2010);

        try {
            card.getCardName();
            isCardName = true;
        } catch (Exception e) {
        }
        assertTrue(isCardName);
    }

    @Test
    public void getColorTestBad() {
        boolean isBadColour = false;

        Card card = new Card(6, 2016);

        try {
            card.getColor();
            isBadColour = true;
        } catch (Exception e) {

        }
        assertTrue(isBadColour);
    }

    @Test
    public void getColorNameTest() {
        boolean isColorNameInteger = false;

        try {
            Card.getColorName(2011);
            Card.getColorName(2018);
            Card.getColorName(2000);

            isColorNameInteger = true;

        } catch (Exception e) {

        }

        assertTrue(isColorNameInteger);
    }

    @Test
    public void hasColorNegative() {
        Card hasColor = new Card(2, null);
        assertFalse(hasColor.hasColor());
    }

    @Test
    public void testToString() {
        Card hasColorToString = new Card(2, 2012);
        String expected = hasColorToString.getCardName();
        Assert.assertNotEquals(expected, hasColorToString.toString());
    }

    @Test
    public void testColorName() {
        Card colorName = new Card(2, 2011);
        colorName.getColorName();
    }

}