/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.world.area;

import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import net.alternativmud.graphics.GraphicsToolkit;

/**
 *
 * @author jblew
 */
public enum Base {
    GRASS {
        @Override
        public void draw(Graphics2D g, GraphicsToolkit t, net.alternativmud.graphics.PositionDimension pd, Location l) {
            ImageIcon grassTopImg = t.getImage("/net/alternativmud/tiles/grass.top.png");
            ImageIcon grassMiddleImg = t.getImage("/net/alternativmud/tiles/grass.middle.png");

            int y = pd.getHeight() - 120;
            int x = 0;
            while (x < pd.getWidth()) {
                g.drawImage(grassTopImg.getImage(), x, y, grassTopImg.getImageObserver());
                x += 32;
            }
            y += 32;

            while (y < pd.getHeight()) {
                x = 0;
                while (x < pd.getWidth()) {
                    g.drawImage(grassMiddleImg.getImage(), x, y, grassMiddleImg.getImageObserver());
                    x += 32;
                }
                y += 32;
            }
        }

    }, COBBLESTONES {
        @Override
        public void draw(Graphics2D g, GraphicsToolkit t,
        net.alternativmud.graphics.PositionDimension pd, Location l) {
            GRASS.draw(g, t, pd, l);
        }

    }, SAND {
        @Override
        public void draw(Graphics2D g, GraphicsToolkit t,
        net.alternativmud.graphics.PositionDimension pd, Location l) {
            GRASS.draw(g, t, pd, l);
        }

    };
    public abstract void draw(Graphics2D g, GraphicsToolkit t, net.alternativmud.graphics.PositionDimension pd, Location l);

}
