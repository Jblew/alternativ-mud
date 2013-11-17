/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.graphics;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author jblew
 */
public class PositionDimension {
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    @JsonCreator
    public PositionDimension(
            @JsonProperty("x") int x,
            @JsonProperty("y") int y,
            @JsonProperty("width") int width,
            @JsonProperty("height") int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
