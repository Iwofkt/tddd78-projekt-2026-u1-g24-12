package se.liu.simjolucul.dopeslope.menu;

import se.liu.simjolucul.dopeslope.game.GameModeType;
import se.liu.simjolucul.dopeslope.Main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The main menu panel for the game.
 * <p>
 * This panel manages the menu's visual components, handles user input,
 * and coordinates between the menu model and view components.
 * It runs on a timer to animate menu elements and process transitions
 * to game modes or other screens.
 * </p>
 */
public class MenuPanel extends JPanel implements ActionListener {

    /** Frames per second for menu animations */
    public static final int FPS = 40;

    private final Main main;
    private final MenuModel menuModel;
    private final MenuComponent menuMenuComponent;
    private final Timer timer;
    private boolean running = false;

    public MenuPanel(Main main, int width, int height) {
	this.main = main;
	setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        menuModel = new MenuModel(width, height);
        menuMenuComponent = new MenuComponent(menuModel);

        // Set up menu action handling
        menuMenuComponent.addActionListener(this::handleMenuAction);

        add(menuMenuComponent, BorderLayout.CENTER);

        timer = new Timer(1000 / FPS, this);
    }

    private void handleMenuAction(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "PLAY":
                menuModel.setScreen(MenuScreen.MODE_SELECT);
                break;

            case "MODE_ENDLESS":
                main.startGame(GameModeType.ENDLESS);
                break;

            case "MODE_COMBE":
                main.startGame(GameModeType.COMBE_DE_CARON);
                break;

            case "BACK":
                menuModel.setScreen(MenuScreen.MAIN);
                break;

            case "OPTIONS":
                menuModel.setScreen(MenuScreen.OPTIONS);
                break;

            case "EXIT":
                System.exit(0);
                break;

            default:
                // Unknown command - ignore
                break;
        }
    }

    public void startMenu() {
        if (!running) {
            running = true;
            timer.start();
            menuMenuComponent.requestFocusInWindow();
        }
    }

    public void stopMenu() {
        if (running) {
            running = false;
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        menuMenuComponent.repaint();
        menuModel.update();
    }
}