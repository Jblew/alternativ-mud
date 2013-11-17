/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.gclient.graphics.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.swing.JPanel;

/**
 *
 * @author jblew
 */
public class JPanelBuilder {
    private JPanel panel;
    
    public JPanelBuilder() {
        panel = new JPanel();
    }
    
    public JPanelBuilder setTransparent() {
        panel.setOpaque(false);
        panel.setBackground(new Color(0,0,0,0));
        return this;
    }
    
    public JPanelBuilder setOpaque(boolean opaque) {
        panel.setOpaque(opaque);
        return this;
    }
    
    public JPanelBuilder setBackground(Color backgroundColor) {
        panel.setBackground(backgroundColor);
        return this;
    }
    
    public JPanelBuilder setLayout(LayoutManager mgr) {
        panel.setLayout(mgr);
        return this;
    }
    
    public JPanelBuilder add(Component component) {
        panel.add(component);
        return this;
    }
    
    public JPanelBuilder add(Component component, Object constraints) {
        panel.add(component, constraints);
        return this;
    }
    
    public JPanelBuilder setPreferredSize(Dimension size) {
        panel.setPreferredSize(size);
        return this;
    }
    
    public JPanel build() {
        return panel;
    }
}
