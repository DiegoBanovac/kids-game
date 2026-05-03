package hr.adhd.igrica.screens;

import hr.adhd.igrica.util.GameType;
import hr.adhd.igrica.util.SceneManager;
import hr.adhd.igrica.util.TtsManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class StartScreen {

    public Parent buildLayout() {
        Label title = new Label("Učimo zajedno! 🌟");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Odaberi igru:");
        subtitle.getStyleClass().add("subtitle-label");

        Button memoryBtn  = gameButton("🃏  Sparivanje slova",
                "Pronađi parove slova i slika!", GameType.MEMORY);
        Button colorsBtn  = gameButton("🌈  Ponavljanje boja",
                "Ponovi niz boja po redu!", GameType.COLOR_SEQUENCE);
        Button reactionBtn = gameButton("⭐  Klikni zvjezdicu",
                "Klikni što brže možeš!", GameType.REACTION);

        Button ttsBtn = new Button("🔊 Čuj uputu");
        ttsBtn.getStyleClass().add("tts-button");
        ttsBtn.setOnAction(e -> TtsManager.speak("Odaberi igru. Sparivanje slova, Ponavljanje boja ili Klikni zvjezdicu."));

        VBox center = new VBox(18, title, subtitle, memoryBtn, colorsBtn, reactionBtn, ttsBtn);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));

        StackPane root = new StackPane(center);
        root.setStyle("-fx-background-color: #FDFAF0;");
        return root;
    }

    private Button gameButton(String label, String ttsText, GameType type) {
        Button btn = new Button(label);
        btn.getStyleClass().add("game-button");
        btn.setMaxWidth(360);
        btn.setOnAction(e -> {
            TtsManager.speak(ttsText);
            SceneManager.showGame(type);
        });
        return btn;
    }
}
