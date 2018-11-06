package de.petri.onu.client;

import de.petri.onu.helper.MessageConverter;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main extends Application {

    public static final String PATH = "de/petri/onu/";

    //Gui
    private static Stage window;
    private static Scene menu;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static OutputStream out;
    public static InputStream in;

    public static boolean runningServer = false;

    //Game
    private static Game game;
    private boolean gameRunning = false;

    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Onu");

        VBox layout = new VBox();
        layout.setPadding(new Insets(55,0,0,0));
        layout.setSpacing(55);
        layout.setAlignment(Pos.TOP_CENTER);

        menu = new Scene(layout, WIDTH, HEIGHT);
        menu.getStylesheets().add(PATH + "client/style.css");

        Label lblOnu = new Label("Onu");
        lblOnu.getStyleClass().add("menu-label");

        Button btnJoin = new Button("Join");
        btnJoin.getStyleClass().add("menu-button");
        btnJoin.setOnAction(e -> {
            window.setScene(new Join(window));
        });

        Button btnLobby = new Button("Lobby");
        btnLobby.getStyleClass().add("menu-button");
        btnLobby.setOnAction(e -> {
            startServer();
            game = new Game(window, "Admin", "localhost");
            window.setScene(game.getLobby());
        });

        Button btnSettings = new Button("Settings");
        btnSettings.getStyleClass().add("menu-button");
        btnSettings.setDisable(true);

        Button btnQuit = new Button("Quit");
        btnQuit.getStyleClass().add("menu-button");
        btnQuit.setOnAction(e -> {
            window.close();
        });

        layout.getChildren().addAll(lblOnu, btnJoin, btnLobby, btnSettings, btnQuit);

        window.setScene(menu);
        window.show();
    }
    public static void startGame(String name, String address) {
        game = new Game(window, name, address);
        window.setScene(game.getLobby());
    }

    private void startServer() {
        if(runningServer) {
            stopServer();
        }

        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec("java -jar src/de/petri/onu/client/Onu.jar 9000");
        } catch (IOException e) {
            e.printStackTrace();
        }

        out = proc.getOutputStream();
        in = proc.getInputStream();

        Scanner s = new Scanner(in);
        PrintWriter w = new PrintWriter(out);

        runningServer = true;
    }

    @Override
    public void stop() {
            stopServer();
            if(game != null) {
                game.close();
            }
    }

    private void stopServer() {
        if(runningServer) {
            String cmd = "stop";
            try {
                out.write(cmd.getBytes());
                out.flush();
                in.close();
                out.close();
                runningServer = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Scene getMenu() {
        return menu;
    }
}
