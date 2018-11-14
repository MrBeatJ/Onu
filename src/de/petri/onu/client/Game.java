package de.petri.onu.client;

import de.petri.onu.game.Card;
import de.petri.onu.game.Hand;
import de.petri.onu.game.Value;
import de.petri.onu.helper.CardImageHandler;
import de.petri.onu.helper.MessageConverter;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;

public class Game extends Scene {

    //Client
    private Client client;
    private boolean admin;

    Thread listen;
    boolean running = false;

    MessageConverter mc;

    //Gui
    private Stage window;
    private Lobby lobby;

    private final GridPane layout;

    HBox hBoxHand;

    //Game
    Hand hand;
    LinkedList<GuiCard> guiCards;
    static CardImageHandler cardImageHandler;
    ArrayList<GamePlayer> players = new ArrayList<GamePlayer>();

    public Game(Stage window, String name, String address) {
        super(new StackPane(), Main.WIDTH, Main.HEIGHT);
        this.window = window;

        getStylesheets().add("gfx/css/style.css");

        cardImageHandler = new CardImageHandler();

        layout = new GridPane();
        layout.setAlignment(Pos.BOTTOM_CENTER);
        layout.setPadding(new Insets(30,10,10,10));
        setRoot(layout);

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
        //- GAME -\\
        //START
        else if(message.startsWith("<start>")) {
            startGame();
        }
        //ADDCARD
        else if(message.startsWith("<addcard>")) {
            addCard(new Card(mc.getBetweenTag(message, "addcard")[0]));
        }
        //REMOVECARD
        else if(message.startsWith("<removecard>")) {
            removeCard(new Card(mc.getBetweenTag(message, "removecard")[0]));
        }
    }
    
    private void startGame() {
        Game g = this;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                window.setScene(g);
            }
        });

        hand = new Hand();
        guiCards = new LinkedList<GuiCard>();

        hBoxHand = new HBox();
        hBoxHand.setSpacing(5);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                layout.getChildren().addAll(hBoxHand);
            }
        });
    }
                    
    private void addCard(Card card) {
        hand.addCard(card);
        guiCards.add(new GuiCard(card, this));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                hBoxHand.getChildren().add(guiCards.getLast());
            }
        });
    }
                    
    public void pickCard(Card card) {
        String msg = mc.tagged(mc.tagged(client.name, "name") + mc.tagged(card.toString(), "card"), "putcard");
        client.send(msg);
    }

    private void removeCard(Card card) {
        for(GuiCard guiCard : guiCards) {
            if(guiCard.getText().equals(card.toString())) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        hBoxHand.getChildren().remove(guiCard);
                    }
                });
                guiCards.remove(guiCard);
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        hand.removeCard(card);
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
    }

    public void close() {
        if(client.getId() != null) {
            String msg = mc.tagged(client.getId().toString(), "leave");
            client.send(msg);
        }
        client.close();
        running = false;
    }

}
