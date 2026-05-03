package hr.adhd.igrica.games;

import hr.adhd.igrica.util.AudioManager;
import hr.adhd.igrica.util.GameSession;
import hr.adhd.igrica.util.GameType;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColorSequenceGame extends BaseGame {

    private static final Color[] NORMAL = {
        Color.web("#E53935"),  // red
        Color.web("#1E88E5"),  // blue
        Color.web("#FDD835"),  // yellow
        Color.web("#43A047")   // green
    };
    private static final Color[] BRIGHT = {
        Color.web("#FF8A80"),
        Color.web("#82B1FF"),
        Color.web("#FFFF8D"),
        Color.web("#B9F6CA")
    };

    private final List<Integer> sequence = new ArrayList<>();
    private final Circle[] circles = new Circle[4];
    private final StackPane[] circlePanes = new StackPane[4];
    private final Random rng = new Random();

    private int playerIndex = 0;
    private boolean playerTurn = false;
    private int roundsDone = 0;
    private static final int TOTAL_ROUNDS = 5;

    private Label statusLabel;
    private Label roundLabel;

    public ColorSequenceGame(GameSession session, AudioManager audioManager) {
        super(session, audioManager);
    }

    @Override public String getGameTitle()     { return "🌈 Ponavljanje boja"; }
    @Override protected GameType getGameType() { return GameType.COLOR_SEQUENCE; }

    @Override
    public Parent buildLayout() {
        roundLabel  = new Label("Runda 1 / " + TOTAL_ROUNDS);
        roundLabel.getStyleClass().add("round-label");

        statusLabel = new Label("Pripremi se...");
        statusLabel.getStyleClass().add("status-label");

        GridPane grid = new GridPane();
        grid.setHgap(36);
        grid.setVgap(36);
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < 4; i++) {
            circles[i] = new Circle(78, NORMAL[i]);
            circles[i].setEffect(new DropShadow(10, Color.gray(0.35)));
            final int idx = i;
            StackPane pane = new StackPane(circles[i]);
            pane.setCursor(javafx.scene.Cursor.HAND);
            pane.setOnMouseClicked(e -> { if (playerTurn) handleClick(idx); });
            circlePanes[idx] = pane;
            grid.add(pane, i % 2, i / 2);
        }

        VBox content = new VBox(22, roundLabel, grid, statusLabel);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(16));

        BorderPane root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(content);
        return root;
    }

    @Override
    public void startGame() {
        sequence.add(rng.nextInt(4));
        sequence.add(rng.nextInt(4));
        PauseTransition delay = new PauseTransition(Duration.millis(700));
        delay.setOnFinished(e -> playSequence());
        delay.play();
    }

    @Override
    public void resetGame() {
        sequence.clear();
        playerIndex = 0;
        playerTurn  = false;
        roundsDone  = 0;
    }

    private void playSequence() {
        playerTurn = false;
        roundLabel.setText("Runda " + (roundsDone + 1) + " / " + TOTAL_ROUNDS);
        statusLabel.setText("👀 Gledaj!");
        playNextFlash(0);
    }

    private void playNextFlash(int idx) {
        if (idx >= sequence.size()) {
            playerTurn  = true;
            playerIndex = 0;
            statusLabel.setText("👆 Tvoj red! Ponovi niz!");
            return;
        }
        int ci = sequence.get(idx);
        circles[ci].setFill(BRIGHT[ci]);
        audioManager.playClick();

        PauseTransition hold = new PauseTransition(Duration.millis(520));
        hold.setOnFinished(e -> {
            circles[ci].setFill(NORMAL[ci]);
            PauseTransition gap = new PauseTransition(Duration.millis(280));
            gap.setOnFinished(e2 -> playNextFlash(idx + 1));
            gap.play();
        });
        hold.play();
    }

    private void handleClick(int ci) {
        // visual pulse
        ScaleTransition pulse = new ScaleTransition(Duration.millis(100), circlePanes[ci]);
        pulse.setFromX(1.0); pulse.setFromY(1.0);
        pulse.setToX(1.18); pulse.setToY(1.18);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(2);
        pulse.play();

        if (ci == sequence.get(playerIndex)) {
            playerIndex++;
            if (playerIndex == sequence.size()) {
                // round clear
                playerTurn = false;
                roundsDone++;
                starSystem.addStars(1);
                statusLabel.setText("✅ Točno! Bravo! ⭐");

                if (roundsDone == TOTAL_ROUNDS) {
                    PauseTransition end = new PauseTransition(Duration.millis(900));
                    end.setOnFinished(e -> finishGame());
                    end.play();
                } else {
                    sequence.add(rng.nextInt(4));
                    PauseTransition next = new PauseTransition(Duration.millis(1100));
                    next.setOnFinished(e -> playSequence());
                    next.play();
                }
            }
        } else {
            // wrong input
            playerTurn = false;
            audioManager.playWrong();
            statusLabel.setText("❌ Netočno! Gledaj ponovo...");
            for (Circle c : circles) c.setFill(Color.web("#FFCDD2"));

            PauseTransition retry = new PauseTransition(Duration.millis(1000));
            retry.setOnFinished(e -> {
                for (int i = 0; i < circles.length; i++) circles[i].setFill(NORMAL[i]);
                playSequence();
            });
            retry.play();
        }
    }

    private void finishGame() {
        int earned = starSystem.getStars();
        int stars = earned >= 5 ? 3 : earned >= 3 ? 2 : 1;
        completeGame(stars);
    }
}
