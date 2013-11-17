/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.graphics;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 *
 * @author jblew
 */
public class GraphicsToolkit {
    private final Map<String, ImageIcon> images = new HashMap<>();
    
    public GraphicsToolkit() {
        
    }
    
    public ImageIcon getImage(String path) {
        if(images.containsKey(path)) return images.get(path);
        else {
            ImageIcon img = new ImageIcon(getClass().getResource(path));
            images.put(path, img);
            return img;
        }
    }
}
