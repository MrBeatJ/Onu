package de.petri.onu.client;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        Client c = new Client("Max", "localhost");
        c.openConnection("localhost");

        String connection = "/j/" + "Max" + "/e/";
        c.send(connection.getBytes());
    }

}
