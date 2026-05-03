package hr.adhd.igrica.games;

import hr.adhd.igrica.util.AudioManager;
import hr.adhd.igrica.util.GameSession;
import hr.adhd.igrica.util.GameType;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReactionGame extends BaseGame {

    private static final int ROUNDS = 10;
    private static final long TIMEOUT_MS = 3000;

    private final List<Long> reactionTimes = new ArrayList<>();
    private final Random rng = new Random();

    private int currentRound = 0;
    private long roundStart;
    private PauseTransition timeout;

    private Pane gamePane;
    private Label starLabel;
    private Label roundLabel;
    private Label feedbackLabel;

    public ReactionGame(GameSession session, AudioManager audioManager) {
        super(session, audioManager);
    }

    @Override public String getGameTitle()     { return "⭐ Klikni zvjezdicu"; }
    @Override protected GameType getGameType() { return GameType.REACTION; }

    @Override
    public Parent buildLayout() {
        roundLabel = new Label("Runda 1 / " + ROUNDS);
        roundLabel.getStyleClass().add("round-label");

        feedbackLabel = new Label("Klikni zvjezdicu što brže možeš!");
        feedbackLabel.getStyleClass().add("status-label");

        starLabel = new Label("⭐");
        starLabel.setStyle("-fx-font-size: 88px; -fx-cursor: hand;");
        starLabel.setVisible(false);
        starLabel.setOnMouseClicked(e -> handleClick());

        gamePane = new Pane(starLabel);
        gamePane.setPrefSize(860, 480);
        gamePane.setStyle("-fx-background-color: #FDFAF0; -fx-background-radius: 12px;");

        VBox top = new VBox(6, roundLabel, feedbackLabel);
        top.setAlignment(Pos.CENTER);

        VBox content = new VBox(14, top, gamePane);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(12));

        BorderPane root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(content);
        return root;
    }

    @Override
    public void startGame() {
        PauseTransition delay = new PauseTransition(Duration.millis(600));
        delay.setOnFinished(e -> nextRound());
        delay.play();
    }

    @Override
    public void resetGame() {
        reactionTimes.clear();
        currentRound = 0;
        if (timeout != null) timeout.stop();
    }

    private void nextRound() {
        if (currentRound >= ROUNDS) {
            finishGame();
            return;
        }
        currentRound++;
        roundLabel.setText("Runda " + currentRound + " / " + ROUNDS);
        feedbackLabel.setText("Spremi se...");
        starLabel.setVisible(false);

        // random delay 800-2200ms before showing star
        int delay = 800 + rng.nextInt(1400);
        PauseTransition pre = new PauseTransition(Duration.millis(delay));
        pre.setOnFinished(e -> showStar());
        pre.play();
    }

    private void showStar() {
        double x = 40 + rng.nextDouble() * (gamePane.getPrefWidth()  - 120);
        double y = 20 + rng.nextDouble() * (gamePane.getPrefHeight() - 110);
        starLabel.setLayoutX(x);
        starLabel.setLayoutY(y);
        starLabel.setVisible(true);

        FadeTransition appear = new FadeTransition(Duration.millis(150), starLabel);
        appear.setFromValue(0);
        appear.setToValue(1);
        appear.play();

        feedbackLabel.setText("Klikni! ⭐");
        roundStart = System.currentTimeMillis();

        timeout = new PauseTransition(Duration.millis(TIMEOUT_MS));
        timeout.setOnFinished(e -> handleMiss());
        timeout.play();
    }

    private void handleClick() {
        if (!starLabel.isVisible()) return;
        timeout.stop();

        long rt = System.currentTimeMillis() - roundStart;
        reactionTimes.add(rt);

        ScaleTransition pop = new ScaleTransition(Duration.millis(130), starLabel);
        pop.setFromX(1.0); pop.setFromY(1.0);
        pop.setToX(1.5); pop.setToY(1.5);
        pop.setAutoReverse(true);
        pop.setCycleCount(2);
        pop.play();

        audioManager.playCorrect();
        starSystem.addStars(1);
        feedbackLabel.setText("✅ " + rt + " ms! Bravo!");

        PauseTransition gap = new PauseTransition(Duration.millis(600));
        gap.setOnFinished(e -> {
            starLabel.setVisible(false);
            nextRound();
        });
        gap.play();
    }

    private void handleMiss() {
        reactionTimes.add(TIMEOUT_MS);
        starLabel.setVisible(false);
        audioManager.playWrong();
        feedbackLabel.setText("⏱ Presporo! Pokušaj brže!");

        PauseTransition gap = new PauseTransition(Duration.millis(800));
        gap.setOnFinished(e -> nextRound());
        gap.play();
    }

    private void finishGame() {
        long avg = reactionTimes.stream().mapToLong(Long::longValue).sum() / reactionTimes.size();
        int stars;
        if      (avg <=  700) stars = 3;
        else if (avg <= 1200) stars = 2;
        else                  stars = 1;
        completeGame(stars);
    }
}
