package de.petri.onu.client;

import de.petri.onu.game.Card;
import de.petri.onu.game.Color;
import de.petri.onu.game.Hand;
import de.petri.onu.game.Value;
import de.petri.onu.helper.CardImageHandler;
import de.petri.onu.helper.MessageConverter;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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

    private final BorderPane layout;
    private HBox topLayout;

    ScrollPane scrollPaneHand;
    HBox hBoxHand;

    //Game
    Hand hand;
    ArrayList<GuiCard> guiCards;
    Sideboard sideboard;
    GuiStacks guiStacks;
    static CardImageHandler cardImageHandler;
    ArrayList<GamePlayer> players = new ArrayList<GamePlayer>();

    public Game(Stage window, String name, String address) {
        super(new StackPane(), Main.WIDTH, Main.HEIGHT);
        this.window = window;

        getStylesheets().add("gfx/css/style.css");

        cardImageHandler = new CardImageHandler();

        layout = new BorderPane();
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
            if(message.startsWith("<join>error</join>")) {
                close();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        window.setScene(Main.getMenu());
                    }
                });
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
        //COUNTUPDATE
        else if(message.startsWith("<countupdate>")) {
            String name = mc.getBetweenTag(mc.getBetweenTag(message, "countupdate")[0], "name")[0];
            int count = Integer.parseInt(mc.getBetweenTag(mc.getBetweenTag(message, "countupdate")[0], "count")[0]);

            sideboard.updateCount(name, count);
        }
        //PUTCARD
        else if(message.startsWith("<putcard>")) {
            String card = mc.getBetweenTag(message, "putcard")[0];
            guiStacks.setPutStack(new Card(card));
        }
        //TURN
        else if(message.startsWith("<turn>")) {
            String name = mc.getBetweenTag(message, "turn")[0];
            if(client.name.equals(name)) {
                setActive(true);
            } else {
                setActive(false);
            }
        }
        //WINNER
        else if(message.startsWith("<winner>")) {
            String winner = mc.getBetweenTag(message, "winner")[0];

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    WinnerDialog.display(winner, window, hand);
                }
            });

            close();
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
        guiCards = new ArrayList<GuiCard>();

        sideboard = new Sideboard(players);
        sideboard.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(sideboard, Pos.CENTER);

        guiStacks = new GuiStacks(this);
        guiStacks.setAlignment(Pos.BOTTOM_LEFT);
        BorderPane.setAlignment(guiStacks, Pos.BOTTOM_LEFT);
        BorderPane.setMargin(guiStacks, new Insets(110,0,0,80));

        hBoxHand = new HBox();
        hBoxHand.setSpacing(5);
        hBoxHand.setStyle("-fx-background-color: #aaaaaa");
        hBoxHand.setAlignment(Pos.BOTTOM_CENTER);
        BorderPane.setAlignment(hBoxHand, Pos.BOTTOM_CENTER);

        scrollPaneHand = new ScrollPane();
        scrollPaneHand.setStyle("-fx-background-color: #aaaaaa");
        scrollPaneHand.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPaneHand.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneHand.setContent(hBoxHand);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                layout.setCenter(guiStacks);
                layout.setLeft(sideboard);
                layout.setBottom(scrollPaneHand);
            }
        });
    }

    private synchronized void addCard(Card card) {
        hand.addCard(card);
        GuiCard guiCard = new GuiCard(card);
        guiCard.setOnAction(e -> {
            pickCard(card);
        });
        guiCards.add(guiCard);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                hBoxHand.getChildren().addAll(guiCard);
            }
        });
    }

    private void setActive(boolean active) {
        if(active) {
            for (GuiCard guiCard : guiCards) {
                guiCard.setActive(true);
            }
            guiStacks.setActive(true);
        } else {
            for (GuiCard guiCard : guiCards) {
                guiCard.setActive(false);
            }
            guiStacks.setActive(false);
        }
    }
                    
    public void pickCard(Card card) {
        if(card.getValue() == Value.COLOR) {
            Color color = ColorDialog.display();
            card.setColor(color);
        }
        String msg = mc.tagged(mc.tagged(client.name, "name") + mc.tagged(card.toString(), "card"), "putcard");
        client.send(msg);
    }

    public void drawCard() {
        String msg = mc.tagged(mc.tagged(client.name, "name"), "drawcard");
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

        GamePlayer remove = null;
        for (GamePlayer player : players) {
            if(player.getName().equals(name)) remove = player;
        }
        players.remove(remove);
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
