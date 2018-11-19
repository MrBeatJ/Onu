package de.petri.onu.client;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class GamePlayer {
    private String name;
    private int count = 0;

    private Label lblName, lblCount;

    public GamePlayer(String name) {
        this.name = name;

        lblName = new Label(name);
        lblCount = new Label(String.valueOf(count));
    }

    public GamePlayer(String name, int count) {
        this.name = name;
        this.count = count;

        lblName = new Label(name);
        lblCount = new Label(String.valueOf(count));
    }


    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void updateCount(int count) {
        this.count = count;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lblCount.setText(String.valueOf(count));
            }
        });
    }

    public Label getLblName() {
        return lblName;
    }

    public Label getLblCount() {
        return lblCount;
    }

    public void setHeight(int height) {
        lblName.setMinHeight(height);
        lblCount.setMinHeight(height);
        lblName.setMaxHeight(height);
        lblCount.setMaxHeight(height);
    }

}
