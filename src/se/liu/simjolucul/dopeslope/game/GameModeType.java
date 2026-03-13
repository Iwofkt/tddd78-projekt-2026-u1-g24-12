package se.liu.simjolucul.dopeslope.game;


/**
 * Represents the different game modes available in the application.
 * <p>
 * Each game mode defines a different gameplay experience:
 * <ul>
 *   <li>{@link #ENDLESS} – an infinite slope with procedurally generated obstacles,
 *       where the goal is to survive as long as possible and maximize distance.</li>
 *   <li>{@link #COMBE_DE_CARON} – a fixed alpine course based on the real‑world
 *       Combe de Caron slope, where the goal is to reach the finish line as
 *       quickly as possible.</li>
 * </ul>
 * <p>
 * Used throughout the game to switch between different game logic,
 * highscore handling, and UI displays.
 *
 * @see GameBase#getGameModeType()
 * @see GameBase#restart(GameModeType)
 * @see GameComponent#drawGameStats(Graphics2D)
 */
public enum GameModeType
{
    ENDLESS, COMBE_DE_CARON
}
