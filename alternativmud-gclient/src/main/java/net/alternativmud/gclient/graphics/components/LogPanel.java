/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.gclient.graphics.components;

import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

/**
 *
 * @author jblew
 */
public class LogPanel extends JScrollPane {
    private final LogArea logArea;
    
    public LogPanel() {
        setOpaque(false);
        setBackground(new Color(0,0,0,0));
        
        logArea = new LogArea();
        setViewportView(logArea);
        
        logArea.setText("<html>"
                + "<font color=\"red\"><strong>"
                + "Hi! Welcome to AlternativMUD"
                + "</strong></font>");
    }
    
    public void append(String text) {
        logArea.setText(logArea.getText()+text);
        scrollRectToVisible(new Rectangle(0,logArea.getHeight()-2,1,1));
    }
    
    /*@Override
        public void paintComponent(Graphics g) {
            super.paintChildren(g);
        }*/
    
    private class LogArea extends JLabel {
        public LogArea() {
            setBackground(new Color(0,0,0,0));
            setVerticalAlignment(JLabel.TOP);
        }
        
        /*@Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }*/
    }
}
