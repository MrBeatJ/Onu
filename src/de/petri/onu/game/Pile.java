package de.petri.onu.game;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class Pile {

    private Stack<Card> cards;

    public Pile() {
        cards = new Stack<Card>();
    }

    //adds a card to the top
    public void push(Card card) {
        cards.push(card);
    }

    //removes top card
    public Card pop() {
        return cards.pop();
    }

    public Card peek() {
        return cards.peek();
    }

    //gives back the amount of cards left in the hand
    public int size() {
        return cards.size();
    }

    //shuffles the stack
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    public static Pile loadFromText(InputStream is) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));

            ArrayList<String> cards = new ArrayList<String>();
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.startsWith("//")) {
                    cards.add(line);
                }
            }

            Pile pile = new Pile();
            for (String card : cards) {
                pile.push(new Card(card));
            }

            return pile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isAcceptable(Card card) {
        if(card.getColor() == cards.peek().getColor()) {
            return true;
        } if(card.getValue() == cards.peek().getValue() || card.getValue() == Value.COLOR) {
            return true;
        } else {
            return false;
        }
    }
}
