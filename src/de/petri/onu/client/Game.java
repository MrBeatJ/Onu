package de.petri.onu.client;

import de.petri.onu.helper.MessageConverter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.UUID;

public class Game extends Scene {

    //Client
    Client client;
    private boolean admin;

    Thread listen;
    boolean running = false;

    MessageConverter mc;

    //Gui
    Stage window;
    Lobby lobby;

    //Game
    ArrayList<GamePlayer> players = new ArrayList<GamePlayer>();

    public Game(Stage window, String name, String address) {
        super(new StackPane(), Main.WIDTH, Main.HEIGHT);
        this.window = window;

        lobby = new Lobby(window, this);

        client = new Client(name, address);
        client.openConnection();

        mc = new MessageConverter();

        running = true;

        listen();
        join();
    }

    private void join() {
        String connection = mc.tagged(mc.tagged(client.name,"name") , "join");
        client.send(connection);
    }

    private void listen() {
        listen = new Thread(() -> {
            while(running) {
                String message = client.receive();

                System.out.println(message);

                if(running) process(message);
            }
        });
        listen.start();
    }

    private void process(String message) {
        //JOIN - answer
        if(message.startsWith("<join>")) {
            if(mc.hasTag(message, "error")) {
                //Server läuft bereits oder ist voll
            } else {
                //adds all other players
                String[] otherPlayers = mc.getBetweenTag(mc.getBetweenTag(message, "join")[0], "cName");
                for (String player: otherPlayers) {
                    addPlayer(player);
                }

                //add UUID
                String id = mc.getBetweenTag(mc.getBetweenTag(message, "join")[0], "id")[0];
                client.setId(UUID.fromString(id));

                //sets your name, maybe changed by server
                client.name = mc.getBetweenTag(mc.getBetweenTag(message, "join")[0], "name")[0];
            }
        }
        //PING
        else if(message.startsWith("<ping>")) {
            String msg = mc.tagged(mc.tagged(client.getId().toString(), "id"), "ping");
            client.send(msg);
        }
        //ADD
        else if(message.startsWith("<add>")) {
            String name = mc.getBetweenTag(mc.getBetweenTag(message, "add")[0], "name")[0];
            if(!name.equals(client.name)) {
                addPlayer(name);
            }
        }
        //LEAVE
        else if(message.startsWith("<leave>")) {
            removePlayer(mc.getBetweenTag(mc.getBetweenTag(message, "leave")[0], "name")[0]);
        }
        //ADMIN
        else if(message.startsWith("<admin>")) {
            if(mc.getBetweenTag(mc.getBetweenTag(message, "admin")[0], "name")[0].equals(client.name)) {
                setAdmin(true);
            } else {
                setAdmin(false);
            }
        }
    }

    private void addPlayer(String name) {
        players.add(new GamePlayer(name));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lobby.addPlayer(name, 0);
            }
        });
    }

    private void setAdmin(boolean isAdmin) {
        if(isAdmin) {
            admin = true;

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    lobby.setAdmin(true);
                }
            });
        } else {
            admin = false;

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    lobby.setAdmin(false);
                }
            });
        }
    }

    private void removePlayer(String name) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lobby.removePlayer(name);
            }
        });
        for (GamePlayer player : players) {
            if(player.getName().equals(name)) players.remove(player);
        }
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void leave() {
        close();
        window.setScene(Main.getMenu());
    }

    public void start() {
        //send start command to server
        client.send(mc.tagged("", "start"));

        //display game
        window.setScene(this);
    }

    public void close() {
        String msg = mc.tagged(client.getId().toString(), "leave");
        client.send(msg);
        client.close();
        running = false;
    }


}
