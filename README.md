# kids-game — 

An educational desktop application for children in grades 1–4 with ADHD and dyslexia, built in collaboration with Vista ADHD Center in Rijeka.

## About

"Ucimo zajedno!" (Let's Learn Together!) is a JavaFX desktop app designed as part of the Multimedia Systems course. It follows pedagogical guidelines provided by Vista ADHD Center, with a focus on minimal interfaces, short tasks, fast feedback, and audio support for children who struggle with reading.

The app contains 3 interactive educational games, a star-based motivation system, and text-to-speech support for dyslexia accessibility.

## Tech Stack

- Java 17
- JavaFX 21 (controls + media)
- Maven

## Games

### Sparivanje slova (Letter-Image Memory)
Match letter cards to their corresponding image cards. Each pair consists of a Croatian letter, an emoji, and a word hint (A = avion, B = banana, M = macka, S = sunce, L = lopta). Covers task requirement: *letter-sound memory card pairs*.

### Ponavljanje boja (Color Sequence)
A Simon-style game with 4 colored circles (red, blue, yellow, green). The app flashes a growing sequence and the player must repeat it in the correct order. 5 rounds. Covers task requirement: *repeat a color sequence of 2-5 elements*.

### Klikni zvjezdicu (Reaction Game)
A star appears at a random position on screen. The player clicks it as fast as possible. 10 rounds with a 3-second timeout per round. Reaction times are tracked and shown at the end. Covers task requirement: *click as fast as you can when the star appears*.

## ADHD and Dyslexia Features

- **Text-to-speech** — a "Cuj uputu" (Hear instructions) button on the start screen reads the game menu aloud using TtsManager
- **Audio feedback** — AudioManager plays sounds for correct and incorrect answers
- **Star reward system** — StarSystem tracks and displays earned stars to motivate children
- **Minimalist UI** — one task per screen, no visual distractions, warm background color
- **Short tasks** — each game lasts 5-10 rounds, under 2 minutes
- **Visual animations** — smooth FadeTransition and ScaleTransition feedback on every interaction

## Project Structure

```
src/main/java/hr/adhd/igrica/
  games/
    BaseGame.java            # Abstract base class for all games
    ColorSequenceGame.java   # Simon color sequence game
    MemoryGame.java          # Letter-image card matching game
    ReactionGame.java        # Star reaction speed game
  screens/
    StartScreen.java         # Game selection menu with TTS button
    EndScreen.java           # Results screen with replay option
  util/
    AudioManager.java        # Sound effect playback
    GameSession.java         # Score and session state tracking
    GameType.java            # Enum for game types
    SceneManager.java        # Screen navigation
    StarSystem.java          # Star reward logic
    TtsManager.java          # Text-to-speech for dyslexia support
  App.java                   # Application entry point
```

## Running the App

Requires Java 17+ and Maven.

```bash
mvn clean javafx:run
```

Or build a JAR:

```bash
mvn clean package
java -jar target/igrica-1.0-SNAPSHOT.jar
```

## Course

Built for the **Izgradnja multimedijskih sustava** (Building Multimedia Systems) course at the University of Applied Sciences of Rijeka, in partnership with **Vista ADHD Center**.
