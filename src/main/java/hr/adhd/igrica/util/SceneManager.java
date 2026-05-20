package hr.adhd.igrica.util;

import hr.adhd.igrica.games.BaseGame;
import hr.adhd.igrica.games.ColorSequenceGame;
import hr.adhd.igrica.games.MemoryGame;
import hr.adhd.igrica.games.ReactionGame;
import hr.adhd.igrica.screens.EndScreen;
import hr.adhd.igrica.screens.StartScreen;
import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    private static Stage stage;
    private static GameSession session;
    private static AudioManager audio;

    public static void initialize(Stage s, GameSession gs, AudioManager am) {
        stage = s;
        session = gs;
        audio = am;
    }

    public static void showStartScreen() {
        switchTo(new StartScreen().buildLayout());
    }

    public static void showGame(GameType type) {
        BaseGame game = switch (type) {
            case MEMORY          -> new MemoryGame(session, audio);
            case COLOR_SEQUENCE  -> new ColorSequenceGame(session, audio);
            case REACTION        -> new ReactionGame(session, audio);
        };
        game.setOnGameComplete(() -> showEndScreen(game.getLastStarsEarned(), type));
        Parent root = game.buildLayout();
        switchTo(root);
        game.startGame();
    }

    public static void showEndScreen(int stars, GameType lastGame) {
        switchTo(new EndScreen(stars, session.getTotalStars(), lastGame).buildLayout());
    }

    private static void switchTo(Parent root) {
        Scene scene = new Scene(root, 900, 670);

        scene.getStylesheets().add(SceneManager.class
                .getResource("/hr/adhd/igrica/styles/main.css")
                .toExternalForm());

        if (Settings.isDyslexiaMode()) {
            scene.getStylesheets().add(SceneManager.class
                    .getResource("/hr/adhd/igrica/styles/dyslexia.css")
                    .toExternalForm());
        }

        root.setOpacity(0);
        stage.setScene(scene);
        FadeTransition ft = new FadeTransition(Duration.millis(200), root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
}
