package de.petri.onu.client;

import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedList;

public class Lobby extends Scene {

    private GridPane layout;
    private VBox vBoxLP;

    Label lblPlayerCount;
    Button btnStart, btnLeave;

    LinkedList<LobbyPlayer> players = new LinkedList<LobbyPlayer>();

    public Lobby(Stage window, Game game) {
        super(new StackPane(), Main.WIDTH, Main.HEIGHT);
        getStylesheets().add(Main.PATH + "client/style.css");

        layout = new GridPane();
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(30,10,10,10));
        setRoot(layout);

        vBoxLP = new VBox();
        vBoxLP.getStyleClass().add("boxLP");
        GridPane.setConstraints(vBoxLP, 0,0);

        lblPlayerCount = new Label("\t   0 of 8 Players");
        lblPlayerCount.getStyleClass().add("LobbyPlayer-box");
        lblPlayerCount.setStyle("-fx-text-alignment: center;");
        vBoxLP.getChildren().add(lblPlayerCount);

        HBox controllBox = new HBox();
        controllBox.setAlignment(Pos.CENTER);
        controllBox.setPadding(new Insets(20,0,0,0));
        controllBox.setSpacing(20);
        GridPane.setConstraints(controllBox, 0,1);

        btnLeave = new Button("Leave");
        btnLeave.setOnAction(e -> {
            game.leave();
        });

        btnStart = new Button("Start");
        btnStart.setDisable(true);
        btnStart.setOnAction(e -> {
            game.start();
        });

        controllBox.getChildren().addAll(btnLeave, btnStart);
        layout.getChildren().addAll(vBoxLP, controllBox);
    }

    public void addPlayer(String name, int ping) {
        players.add(new LobbyPlayer(name, ping));
        vBoxLP.getChildren().addAll(players.getLast());
        lblPlayerCount.setText("\t   " + players.size() + " of 8 Players");
    }

    public void removePlayer(String name) {
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getName().equals(name)) {
                vBoxLP.getChildren().remove(players.get(i));
                players.remove(i);
                lblPlayerCount.setText("\t   " + players.size() + " of 8 Players");
                return;
            }
        }
    }

    public void updatePlayers(int[] ping) {
        for (int i = 0; i < ping.length; i++) {
            players.get(i).setPing(ping[i]);
        }
    }

    public void setAdmin(boolean isAdmin) {
        if(isAdmin) {
            btnStart.setDisable(false);
        } else {
            btnStart.setDisable(true);
        }
    }

    private class LobbyPlayer extends GridPane {
        private String name;
        private int ping;

        Label lblName, lblPing;

        public LobbyPlayer(String name, int ping) {
            this.name = name;
            this.ping = ping;
            getStylesheets().add(Main.PATH + "client/style.css");
            getStyleClass().add("LobbyPlayer-box");
            setPadding(new Insets(10,0,10,10));

            lblName = new Label(name);
            lblName.getStyleClass().add("LobbyPlayer-name");
            GridPane.setConstraints(lblName, 0,0);

            lblPing = new Label("(" + String.valueOf(ping) + "ms)");
            lblPing.getStyleClass().add("LobbyPlayer-ping");
            GridPane.setConstraints(lblPing, 1,0);

            getChildren().addAll(lblName, lblPing);
        }

        public void setPing(int ping) {
            this.ping = ping;
            lblPing.setText(String.valueOf(ping));
        }

        public String getName() {
            return name;
        }
    }
}
