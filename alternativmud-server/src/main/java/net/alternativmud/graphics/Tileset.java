/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.graphics;

import java.awt.Graphics2D;
import java.awt.Shape;

/**
 *
 * @author jblew
 */
public class Tileset {
    private final String filepath;
    private final int tileDimension;
    private final int tilesX;
    public Tileset(String filepath, int tileDimension, int tilesX) {
        this.filepath = filepath;
        this.tileDimension = tileDimension;
        this.tilesX = tilesX;
    }

    public void draw(GraphicsToolkit gt, Graphics2D g, int tileNum, int x, int y) {
        int tileNumX = tileNum % tilesX;
        int tileNumY = (int) Math.floor(((double)tileNum)/((double)tilesX));
        
        Shape oldClip = g.getClip();
        g.setClip(x, y, tileDimension, tileDimension);
        g.drawImage(gt.getImage(filepath).getImage(), tileNumX*tileDimension,
                tileNumY*tileDimension, tileDimension, tileDimension, null);
        g.setClip(oldClip);
    }

}
