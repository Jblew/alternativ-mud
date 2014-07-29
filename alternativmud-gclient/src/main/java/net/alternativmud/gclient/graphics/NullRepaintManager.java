/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.gclient.graphics;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

public class NullRepaintManager extends RepaintManager {
    public static void install() {
        RepaintManager repaintManager = new NullRepaintManager();
        repaintManager.setDoubleBufferingEnabled(false);
        RepaintManager.setCurrentManager(repaintManager);
    }

    @Override
    public void addInvalidComponent(JComponent component) {
    }

    @Override
    public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
    }

    @Override
    public void markCompletelyDirty(JComponent component) {
    }

    @Override
    public void paintDirtyRegions() {
    }

}