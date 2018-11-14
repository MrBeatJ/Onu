package de.petri.onu.client;

import de.petri.onu.game.Card;
import de.petri.onu.game.Value;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GuiCard extends GridPane {
    String text;

    StackPane paneCard;
    ImageView imgCard, imgFace;
    Button btnPick;

    private final double ratio = 3/4d;
    private int height = 100;
    private int width = (int) (((double) height) * ratio);

    Game game;

    public GuiCard(Card card, Game game) {
        text = card.toString();
        this.game = game;

        //creates the StackPane for the cards look
        paneCard = new StackPane();
        paneCard.setMaxHeight(height);
        paneCard.setMaxWidth(width);
        GridPane.setConstraints(paneCard, 0,0);

        //creates the cardImage
        Image cardImage = Game.cardImageHandler.getCardImage(card.toString());
        imgCard = new ImageView(cardImage);
        imgCard.setFitHeight(height);
        imgCard.setFitWidth(width);

        //creates the faceImage
        Image faceImage = Game.cardImageHandler.getFaceImage(card.toString());
        imgFace = new ImageView(faceImage);
        imgFace.setFitHeight(height);
        imgFace.setFitWidth(width);

        //adds cardImage and faceImage to the StackPane
        paneCard.getChildren().addAll(imgCard, imgFace);

        //creates and adds the button to the GuiCard
        btnPick = new Button("Pick");
        btnPick.setMinWidth(width);
        btnPick.getStyleClass().add("GuiCard-pick");
        btnPick.setOnAction(e -> {
            game.pickCard(card);
        });
        GridPane.setConstraints(btnPick, 0,1);

        //adds everything to the GuiCard
        getChildren().addAll(paneCard, btnPick);
    }

    public int getCardHeight() {
        return height;
    }

    public void setCardHeight(int height) {
        this.height = height;
        this.width = (int) (((double) height) * ratio);

        imgCard.setFitHeight(height);
        imgCard.setFitWidth(width);

        imgFace.setFitHeight(height);
        imgFace.setFitWidth(width);

        paneCard.setMaxHeight(height);
        paneCard.setMaxWidth(width);
    }

    public int getCardWidth() {
        return width;
    }

    public String getText() {
        return text;
    }
}