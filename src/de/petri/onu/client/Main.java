package de.petri.onu.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {

    public static final String PATH = "de/petri/onu/";

    //Gui
    private static Stage window;
    private static Scene menu;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private Process serverProc;
    private Thread watch, watchError;
    private static BufferedReader wReader, eReader;

    public static OutputStream out;
    public static InputStream in;

    public static boolean runningServer = false;

    //Game
    private static Game game;
    private boolean gameRunning = false;
    public static boolean DEVELOPMENT = false;

    public static void main(String[] args) {

        if(args.length != 0 && args[0].equals("dev")) {
            DEVELOPMENT = true;
        }
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Onu");
        window.setResizable(false);

        VBox layout = new VBox();
        layout.setPadding(new Insets(55,0,0,0));
        layout.setSpacing(55);
        layout.setAlignment(Pos.TOP_CENTER);

        menu = new Scene(layout, WIDTH, HEIGHT);
        menu.getStylesheets().add("/gfx/css/style.css");

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

        try {
            String name =  new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
            String path = "";
            path = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            path = path.substring(0, path.length() - name.length());
            if(DEVELOPMENT == true) path += "nu/";
            path += "OnuServer.jar";

            serverProc = Runtime.getRuntime().exec("cmd /c java -jar " + path + " 9000");

        } catch (IOException e) {
            e.printStackTrace();
        }

        out = serverProc.getOutputStream();
        in = serverProc.getInputStream();

        watchProc(serverProc);
        errorProc(serverProc);

        runningServer = true;
    }

    @Override
    public void stop() {
            stopServer();
            if(game != null) {
                game.close();
            }
    }

    private void errorProc(final Process process) {
        watchError = new Thread() {
            public void run() {
                this.setName("watchError");
                eReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line = null;
                try {
                    while ((line = eReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        watchError.start();
    }

    private void watchProc(final Process process) {
        watch = new Thread() {
            public void run() {
                this.setName("watchProc");
                wReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = null;
                try {
                    while ((line = wReader.readLine()) != null) {
                        if(line.equals("ready")) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    game = new Game(window, "Admin", "127.0.0.1");
                                    window.setScene(game.getLobby());
                                }
                            });
                        }
                        if(line.equals("stopped")) {
                            stopServer();
                        }
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        watch.start();
    }

    private void stopServer() {
        if(runningServer) {
            String cmd = "stop";
            try {
                wReader.close();
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
