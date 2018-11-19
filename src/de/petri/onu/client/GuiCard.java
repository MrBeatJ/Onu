package de.petri.onu.client;

import de.petri.onu.game.Card;
import de.petri.onu.game.Value;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

    private final double ratio = 4/3d;
    private int width = 75;
    private int height = (int) (((double) width) * ratio);

    public final int MAX_WIDTH = width;

    Game game;

    public GuiCard(Card card) {
        loadCard(card);

        //creates and adds the button to the GuiCard
        btnPick = new Button("Pick");
        btnPick.setMinWidth(width);
        btnPick.getStyleClass().add("GuiCard-pick");
        GridPane.setConstraints(btnPick, 0,1);
        getChildren().add(btnPick);

    }

    public GuiCard(Card card, boolean btn) {
        loadCard(card);

        if(btn) {
            //creates and adds the button to the GuiCard
            btnPick = new Button("Pick");
            btnPick.setMinWidth(width);
            btnPick.getStyleClass().add("GuiCard-pick");
            GridPane.setConstraints(btnPick, 0, 1);
            getChildren().add(btnPick);
        }
    }

    private void loadCard(Card card) {
        text = card.toString();

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

        getChildren().add(paneCard);
    }

    public void setCard(Card card) {
        text = card.toString();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loadCard(card);
            }
        });
    }

    public void setOnAction(EventHandler<ActionEvent> e) {
        btnPick.setOnAction(e);
    }

    public int getCardHeight() {
        return height;
    }

    public int getCardWidth() {
        return width;
    }

    public String getText() {
        return text;
    }

    public void setCardWidth(int width) {
        this.width = width;
        this.height = (int) (((double) width) * ratio);

        imgCard.setFitHeight(height);
        imgCard.setFitWidth(width);

        imgFace.setFitHeight(height);
        imgFace.setFitWidth(width);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(btnPick != null) {
                    btnPick.setMinWidth(width);
                }
            }
        });

        paneCard.setMaxHeight(height);
        paneCard.setMaxWidth(width);
    }

    public void setActive(boolean b) {
        if(b) {
            btnPick.setDisable(false);
        } else {
            btnPick.setDisable(true);
        }
    }
}