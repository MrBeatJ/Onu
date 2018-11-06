package de.petri.onu.client;

public class GamePlayer {
    private String name;
    private int cards;

    public GamePlayer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getCards() {
        return cards;
    }

    public void setCards(int cards) {
        this.cards = cards;
    }
}
