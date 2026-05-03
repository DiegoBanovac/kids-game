package hr.adhd.igrica.util;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class StarSystem {

    private int currentStars = 0;
    private final HBox widget = new HBox(6);
    private final Label countLabel = new Label("0 ⭐");

    public StarSystem() {
        countLabel.setStyle("-fx-font-size: 26px; -fx-text-fill: #F5A623; -fx-font-weight: bold;");
        widget.getChildren().add(countLabel);
        widget.setStyle("-fx-alignment: center-left;");
    }

    public HBox getWidget() {
        return widget;
    }

    public void reset() {
        currentStars = 0;
        countLabel.setText("0 ⭐");
    }

    public void addStars(int count) {
        currentStars += count;
        countLabel.setText(currentStars + " ⭐");
        ScaleTransition pop = new ScaleTransition(Duration.millis(200), countLabel);
        pop.setFromX(1.0);
        pop.setFromY(1.0);
        pop.setToX(1.4);
        pop.setToY(1.4);
        pop.setAutoReverse(true);
        pop.setCycleCount(2);
        pop.play();
    }

    public int getStars() {
        return currentStars;
    }
}
