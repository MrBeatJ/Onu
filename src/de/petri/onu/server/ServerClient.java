package de.petri.onu.server;

import de.petri.onu.game.Hand;

import java.net.InetAddress;
import java.util.UUID;

public class ServerClient {

    //Server
    final private UUID ID;
    private String name;
    private InetAddress address;
    private int port;

    public int ping;
    public long lastTime;
    public int attempts = 0;

    //Game
    private Hand hand;

    public ServerClient(final UUID ID, String name, InetAddress address, int port) {
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.port = port;

        lastTime = System.currentTimeMillis();

        hand = new Hand();
    }

    public UUID getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
    
    publiv Hand getHand() {
        return hand;   
    }

}
