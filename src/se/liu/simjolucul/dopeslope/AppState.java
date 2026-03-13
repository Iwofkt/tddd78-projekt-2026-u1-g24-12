package se.liu.simjolucul.dopeslope;

/**
 * Represents the current state of the application.
 * <p>
 * Used to track whether the user is in the main menu or actively playing a game.
 * This state is managed by the {@link Main} class.
 *
 * @see Main
 */
public enum AppState {
    /** The application is displaying the main menu. */
    MENU,

    /** The application is in an active game session. */
    PLAYING
}