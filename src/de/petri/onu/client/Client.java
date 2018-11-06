package de.petri.onu.client;

import java.io.IOException;
import java.net.*;
import java.util.UUID;

public class Client {

    //CLIENT\\

    //Socket
    private DatagramSocket socket;

    public String name;
    private String address;
    private int port = 9000;

    private InetAddress ip;

    private final int PACKET_SIZE = 2048;

    //Thread
    private Thread send;

    //ID
    private UUID id;

    //constructor
    public Client(String name, String address) {
        this.name = name;
        this.address = address;
    }

    //opens a connection with the Server
    public boolean openConnection() {
        try {
            socket = new DatagramSocket();
            ip = InetAddress.getByName(address);
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String receive() {
        byte[] data = new byte[PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String message = new String(packet.getData());
        return message;
    }

    public void send(final byte[] data) {
        send = new Thread("Send") {
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    public void send(String message) {
        send(message.getBytes());
    }

    public void close() {
        new Thread() {
            public void run() {
                synchronized (socket) {
                    socket.close();
                }
            }
        }.start();
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
