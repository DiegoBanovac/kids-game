package hr.adhd.igrica.screens;

import hr.adhd.igrica.util.GameType;
import hr.adhd.igrica.util.SceneManager;
import hr.adhd.igrica.util.Settings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class StartScreen {

    public Parent buildLayout() {
        Label title = new Label("Učimo zajedno!");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Odaberi igru:");
        subtitle.getStyleClass().add("subtitle-label");

        Button memoryBtn   = gameButton("◆  Povezivanje slova", GameType.MEMORY, "btn-purple");
        Button colorsBtn   = gameButton("●  Ponavljanje boja",  GameType.COLOR_SEQUENCE, "btn-blue");
        Button reactionBtn = gameButton("★  Klikni zvjezdicu",  GameType.REACTION, "btn-orange");

        Separator sep = new Separator();
        sep.setMaxWidth(340);
        sep.setPadding(new Insets(4, 0, 4, 0));

        boolean dyslexia = Settings.isDyslexiaMode();
        Button dyslexiaBtn = new Button(dyslexia
                ? "Mod za disleksiju:  ON"
                : "Mod za disleksiju:  OFF");
        dyslexiaBtn.getStyleClass().addAll("dyslexia-toggle",
                dyslexia ? "dyslexia-on" : "dyslexia-off");
        dyslexiaBtn.setMaxWidth(380);
        dyslexiaBtn.setOnAction(e -> {
            Settings.toggleDyslexiaMode();
            SceneManager.showStartScreen();
        });

        VBox panel = new VBox(18, title, subtitle,
                memoryBtn, colorsBtn, reactionBtn,
                sep, dyslexiaBtn);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("start-panel");
        panel.setMaxWidth(500);

        StackPane root = new StackPane(panel);
        root.getStyleClass().add("root-bg");
        return root;
    }

    private Button gameButton(String label, GameType type, String colorClass) {
        Button btn = new Button(label);
        btn.getStyleClass().addAll("game-button", colorClass);
        btn.setMaxWidth(380);
        btn.setOnAction(e -> SceneManager.showGame(type));
        return btn;
    }
}
