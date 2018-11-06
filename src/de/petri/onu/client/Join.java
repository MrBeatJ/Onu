package de.petri.onu.client;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Join extends Scene {

    private GridPane layout;

    private Button btnJoin, btnBack;
    private Label lblName, lblAddress;
    private TextField txtName, txtAddress;

    public Join(Stage window) {
        super(new StackPane(),  Main.WIDTH, Main.HEIGHT);
        getStylesheets().add(Main.PATH + "client/style.css");

        layout = new GridPane();
        layout.setPadding(new Insets(10,10,10,10));
        layout.setVgap(8);
        layout.setHgap(10);
        setRoot(layout);

        //Name
        lblName = new Label("Name:");
        GridPane.setConstraints(lblName, 0, 0);

        txtName = new TextField();
        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty() || txtAddress.getText().isEmpty()) {
                btnJoin.setDisable(true);
            } else {
                btnJoin.setDisable(false);
            }
        });
        GridPane.setConstraints(txtName, 1, 0);

        //Address
        lblAddress = new Label("Address:");
        GridPane.setConstraints(lblAddress, 0, 1);

        txtAddress = new TextField();
        txtAddress.setPromptText("192.168.0.1");
        String regex = makePartialIPRegex();
        final UnaryOperator<TextFormatter.Change> ipAddressFilter = c -> {
            String text = c.getControlNewText();
            if  (text.matches(regex)) {
                return c ;
            } else {
                return null ;
            }
        };
        txtAddress.setTextFormatter(new TextFormatter<>(ipAddressFilter));
        txtAddress.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue.isEmpty() || txtName.getText().isEmpty()) {
                btnJoin.setDisable(true);
            } else {
                btnJoin.setDisable(false);
            }
        });
        GridPane.setConstraints(txtAddress, 1, 1);

        //Menu
        btnBack = new Button("Back");
        btnBack.setOnAction(e -> {
            window.setScene(Main.getMenu());
        });
        GridPane.setConstraints(btnBack, 0,2);

        btnJoin = new Button("Join");
        btnJoin.setDisable(true);
        btnJoin.setOnAction(e -> {
            String name = txtName.getText();
            String address = txtAddress.getText();

            if(!name.isEmpty() && !address.isEmpty()) {
                Main.startGame(name, address);
            }
        });
        GridPane.setConstraints(btnJoin, 1,2);

        layout.getChildren().addAll(lblName, lblAddress, txtName, txtAddress, btnBack, btnJoin);
    }

    private String makePartialIPRegex() {
        String partialBlock = "(([01]?[0-9]{0,2})|(2[0-4][0-9])|(25[0-5]))" ;
        String subsequentPartialBlock = "(\\."+partialBlock+")" ;
        String ipAddress = partialBlock+"?"+subsequentPartialBlock+"{0,3}";
        return "^"+ipAddress ;
    }
}
