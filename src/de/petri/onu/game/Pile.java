package de.petri.onu.game;

import java.util.Collections;
import java.util.Stack;

public class Pile {

    private Stack<Card> cards = new Stack<Card>();

    //adds a card to the top
    public void push(Card card) {
        cards.push(card);
    }

    //removes top card
    public Card pop() {
        return cards.pop();
    }

    //gives back the amount of cards left in the hand
    public int size() {
        return cards.size();
    }

    //shuffles the stack
    public void shuffle() {
        Collections.shuffle(cards);
    }
}
