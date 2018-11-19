package de.petri.onu.game;

public class Card {

    private Color color;
    private Value value;
    
    public Card(Color color, Value value) {
        this.color = color;
        this.value = value;
    }

    public Card(String card) {
        String color = String.valueOf(card.charAt(0));
        String value = String.valueOf(card.charAt(1));

        this.color = getColorFromString(color);
        this.value = getValueFromString(value);
    }

    private Color getColorFromString(String color) {
        switch(color) {
            case "b":
                return Color.BLUE;
            case "g":
                return Color.GREEN;
            case "r":
                return Color.RED;
            case "y":
                return Color.YELLOW;
            case "n":
                return Color.NONE;
        }
        return null;
    }

    private Value getValueFromString(String value) {
        switch(value) {
            case "0":
                return Value.ZERO;
            case "1":
                return Value.ONE;
            case "2":
                return Value.TWO;
            case "3":
                return Value.THREE;
            case "4":
                return Value.FOUR;
            case "5":
                return Value.FIVE;
            case "6":
                return Value.SIX;
            case "7":
                return Value.SEVEN;
            case "8":
                return Value.EIGHT;
            case "9":
                return Value.NINE;
            case "c":
                return Value.COLOR;
            case "d":
                return Value.DRAW;
            case "b":
                return Value.BLOCK;
            case "s":
                return Value.SWITCH;
            case "n":
                return Value.NONE;
        }
        return null;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String toString() {
        String text = "";

        String c = "";
        switch(color) {
            case RED: c = "r";
                break;
            case BLUE: c = "b";
                break;
            case YELLOW: c = "y";
                break;
            case GREEN: c = "g";
                break;
            case NONE: c = "n";
        }

        String v = "";
        switch(value) {
            case ZERO: v = "0";
                break;
            case ONE: v = "1";
                break;
            case TWO: v = "2";
                break;
            case THREE: v = "3";
                break;
            case FOUR: v = "4";
                break;
            case FIVE: v = "5";
                break;
            case SIX: v = "6";
                break;
            case SEVEN: v = "7";
                break;
            case EIGHT: v = "8";
                break;
            case NINE: v = "9";
                break;
            case BLOCK: v = "b";
                break;
            case SWITCH: v = "s";
                break;
            case DRAW: v = "d";
                break;
            case COLOR: v = "c";
                break;
            case NONE: v = "n";
                break;
        }

        text = c + v;
        return text;
    }

    public Color getColor() {
        return color;
    }

    public Value getValue() {
        return value;
    }
}
