package hr.adhd.igrica.games;

import hr.adhd.igrica.util.AudioManager;
import hr.adhd.igrica.util.GameSession;
import hr.adhd.igrica.util.GameType;
import hr.adhd.igrica.util.SceneManager;
import hr.adhd.igrica.util.StarSystem;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseGame {

    protected final GameSession session;
    protected final AudioManager audioManager;
    protected final StarSystem starSystem;

    private Runnable onGameComplete;
    private int lastStarsEarned;

    private final List<PauseTransition> timers = new ArrayList<>();
    protected boolean cancelled = false;

    protected BaseGame(GameSession session, AudioManager audioManager) {
        this.session = session;
        this.audioManager = audioManager;
        this.starSystem = new StarSystem();
    }

    public abstract Parent buildLayout();
    public abstract void startGame();
    public abstract void resetGame();
    public abstract String getGameTitle();
    protected abstract GameType getGameType();

    public void setOnGameComplete(Runnable callback) {
        this.onGameComplete = callback;
    }

    public int getLastStarsEarned() {
        return lastStarsEarned;
    }

    /** Creates a tracked PauseTransition. Its action is skipped if the game was cancelled. */
    protected PauseTransition delay(Duration d, Runnable action) {
        PauseTransition pt = new PauseTransition(d);
        pt.setOnFinished(e -> { if (!cancelled) action.run(); });
        timers.add(pt);
        return pt;
    }

    /** Stops all pending timers and marks the game inactive. */
    public void cancel() {
        cancelled = true;
        timers.forEach(PauseTransition::stop);
        timers.clear();
    }

    protected HBox buildHeader() {
        Label title = new Label(getGameTitle());
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button homeBtn = new Button("< Izbornik");
        homeBtn.getStyleClass().add("home-button");
        homeBtn.setOnAction(e -> {
            cancel();
            SceneManager.showStartScreen();
        });

        HBox header = new HBox(16, title, spacer, starSystem.getWidget(), homeBtn);
        header.getStyleClass().add("header-bar");
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    protected void completeGame(int stars) {
        if (cancelled) return;
        lastStarsEarned = stars;
        session.recordResult(getGameType(), stars);
        audioManager.playVictory();
        if (onGameComplete != null) onGameComplete.run();
    }
}
