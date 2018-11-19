package de.petri.onu.client;

import de.petri.onu.game.Color;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class WinnerDialog {


    public static void display(String winner, Stage window) {
        Stage dialog = new Stage();
        dialog.setResizable(false);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("choose Color!");

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);

        Label lblWinner = new Label();
        lblWinner.setText("The winner is " + winner);
        lblWinner.setFont(new Font(30));
        layout.getChildren().add(lblWinner);

        Button btnClose = new Button("Menu");
        btnClose.setFont(new Font(30));
        btnClose.setOnAction(e -> {
            window.setScene(Main.getMenu());
            dialog.close();
        });
        layout.getChildren().add(btnClose);

        Scene scene = new Scene(layout, 400, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

}
