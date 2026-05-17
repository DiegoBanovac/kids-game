package hr.adhd.igrica.screens;

import hr.adhd.igrica.util.GameType;
import hr.adhd.igrica.util.SceneManager;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class EndScreen {

    private final int starsEarned;
    private final int totalStars;
    private final GameType lastGame;

    public EndScreen(int starsEarned, int totalStars, GameType lastGame) {
        this.starsEarned = starsEarned;
        this.totalStars  = totalStars;
        this.lastGame    = lastGame;
    }

    public Parent buildLayout() {
        Label bravoLabel = new Label("Bravo!");
        bravoLabel.getStyleClass().add("end-title");

        Label earnedLabel = new Label("Zaradio/la si  " + starsEarned + " ★");
        earnedLabel.getStyleClass().add("end-earned");

        HBox starRow = buildStarRow(starsEarned);

        Label totalLabel = new Label("Ukupno zvjezdica:  " + totalStars + " ★");
        totalLabel.getStyleClass().add("end-total");

        Button replayBtn = new Button("►  Igraj opet");
        replayBtn.getStyleClass().add("replay-button");
        replayBtn.setOnAction(e -> SceneManager.showGame(lastGame));

        Button menuBtn = new Button("Odaberi igru");
        menuBtn.getStyleClass().add("menu-button");
        menuBtn.setOnAction(e -> SceneManager.showStartScreen());

        HBox buttons = new HBox(24, replayBtn, menuBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox panel = new VBox(22, bravoLabel, earnedLabel, starRow, totalLabel, buttons);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("end-panel");
        panel.setMaxWidth(540);
        panel.setPadding(new Insets(50, 60, 50, 60));

        StackPane root = new StackPane(panel);
        root.getStyleClass().add("root-bg");
        return root;
    }

    private HBox buildStarRow(int count) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        SequentialTransition seq = new SequentialTransition();

        for (int i = 0; i < Math.max(count, 1); i++) {
            Label star = new Label("★");
            star.getStyleClass().add("end-star");
            star.setScaleX(0);
            star.setScaleY(0);
            row.getChildren().add(star);

            ScaleTransition pop = new ScaleTransition(Duration.millis(200), star);
            pop.setFromX(0); pop.setFromY(0);
            pop.setToX(1.0); pop.setToY(1.0);
            seq.getChildren().add(pop);
        }
        seq.play();
        return row;
    }
}
