package de.petri.onu.server;

import de.petri.onu.game.Card;
import de.petri.onu.game.GameState;
import de.petri.onu.game.Pile;
import de.petri.onu.game.Value;
import de.petri.onu.helper.MessageConverter;
import de.petri.onu.helper.ResourceLoader;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class Server implements Runnable {

    //--SERVER--\\

    //Client
    private ArrayList<ServerClient> clients = new ArrayList<ServerClient>();
    private ArrayList<UUID> removeClients = new ArrayList<UUID>();
    private ArrayList<String> clientResponse = new ArrayList<String>();

    private long lastPing;

    private UUID admin;

    //Socket
    private DatagramSocket socket;
    private int port;

    //Helper
    MessageConverter mc;

    //Constants
    private static final int PACKET_SIZE = 2048;
    private final int MAX_ATTEMPTS = 5;

    private boolean raw = true;
    private boolean rawPing = false;

    //Thread
    private Thread run, manage, send, receive;
    private boolean running;

    //Console
    private String[] help;

    //--GAME--\\
    private GameState gs;
    
    private Pile[] piles;
    private int startAmount = 7;

    private int currentClient = 0;
    private int direction = 1;
    private final int maxPlayers = 8;

    //constructor
    public Server(int port) {
        gs = GameState.SETUP;
        System.out.println("Starting the server...");

        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            return;        }

        mc = new MessageConverter();

        help = loadHelp();

        run = new Thread(this, "Server");
        run.start();
    }

    @Override
    public void run() {
        running = true;
        System.out.println("Server is running on port " + port + "!");

        //starts Threads for managing all connected clients ie. checking for timeouts
        manageClients();

        //starts Thread for handling incoming data packets
        receive();

        //sets GameState to Lobby so players can join
        gs = GameState.LOBBY;
        System.out.println("ready");

        Scanner s = new Scanner(System.in);
        while(running) {
            String input = s.nextLine().toLowerCase();

            switch(input) {
                case "stop":
                    stop();
                    break;
                case "clients":
                    printClients();
                    break;
                case "raw":
                    if(raw) {
                        raw = false;
                        System.out.println("RAW-mode activated");
                    } else {
                        raw = true;
                        System.out.println("RAW-mode disabled!");
                    }
                    break;
                case "help":
                    printHelp();
                    break;
            }
        }

        System.out.println("Server is stopped!");
    }

    private void manageClients() {
        manage = new Thread("Manage") {
            @Override
            public void run() {
                while(running) {
                    broadcast(mc.tagged("", "ping"));
                    for (ServerClient client : clients) {
                        client.lastTime = System.currentTimeMillis();
                    }
                    lastPing = System.currentTimeMillis();

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (UUID id : removeClients) {
                        disconnect(id);
                    }

                    for (ServerClient client : clients) {
                        if(!clientResponse.contains(client.getID().toString())) {
                            if (client.attempts >= MAX_ATTEMPTS) {
                                removeClients.add(client.getID());
                            } else {
                                client.attempts++;
                            }
                        } else {
                            clientResponse.remove(client.getID().toString());
                            client.attempts = 0;
                        }
                    }
                }
            }
        };
        manage.start();
    }

    private void receive() {
        receive = new Thread("Receive") {
            @Override
            public void run() {
                while(running) {
                    byte[] data = new byte[PACKET_SIZE];
                    DatagramPacket packet = new DatagramPacket(data, data.length);

                    try {
                        socket.receive(packet);
                    } catch (SocketException e) {
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(running) process(packet);
                }
            }
        };
        receive.start();
    }

    //processes a received packet
    private void process(DatagramPacket packet) {
        String data = new String(packet.getData());
        if(raw) {
            if(data.startsWith("<ping>")) {
                if(rawPing) System.out.println(data);
            } else {
                System.out.println(data);
            }
        }

        //JOIN
        if(data.startsWith("<join>")) {
            if(clients.size() >= maxPlayers) {
                send(mc.tagged("error","join"), packet.getAddress(), packet.getPort());
            } else if(gs == GameState.LOBBY) {
                UUID id = UUID.randomUUID();
                String name = mc.getBetweenTag(mc.getBetweenTag(data, "join")[0], "name")[0];

                //checks & updates playername if already existing
                String newName = name;
                for (int i = 2; i <= clients.size() + 1; i++) {
                    boolean contains = false;
                    for (ServerClient client : clients) {
                        if (newName.equals(client.getName())) {
                            contains = true;
                        }
                    }
                    if(!contains) break;

                    newName = name + i;
                }
                name = newName;

                clients.add(new ServerClient(id, name, packet.getAddress(), packet.getPort()));
                System.out.println(name + " has connected to the server!");

                //sends client specific data
                String msg = mc.tagged(mc.tagged(name, "name") + mc.tagged(id.toString() , "id") + getClientsNameTagged(),"join");
                send(msg, packet.getAddress(), packet.getPort());

                //sends the information to all clients
                msg = mc.tagged(mc.tagged(name, "name"), "add");
                broadcast(msg);

                if(clients.size() <= 1) {
                    setAdmin(id);
                }
            } else {
                String msg = mc.tagged(mc.tagged("", "error"), "join");
                send(msg, packet.getAddress(), packet.getPort());
            }
        }
        //PING
        else if(data.startsWith("<ping>")) {
            String id = mc.getBetweenTag(mc.getBetweenTag(data, "ping")[0], "id")[0];
            for (ServerClient client : clients) {
                if(client.getID().equals(id)) {
                    client.ping = (int) (System.currentTimeMillis() - lastPing);
                }
            }
            clientResponse.add(id);
        }
        //LEAVE
        else if(data.startsWith("<leave>")) {
            //gets the id
            UUID id = UUID.fromString(mc.getBetweenTag(data, "leave")[0]);

            //gets name from player with right id
            String name = "";
            for (ServerClient client : clients) {
                if(client.getID().equals(id)) name = client.getName();
            }

            //disconnects the player
            disconnect(id);

            //sets Admin
            if(clients.size() > 0) {
                if (id.equals(admin)) {
                    setAdmin(clients.get(0).getID());
                }

                //sends the leave command to all players
                broadcast(mc.tagged(mc.tagged(name, "name"), "leave"));
            }
        }
        //START
        else if(data.startsWith("<start>")) {
            startGame();
        }
        //PUTCARD
        else if(data.startsWith("<putcard>")) {
            String name = mc.getBetweenTag(mc.getBetweenTag(data, "putcard")[0],"name")[0];
            String cardText = mc.getBetweenTag(mc.getBetweenTag(data, "putcard")[0],"card")[0];

            if(name.equals(clients.get(currentClient).getName())) {
                ServerClient client = getClient(name);
                Card card = new Card(cardText);
                if(putCard(client, card)) {
                    if(card.getValue() == Value.COLOR) {
                        send(mc.tagged("nc", "removecard"), packet.getAddress(), packet.getPort());
                    } else {
                        send(mc.tagged(cardText, "removecard"), packet.getAddress(), packet.getPort());
                    }
                    sendCountUpdate(client);
                }
            }
        }
        //DRAW
        else if(data.startsWith("<drawcard>")) {
            String name = mc.getBetweenTag(mc.getBetweenTag(data, "drawcard")[0],"name")[0];

            if(name.equals(clients.get(currentClient).getName())) {
                ServerClient client = getClient(name);
                drawCard(client);
                sendCountUpdate(client);
                plusTurn(true);
            }
        }
        //if data doesn't fit in the protocol
        else {
            System.out.println("ERROR: " + data);
        }
    }

    private void startGame() {
        gs = GameState.GAME;

        broadcast(mc.tagged("", "start"));
        
        piles = new Pile[2];

        //loads the drawing stack
        InputStream cardsFile = ResourceLoader.load("game/cards.txt");
        piles[0] = Pile.loadFromText(cardsFile);
        piles[0].shuffle();

        //adds 7 cards to all players
        for(ServerClient client : clients) {
            for(int i = 0; i < startAmount; i++) {
                client.getHand().addCard(piles[0].pop());
            }
        }

        for(ServerClient client : clients) {
            Card[] cards = client.getHand().getCards();
            for(Card card : cards) {
                sendCard(client, card);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sendCountUpdate(client);
        }

        //creates the put stack
        piles[1] = new Pile();
        do {
            if(piles[0].size() <= 0) {
                piles[0] = piles[1];
                piles[0].shuffle();
                piles[1] = new Pile();
            }
            piles[1].push(piles[0].pop());
        } while(piles[1].peek().toString().equals("nc"));

        broadcast(mc.tagged(piles[1].peek().toString(), "putcard"));
        broadcast(mc.tagged(clients.get(currentClient).getName(), "turn"));
    }
    
    private void sendCard(ServerClient client, Card card) {
        String msg = mc.tagged(card.toString(), "addcard");
        send(msg, client.getAddress(), client.getPort());
    }

    private boolean putCard(ServerClient client, Card card) {
        if(piles[1].isAcceptable(card)) {
            if(card.getValue() == Value.COLOR) {
                client.getHand().removeCard(new Card("nc"));
                piles[1].push(card);
                broadcast(mc.tagged(card.toString(), "putcard"));
            } else {
                piles[1].push(client.getHand().removeCard(card));
                broadcast(mc.tagged(card.toString(), "putcard"));
                if(card.getValue() == Value.DRAW) {
                    drawCard(getNextClient());
                    drawCard(getNextClient());
                    sendCountUpdate(getNextClient());
                }
            }
            if(client.getHand().getCards().length <= 0) {
                endGame(client);
            }
            nextTurn(card);
            return true;
        } else {
            return false;
        }
    }

    private void endGame(ServerClient winner) {
        broadcast(mc.tagged(winner.getName(),"winner"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stop();
    }

    private void drawCard(ServerClient client) {
        if(piles[0].size() <= 0) {
            Card card = piles[1].pop();
            piles[0] = piles[1];
            piles[0].shuffle();
            piles[1] = new Pile();
            piles[1].push(card);
       }
       Card card = piles[0].pop();
       client.getHand().addCard(card);
       send(mc.tagged(card.toString(), "addcard"), client.getAddress(), client.getPort());
    }

    private void sendCountUpdate(ServerClient client) {
        broadcast(mc.tagged(mc.tagged(client.getName(), "name") + mc.tagged(String.valueOf(client.getHand().getCount()), "count"), "countupdate"));
    }

    private ServerClient getNextClient() {
        ServerClient client = null;
        int i = currentClient + direction;
        if(i < 0) {
            i = clients.size() - 1;
        } else if(i > clients.size() - 1) {
            i = 0;
        }
        return clients.get(i);
    }

    public ServerClient getClient(String name) {
        for (ServerClient client : clients) {
            if(client.getName().equals(name)) {
                return client;
            }
        }
        return null;
    }

    private String getClientsNameTagged() {
        String names = "";
        for (ServerClient client : clients) {
            names += mc.tagged(client.getName(), "cName");
        }
        return names;
    }

    private String getClientsPingTagged() {
        String pings = "";
        for (ServerClient client : clients) {
            pings += mc.tagged(String.valueOf(client.ping), "ping");
        }
        return pings;
    }

    //sets a client to Admin
    private void setAdmin(UUID clientID) {
        ServerClient newAdmin = null;
        for (ServerClient client : clients) {
            if(client.getID().equals(clientID)) {
                newAdmin = client;
                break;
            }
        }

        broadcast(mc.tagged(mc.tagged(newAdmin.getName(), "name"), "admin"));
        admin = newAdmin.getID();
    }

    //sends the message to all connected clients
    private void broadcast(String message) {
        for(ServerClient client : clients) {
            send(message, client.getAddress(), client.getPort());
        }
    }

    private void nextTurn(Card card) {
        if(card.getValue() == Value.SWITCH)
            direction *= -1;

        if(card.getValue() == Value.BLOCK)
            plusTurn(false);

        plusTurn(true);
    }

    private void plusTurn(boolean send) {
        currentClient += direction;
        if(currentClient < 0) {
            currentClient = clients.size() - 1;
        } else if(currentClient > clients.size() - 1) {
            currentClient = 0;
        }
        if(send) {
            broadcast(mc.tagged(clients.get(currentClient).getName(), "turn"));
        }
    }

    //sends a message to a specific address & port
    private void send(String message, InetAddress address, int port) {
        send(message.getBytes(), address, port);
    }

    public void send(final byte[] data, InetAddress address, int port) {
        send = new Thread("Send") {
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    //disconnects a client
    private void disconnect(UUID id) {
        ServerClient c = null;
        boolean existed = false;
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getID().equals(id)) {
                c = clients.get(i);
                clients.remove(i);
                existed = true;
                break;
            }
        }
        if (!existed) return;
        String message = "";
        if (c.attempts < MAX_ATTEMPTS) {
            message = "Client " + c.getName() + " (" + c.getID() + ") @ " + c.getAddress().toString() + ":" + c.getPort() + " closed the connection!";
        } else {
            message = "Client " + c.getName() + " (" + c.getID() + ") @ " + c.getAddress().toString() + ":" + c.getPort() + " timed out!";
        }
        System.out.println(message);
        
        if(clients.size() <= 0) {
            stop();
        }
    }

    //prints all connected Clients
    private void printClients() {
        clearScreen();
        System.out.println("Clients:");
        for (ServerClient client : clients) {
            System.out.println("\t"+client.getName()+"("+client.ping+"ms)\tID="+client.getID());
        }
    }

    //prints help
    private void printHelp() {
        clearScreen();
        for (String s : help) {
            System.out.println(s);
        }
    }

    private String[] loadHelp() {
        ArrayList<String> help = new ArrayList<String>();

        InputStream is = ResourceLoader.load("server/help.txt");

        try {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                help.add(line);
            }

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] helpArr = new String[help.size()];
        for (int i = 0; i < helpArr.length; i++) {
            helpArr[i] = help.get(i);
        }

        return helpArr;
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    //stops the server
    private void stop() {
        System.out.println("Server is shutting down...");

        gs = GameState.END;
        running = false;
        socket.close();
        System.out.println("stopped");
    }

}
