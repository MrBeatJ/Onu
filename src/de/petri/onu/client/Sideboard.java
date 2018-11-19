package de.petri.onu.client;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class Sideboard extends HBox {

    private ArrayList<GamePlayer> players;

    private VBox boxDirection, boxNames, boxCards;
    private final int width = 200;
    private final int lineHeight = 40;

    private ImageView imgDirection;
    Image imgUp, imgDown;

    String direction = "+";

    public Sideboard(ArrayList<GamePlayer> players) {
        this.players = players;

        setStyle("-fx-background-color: #aaaaaa");
        setMinSize(width, lineHeight * players.size());
        setMaxSize(width, lineHeight * players.size());
        setSpacing(10);

        boxDirection = new VBox();
        boxNames = new VBox();
        boxCards = new VBox();
        getChildren().addAll(boxDirection, boxNames, boxCards);

        //setPlayer(0);

        for (GamePlayer player : players) {
            setHeight(lineHeight);
            boxNames.getChildren().add(player.getLblName());
            boxCards.getChildren().add(player.getLblCount());
        }
    }

    public void setDirection(boolean up) {
        if(up) {
            direction = "-";
        } else {
            direction = "+";
        }
    }

    public void setPlayer(int index) {
        for(int i = 0; i < index - 1; i++) {
            boxDirection.getChildren().add(new Label("    "));
            System.out.println("test");
        }
        boxDirection.getChildren().add(new Label(direction));
    }

    public void updateCount(String name, int count) {
        for (GamePlayer player : players) {
            if(player.getName().equals(name)) {
                player.updateCount(count);
            }
        }
    }
}
