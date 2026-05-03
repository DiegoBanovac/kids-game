package hr.adhd.igrica;

import hr.adhd.igrica.util.AudioManager;
import hr.adhd.igrica.util.GameSession;
import hr.adhd.igrica.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Učimo zajedno!");
        stage.setWidth(900);
        stage.setHeight(670);
        stage.setResizable(false);

        GameSession session = new GameSession();
        AudioManager audio = AudioManager.getInstance();
        audio.initialize();

        SceneManager.initialize(stage, session, audio);
        SceneManager.showStartScreen();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
