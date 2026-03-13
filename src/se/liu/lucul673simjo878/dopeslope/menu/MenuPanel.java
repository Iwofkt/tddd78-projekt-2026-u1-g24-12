package se.liu.simjolucul.dopeslope.menu;

import se.liu.simjolucul.dopeslope.game.GameModeType;
import se.liu.simjolucul.dopeslope.Main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel implements ActionListener {
    private Main main;
    private MenuModel menuModel;
    private MenuComponent menuMenuComponent;
    private Timer timer;
    public static final int FPS = 40;
    private boolean running = false;

    public MenuPanel(Main main, int width, int height) {
        this.main = main;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        menuModel = new MenuModel(width, height);
        menuMenuComponent = new MenuComponent(menuModel);

        // Listen for menu actions (e.g., Start Game selected)
        menuMenuComponent.addActionListener(e -> {
            String command = e.getActionCommand();
            if ("Start Game".equals(command)) {
                main.startGame(GameModeType.Endless);
            }
            else if ("Options".equals(command)) {
                // handle options
            }
        });

        add(menuMenuComponent, BorderLayout.CENTER);

        timer = new Timer(1000 / FPS, this);

        // In MenuPanel constructor, after creating menuComponent:
        menuMenuComponent.addActionListener(e -> {
            String cmd = e.getActionCommand();
            switch (cmd) {
                case "PLAY":
                    menuModel.setScreen(MenuScreens.MODE_SELECT);
                    break;
                case "MODE_ENDLESS":
                    main.startGame(GameModeType.Endless);
                    break;
                case "MODE_COMBE":
                    main.startGame(GameModeType.CombeDeCaron);
                    break;
                case "BACK":
                    menuModel.setScreen(MenuScreens.MAIN);
                    break;
                case "OPTIONS":
                    menuModel.setScreen(MenuScreens.OPTIONS);
                    break;
                case "EXIT":
                    System.exit(0);
                    break;
                // ... handle options sub‑items later
            }
        });
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