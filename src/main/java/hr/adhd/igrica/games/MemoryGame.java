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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryGame extends BaseGame {

    // Each pair: [letter, Croatian word]
    private static final String[][] PAIRS = {
        {"A", "Avion"},
        {"B", "Brod"},
        {"M", "Macka"},
        {"S", "Sunce"},
        {"L", "Lopta"},
        {"P", "Pas"},
        {"R", "Riba"},
        {"T", "Torta"}
    };

    private final List<CardView> cards = new ArrayList<>();
    private CardView firstFlipped;
    private CardView secondFlipped;
    private boolean isChecking = false;
    private int matchesFound = 0;
    private int flipCount = 0;
    private Label instructionLabel;

    public MemoryGame(GameSession session, AudioManager audioManager) {
        super(session, audioManager);
    }

    @Override public String getGameTitle()  { return "◆ Povezivanje slova"; }
    @Override protected GameType getGameType() { return GameType.MEMORY; }

    @Override
    public Parent buildLayout() {
        instructionLabel = new Label("Pronadi parove: slovo  rijec!");
        instructionLabel.getStyleClass().add("instruction-label");

        VBox content = new VBox(12, instructionLabel, buildCardGrid());
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(content);
        return root;
    }

    private GridPane buildCardGrid() {
        List<CardData> data = new ArrayList<>();
        for (int i = 0; i < PAIRS.length; i++) {
            data.add(new CardData(i, true,  PAIRS[i][0]));
            data.add(new CardData(i, false, PAIRS[i][1]));
        }
        Collections.shuffle(data);

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < data.size(); i++) {
            CardView card = new CardView(data.get(i));
            card.setOnCardClicked(() -> handleCardClick(card));
            cards.add(card);
            grid.add(card, i % 4, i / 4);
        }
        return grid;
    }

    @Override
    public void startGame() {
        // layout already ready; game starts on first click
    }

    @Override
    public void resetGame() {
        cards.clear();
        firstFlipped = null;
        secondFlipped = null;
        isChecking = false;
        matchesFound = 0;
        flipCount = 0;
    }

    private void handleCardClick(CardView card) {
        if (isChecking || card.isFaceUp() || card.isMatched()) return;

        flipCount++;
        card.flip(true);

        if (firstFlipped == null) {
            firstFlipped = card;
        } else {
            secondFlipped = card;
            isChecking = true;
            PauseTransition pause = new PauseTransition(Duration.millis(900));
            pause.setOnFinished(e -> checkMatch());
            pause.play();
        }
    }

    private void checkMatch() {
        if (firstFlipped.getPairId() == secondFlipped.getPairId()) {
            firstFlipped.setMatched();
            secondFlipped.setMatched();
            matchesFound++;
            starSystem.addStars(1);
            audioManager.playCorrect();
            instructionLabel.setText("Bravo! ★  Nadeno " + matchesFound + " / " + PAIRS.length + " parova!");

            if (matchesFound == PAIRS.length) {
                PauseTransition end = new PauseTransition(Duration.millis(700));
                end.setOnFinished(e -> finishGame());
                end.play();
            }
        } else {
            firstFlipped.flip(false);
            secondFlipped.flip(false);
            audioManager.playWrong();
            instructionLabel.setText("✗ Nije par, pokusaj ponovo!");
            PauseTransition reset = new PauseTransition(Duration.millis(1200));
            reset.setOnFinished(e -> instructionLabel.setText("Pronadi parove: slovo  rijec!"));
            reset.play();
        }
        firstFlipped = null;
        secondFlipped = null;
        isChecking = false;
    }

    private void finishGame() {
        int stars;
        int total = PAIRS.length;
        if      (flipCount <= total * 2 + 4) stars = 3;
        else if (flipCount <= total * 3)     stars = 2;
        else                                 stars = 1;
        completeGame(stars);
    }

    // ---- Data record ----

    private record CardData(int pairId, boolean isLetter, String display) {}

    // ---- CardView inner class ----

    private class CardView extends StackPane {
        private final CardData data;
        private boolean faceUp = false;
        private boolean matched = false;
        private Runnable onClicked;

        private final StackPane backFace;
        private final StackPane frontFace;

        CardView(CardData data) {
            this.data = data;
            setPrefSize(108, 108);
            setMinSize(108, 108);
            setMaxSize(108, 108);

            Label backLabel = new Label("?");
            backLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: white; -fx-font-weight: bold;");
            backFace = new StackPane(backLabel);
            backFace.getStyleClass().addAll("card", "card-back");
            backFace.setPrefSize(108, 108);

            Label frontMain = new Label(data.display());
            if (data.isLetter()) {
                frontMain.setStyle("-fx-font-size: 48px; -fx-text-fill: #2C3E8C; -fx-font-weight: bold;");
            } else {
                frontMain.setStyle("-fx-font-size: 20px; -fx-text-fill: #7B3FA0; -fx-font-weight: bold;");
            }
            frontFace = new StackPane(frontMain);
            frontFace.getStyleClass().addAll("card", "card-front");
            frontFace.setPrefSize(108, 108);

            getChildren().add(backFace);
            setOnMouseClicked(e -> { if (onClicked != null) onClicked.run(); });
        }

        void setOnCardClicked(Runnable r) { this.onClicked = r; }
        boolean isFaceUp()  { return faceUp;  }
        boolean isMatched() { return matched; }
        int getPairId()     { return data.pairId(); }

        void flip(boolean toFaceUp) {
            ScaleTransition shrink = new ScaleTransition(Duration.millis(140), this);
            shrink.setFromX(1.0);
            shrink.setToX(0.0);
            shrink.setOnFinished(e -> {
                faceUp = toFaceUp;
                getChildren().setAll(toFaceUp ? frontFace : backFace);
                ScaleTransition grow = new ScaleTransition(Duration.millis(140), this);
                grow.setFromX(0.0);
                grow.setToX(1.0);
                grow.play();
            });
            shrink.play();
        }

        void setMatched() {
            matched = true;
            frontFace.getStyleClass().setAll("card", "card-matched");
            onClicked = null;
            ScaleTransition pulse = new ScaleTransition(Duration.millis(180), this);
            pulse.setFromX(1.0); pulse.setFromY(1.0);
            pulse.setToX(1.12); pulse.setToY(1.12);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(2);
            pulse.play();
        }
    }
}
