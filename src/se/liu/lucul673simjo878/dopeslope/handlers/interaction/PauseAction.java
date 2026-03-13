package se.liu.simjolucul.dopeslope.handlers.interaction;

import se.liu.simjolucul.dopeslope.game.GameBase;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class PauseAction extends AbstractAction {
    private final GameBase gameBase;

    public PauseAction(GameBase gameBase) {
        this.gameBase = gameBase;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        gameBase.setGamePaused(!gameBase.getGamePaused());
    }
}