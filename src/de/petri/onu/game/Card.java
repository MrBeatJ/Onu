package de.petri.onu.game;

public class Card {

    private Color color;
    private Value value;
    
    private String text;

    public Card(Color color, Value value) {
        this.color = color;
        this.value = value;
    }

    public Card(String card) {
        String color = card.substring(0,0).toLowerCase();
        String value = card.substring(1,1).toLowerCase();

        this.color = getColorFromString(color);
        this.value = getValueFromString(value);
        
        text = card;
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
        }
        return null;
    }
    
    public String toString() {
        return text;   
    }

    public Color getColor() {
        return color;
    }

    public Value getValue() {
        return value;
    }
}
