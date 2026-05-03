package hr.adhd.igrica.screens;

import hr.adhd.igrica.util.GameType;
import hr.adhd.igrica.util.SceneManager;
import hr.adhd.igrica.util.TtsManager;
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
        Label bravoLabel = new Label("Bravo! 🎉");
        bravoLabel.getStyleClass().add("end-title");

        Label earnedLabel = new Label("Zaradio/la si  " + starsEarned + " ⭐");
        earnedLabel.setStyle("-fx-font-size: 34px; -fx-text-fill: #333; -fx-font-weight: bold;");

        // animated star row
        HBox starRow = buildStarRow(starsEarned);

        Label totalLabel = new Label("Ukupno zvjezdica:  " + totalStars + " ⭐");
        totalLabel.setStyle("-fx-font-size: 26px; -fx-text-fill: #555;");

        Button replayBtn = new Button("▶  Igraj opet");
        replayBtn.getStyleClass().add("replay-button");
        replayBtn.setOnAction(e -> {
            TtsManager.speak("Igramo opet!");
            SceneManager.showGame(lastGame);
        });

        Button menuBtn = new Button("🏠  Odaberi igru");
        menuBtn.getStyleClass().add("menu-button");
        menuBtn.setOnAction(e -> SceneManager.showStartScreen());

        HBox buttons = new HBox(24, replayBtn, menuBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox content = new VBox(20, bravoLabel, earnedLabel, starRow, totalLabel, buttons);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));

        StackPane root = new StackPane(content);
        root.setStyle("-fx-background-color: #FDFAF0;");

        TtsManager.speak("Bravo! Zaradio si " + starsEarned + " zvjezdica!");
        return root;
    }

    private HBox buildStarRow(int count) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);
        SequentialTransition seq = new SequentialTransition();

        for (int i = 0; i < Math.max(count, 1); i++) {
            Label star = new Label("⭐");
            star.setStyle("-fx-font-size: 52px;");
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
