package de.petri.onu.client;

import de.petri.onu.game.Color;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ColorDialog {

    private static Color color;

    public static Color display() {
        Stage window = new Stage();
        window.setResizable(false);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("choose Color!");

        GridPane layout = new GridPane();

        Button btnRed = new Button("   ");
        btnRed.setStyle("-fx-background-color: red");
        btnRed.setMinWidth(200);
        btnRed.setMinHeight(200);
        btnRed.setOnAction(e -> {
            color = Color.RED;
            window.close();
        });
        layout.add(btnRed, 0, 0);

        Button btnBlue = new Button("   ");
        btnBlue.setStyle("-fx-background-color: blue");
        btnBlue.setMinWidth(200);
        btnBlue.setMinHeight(200);
        btnBlue.setOnAction(e -> {
            color = Color.BLUE;
            window.close();
        });
        layout.add(btnBlue, 1, 0);

        Button btnYellow = new Button("   ");
        btnYellow.setStyle("-fx-background-color: yellow");
        btnYellow.setMinWidth(200);
        btnYellow.setMinHeight(200);
        btnYellow.setOnAction(e -> {
            color = Color.YELLOW;
            window.close();
        });
        layout.add(btnYellow, 0, 1);

        Button btnGreen = new Button("   ");
        btnGreen.setStyle("-fx-background-color: green");
        btnGreen.setMinWidth(200);
        btnGreen.setMinHeight(200);
        btnGreen.setOnAction(e -> {
            color = Color.GREEN;
            window.close();
        });
        layout.add(btnGreen, 1, 1);

        Scene scene = new Scene(layout, 400, 400);
        window.setScene(scene);
        window.showAndWait();

        return color;
    }

}
