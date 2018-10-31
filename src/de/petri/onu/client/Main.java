package de.petri.onu.client;

import de.petri.onu.client.gui.GUI;

public class Main {

    public static void main(String[] args) {

        GUI gui = new GUI();
        gui.setVisible(true);

        Client c = new Client("Max", "localhost");
        c.openConnection("localhost");

        String connection = "/j/" + "Max" + "/e/";
        c.send(connection.getBytes());
    }

}
