package de.petri.onu.server;

import de.petri.onu.game.GameState;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

    //Socket
    private DatagramSocket socket;
    private int port;

    //Constants
    private static final int PACKET_SIZE = 2048;
    private final int MAX_ATTEMPTS = 5;
    private boolean raw = false;

    //Thread
    private Thread run, manage, send, receive;
    private boolean running;

    //Console
    String[] help;

    //--GAME--\\
    GameState gs;


    //constructor
    public Server(int port) {
        gs = GameState.SETUP;
        System.out.println("Starte den Server...");

        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        help = loadHelp();

        run = new Thread(this, "Server");
        run.start();
    }

    @Override
    public void run() {
        running = true;
        System.out.println("Server läuft auf port " + port + "!");

        //starts Threads for managing all connected clients ie. checking for timeouts
        manageClients();

        //starts Thread for handling incoming data packets
        receive();

        Scanner s = new Scanner(System.in);
        while(running) {
            String input = s.nextLine();

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
                        System.out.println("RAW-Modus deaktiviert!");
                    } else {
                        raw = true;
                        System.out.println("RAW-Modus aktiviert!");
                    }
                    break;
                case "help":
                    printHelp();
                    break;
            }
        }

        System.out.println("Server ist beendet!");
    }

    private void manageClients() {
        manage = new Thread("Manage") {
            @Override
            public void run() {
                while(running) {
                    broadcast("/p/");

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (UUID id : removeClients) {
                        for(int i = 0; i < clients.size(); i++) {
                            if(clients.get(i).getID().equals(id)) {
                                clients.remove(i);
                                break;
                            }
                        }
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
        if(raw) System.out.println(data);
        if(data.startsWith("/j/")) {
            UUID id = UUID.randomUUID();
            String name = data.split("/j/|/e/")[1];

            clients.add(new ServerClient(id, name, packet.getAddress(), packet.getPort()));
            System.out.println(name + " hat die Verbindung aufgebaut!");

            String msg = "/c/" + id;
            send(msg, packet.getAddress(), packet.getPort());
        }
    }

    private void sendUpdate() {
        if (clients.size() <= 0) return;

    }

    //sends the message to all connected clients
    private void broadcast(String message) {
        for(ServerClient client : clients) {
            send(message, client.getAddress(), client.getPort());
        }
    }

    //sends a message to a specific address & port
    private void send(String message, InetAddress address, int port) {
        message += "/e/";
        send(message.getBytes(), address, port);
    }

    //sends a byte[] to a specific address & port
    private void send(final byte[] data, final InetAddress address, final int port) {
        send = new Thread("Send") {
            public void run() {
                DatagramPacket packet = new DatagramPacket(data,  data.length,address, port);

                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    //disconnects a client
    private void disconnect(UUID id, boolean status) {
        ServerClient c = null;
        boolean existed = false;
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getID() == id) {
                c = clients.get(i);
                clients.remove(i);
                existed = true;
                break;
            }
        }
        if (!existed) return;
        String message = "";
        if (status) {
            message = "Client " + c.getName() + " (" + c.getID() + ") @ " + c.getAddress().toString() + ":" + c.getPort() + " trennte die Verbindung!";
        } else {
            message = "Client " + c.getName() + " (" + c.getID() + ") @ " + c.getAddress().toString() + ":" + c.getPort() + " ist ausgetimed!";
        }
        System.out.println(message);
    }

    //prints all connected Clients
    private void printClients() {
        System.out.println("Clients:");
        for (ServerClient client : clients) {
            System.out.println("\t"+client.getName()+"("+client.ping+"ms)\tID="+client.getID());
        }
    }

    //prints help
    private void printHelp() {
        for (String s : help) {
            System.out.println(s);
        }
    }

    private String[] loadHelp() {
        // The name of the file to open.
        String fileName = "help.txt";

        ArrayList<String> help = new ArrayList<String>();

        try {
            FileReader fileReader = new FileReader(fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

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

    //stops the server
    private void stop() {
        System.out.println("Server fährt herunter...");

        gs = GameState.END;
        running = false;
        socket.close();
    }

}
