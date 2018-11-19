package de.petri.onu.client;

import de.petri.onu.game.Card;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GuiStacks extends HBox {

    GuiCard drawStack, putStack;

    Game game;

    public GuiStacks(Game game) {
        this.game = game;

        drawStack = new GuiCard(new Card("nn"));
        drawStack.setCardWidth(125);
        drawStack.setOnAction(e -> {
            game.drawCard();
        });

        putStack = new GuiCard(new Card("nn"), false);
        putStack.setCardWidth(125);

        setSpacing(20);
        getChildren().addAll(drawStack, putStack);
    }

    public Card getPutStack() {
        return new Card(putStack.text);
    }

    public void setPutStack(Card card) {
        putStack.setCard(card);
    }

    public void setActive(boolean b) {
        if(b) {
            drawStack.setActive(true);
        } else {
            drawStack.setActive(false);
        }
    }
}
