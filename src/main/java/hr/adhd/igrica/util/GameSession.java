package hr.adhd.igrica.util;

import java.util.EnumMap;
import java.util.Map;

public class GameSession {

    private final Map<GameType, Integer> bestStars = new EnumMap<>(GameType.class);
    private int totalStarsEarned = 0;
    private GameType lastGame;

    public void recordResult(GameType type, int stars) {
        lastGame = type;
        totalStarsEarned += stars;
        bestStars.merge(type, stars, Math::max);
    }

    public int getTotalStars() {
        return totalStarsEarned;
    }

    public int getBestStars(GameType type) {
        return bestStars.getOrDefault(type, 0);
    }

    public GameType getLastGame() {
        return lastGame;
    }
}
