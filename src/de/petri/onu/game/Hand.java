package de.petri.onu.game;

import java.util.ArrayList;

public class Hand {

    private ArrayList<Card> cards = new ArrayList<>();

    //adds a card
    public void addCard(Card card) {
        cards.add(card);
    }

    //removes and gives back the card at the index
    public Card removeCard(int index) {
        return cards.remove(index);
    }
    
    public Card removeCard(Card card) {
        for (int i = 0; i < cards.size(); i++) {
            if(cards.get(i).toString().equals(card.toString())) {
                cards.remove(i);
            }
        }
        return card;
    }

    //gives back the amount of cards left in the hand
    public int getCount() {
        return cards.size();
    }

    //gives back an array of all cards in the hand
    public Card[] getCards() {
        Card[] cards = new Card[getCount()];

        for (int i = 0; i < getCount(); i++) {
            cards[i] = this.cards.get(i);
        }
        return cards;
    }

    public int getEndValue() {
        int sum = 0;
        for (Card card : cards) {
            sum += card.getEndValue();
        }
        return sum - 10;
    }
}
