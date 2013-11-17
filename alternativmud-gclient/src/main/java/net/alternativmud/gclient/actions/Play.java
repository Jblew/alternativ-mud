/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.gclient.actions;

import com.google.common.eventbus.Subscribe;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import net.alternativmud.gclient.forms.FormFrame;
import net.alternativmud.gclient.graphics.NullRepaintManager;
import net.alternativmud.gclient.graphics.ScreenManager;
import net.alternativmud.gclient.graphics.components.JPanelBuilder;
import net.alternativmud.gclient.graphics.components.LogPanel;
import net.alternativmud.gclient.hameplay.DemoTileMap;
import net.alternativmud.graphics.GraphicsConfig;
import net.alternativmud.graphics.GraphicsToolkit;
import net.alternativmud.graphics.PositionDimension;
import net.alternativmud.logic.User;
import net.alternativmud.logic.event.GameplayClientReady;
import net.alternativmud.logic.event.LocationChanged;
import net.alternativmud.logic.event.ReceivedTextFromUser;
import net.alternativmud.logic.event.SendTextToUser;
import net.alternativmud.logic.time.TimeMachine;
import net.alternativmud.logic.world.area.Location;
import net.alternativmud.system.nebus.server.NetworkBusClient;

/**
 * Game ui examples to see: http://www.antonw.com/portfolio/fantasy-game-gui/
 * http://liuxiaoxicici.squarespace.com/art/2010/10/14/game-ui-design.html
 * http://www.antonw.com/portfolio/185/downloads/jadestale-facebook-game-interface-design.JPG
 *
 * Design colours: #832c2a (red), #3853a6 (blue)
 *
 * @author jblew
 */
public class Play implements Runnable {
    private final GraphicsToolkit graphicsToolkit = new GraphicsToolkit();
    private final ExecutorService executor;
    private final FormFrame formFrame;
    private final NetworkBusClient clientBus;
    private final User user;
    private final TimeMachine timeMachine;
    private final String characterName;
    private final JPanel topPanel;
    private final JPanel centerPanel;
    private final JPanel bottomPanel;
    private final LogPanel logPanel = new LogPanel();
    private final JButton goNorthButton = new JButton("go north");
    private Location location = null;
    private long counter = 0;
    private static final DisplayMode POSSIBLE_MODES[] = {
        new DisplayMode(1024, 768, 32, 0),
        new DisplayMode(800, 600, 32, 0),
        new DisplayMode(800, 600, 24, 0),
        new DisplayMode(800, 600, 16, 0),
        new DisplayMode(640, 480, 32, 0),
        new DisplayMode(640, 480, 24, 0),
        new DisplayMode(640, 480, 16, 0)
    };
    private ScreenManager screen;
    public Play(ExecutorService executor, FormFrame formFrame, NetworkBusClient clientBus, User user, TimeMachine timeMachine, String characterName) {
        this.executor = executor;
        this.formFrame = formFrame;
        this.clientBus = clientBus;
        this.user = user;
        this.timeMachine = timeMachine;
        this.characterName = characterName;

        topPanel = new JPanelBuilder().setTransparent().setPreferredSize(new Dimension(Integer.MAX_VALUE, 50)).setLayout(new FlowLayout(FlowLayout.RIGHT)).add(goNorthButton).build();

        centerPanel = new JPanelBuilder().setTransparent().build();

        bottomPanel = new JPanelBuilder().setTransparent().setLayout(new BorderLayout()).add(logPanel, BorderLayout.CENTER).setPreferredSize(new Dimension(Integer.MAX_VALUE, 250)).build();
    }

    @Override
    public void run() {
        clientBus.register(this);
        clientBus.post(new GameplayClientReady());

        screen = new ScreenManager();
        try {
            DisplayMode displayMode =
                    screen.findFirstCompatibleMode(POSSIBLE_MODES);
            //screen.setFullScreen(displayMode);
            screen.setFullScreen(displayMode);

            NullRepaintManager.install();

            initComponents();

            animationLoop();
        } finally {
            screen.restoreScreen();
        }
    }

    public void initComponents() {
        JFrame frame = screen.getFullScreenWindow();

        ((JComponent) frame.getContentPane()).setOpaque(false);
        frame.getContentPane().setBackground(new Color(0, 0, 0, 0));

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        goNorthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                clientBus.post(new ReceivedTextFromUser("north"));
            }

        });

        frame.validate();
    }

    public void loadImages() {
    }

    public void animationLoop() {
        long startTime = System.currentTimeMillis();
        long currTime = startTime;

        while (true) {
            long elapsedTime =
                    System.currentTimeMillis() - currTime;
            currTime += elapsedTime;

            // Aktualizacja duszków:
            update(elapsedTime);

            // Narysowanie i aktualizacja ekranu:
            Graphics2D g = screen.getGraphics();
            draw(g);
            screen.getFullScreenWindow().getContentPane().paintComponents(g);
            g.dispose();
            screen.update();

            // Chwila przerwy:
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException ex) {
            }
        }
    }

    public void update(long elapsedTime) {
        counter++;
        if (counter % 50 == 0) {
            //go to
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screen.getWidth(), screen.getHeight());

        g.setColor(Color.WHITE);

        if (location != null) {
            g.drawString(location.getName() + " " + location.getCoordinates(), 10, 25);
            //location.getBase().draw(g, graphicsToolkit, 
            //        new PositionDimension(0, 50, screen.getWidth(),
            //        screen.getHeight()-250), location);
        }
        for (int i = 0; i < DemoTileMap.tiles.length; i++) {
            for (int j = DemoTileMap.tiles[i].length-1; j >= 0; j--) {  // Changed loop condition here.
                int x = (j * 64 / 2) + (i * 64 / 2);
                int y = (i * 64 / 2) - (j * 64 / 2);
                GraphicsConfig.TILESET.draw(graphicsToolkit, g, DemoTileMap.tiles[i][j], x, y);
            
            }
        }
    }

    @Subscribe
    public void gotEvent(Object evt) {
        System.out.println(evt.toString());
    }

    @Subscribe
    public void locationChanged(LocationChanged evt) {
        location = evt.getLocation();
    }

    @Subscribe
    public void displayText(SendTextToUser u) {
        logPanel.append(u.getText() + "\n");
    }

}
