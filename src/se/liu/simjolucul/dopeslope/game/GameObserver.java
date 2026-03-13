package se.liu.simjolucul.dopeslope.game;

/**
 * Observer interface for receiving notifications about changes in the game world.
 * <p>
 * Classes that need to react to game state updates (such as the main rendering component)
 *  @see GameBase#addObserver(GameObserver)
 *  @see GameBase#notifyObservers()
 *  @see GameComponent
 *  */
public interface GameObserver
{
    public void worldUpdated();
}
