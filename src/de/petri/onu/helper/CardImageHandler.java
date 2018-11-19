package de.petri.onu.helper;

import de.petri.onu.game.Card;
import javafx.scene.image.Image;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CardImageHandler {

    private final HashMap<String, Image> cardImages, faceImages;

    public CardImageHandler() {
        cardImages = new HashMap<String, Image>();
        faceImages = new HashMap<String, Image>();

        loadImages();
        loadSymbols();
    }

    private void loadImages() {
        Image img = null;

        ArrayList<String> cards = new ArrayList<String>();
        InputStream is = ResourceLoader.load("gfx/cards/config.txt");

        try {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                cards.add(line);
            }

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String l : cards) {
            String[] s = l.split(" ");

            img = new Image("gfx/cards/" + s[1]);

            cardImages.put(s[0], img);
        }
    }

    private void loadSymbols() {
        Image img = null;

        ArrayList<String> faces = new ArrayList<String>();
        InputStream is = ResourceLoader.load("gfx/faces/config.txt");

        try {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                faces.add(line);
            }

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String l : faces) {
            String[] s = l.split(" ");

            img = new Image("gfx/faces/" + s[1]);

            faceImages.put(s[0], img);
        }
    }

    public Image getCardImage(String text) {

        if (text.startsWith("nn")) {
            return cardImages.get("n");
        } else if (text.endsWith("c")) {
            return cardImages.get(text);
        } else if (text.startsWith("r")) {
            return cardImages.get("r");
        } else if (text.startsWith("b")) {
            return cardImages.get("b");
        } else if (text.startsWith("g")) {
            return cardImages.get("g");
        } else if (text.startsWith("y")) {
            return cardImages.get("y");
        }

        return null;
    }

    public Image getFaceImage(String text) {
        return faceImages.get(text.substring(text.length() - 1));
    }

}
