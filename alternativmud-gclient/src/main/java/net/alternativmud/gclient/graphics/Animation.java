/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.gclient.graphics;

import java.awt.Image;
import java.util.ArrayList;

/**
    Klasa Animation zarz¹dza seri¹ rysunków (ramek) oraz
    czasem wywietlania ka¿dej z ramek.
*/
public class Animation {
    private ArrayList frames;
    private int currFrameIndex;
    private long animTime;
    private long totalDuration;

    /**
        Tworzy nowy, pusty obiekt Animation.
    */
    public Animation() {
        frames = new ArrayList();
        totalDuration = 0;
        start();
    }

    /**
        Dodaje do animacji rysunek o okrelonym czasie wywietlania.
    */
    public synchronized void addFrame(Image image,
        long duration)
    {
        totalDuration += duration;
        frames.add(new AnimFrame(image, totalDuration));
    }

    /**
        Uruchamia animacjê od pocz¹tku.
    */
    public synchronized void start() {
        animTime = 0;
        currFrameIndex = 0;
    }

    /**
        W razie potrzeby modyfikuje bie¿¹c¹ ramkê (rysunek) animacji.
    */
    public synchronized void update(long elapsedTime) {
        if (frames.size() > 1) {
            animTime += elapsedTime;

            if (animTime >= totalDuration) {
                animTime = animTime % totalDuration;
                currFrameIndex = 0;
            }

            while (animTime > getFrame(currFrameIndex).endTime) {
                currFrameIndex++;
            }
        }
    }

    /**
        Pobiera bie¿¹cy rysunek z animacji. Zwraca null,
        je¿eli animacja nie zawiera rysunków.
    */
    public synchronized Image getImage() {
        if (frames.size() == 0) {
            return null;
        }
        else {
            return getFrame(currFrameIndex).image;
        }
    }

    private AnimFrame getFrame(int i) {
        return (AnimFrame)frames.get(i);
    }

    private class AnimFrame {

        Image image;
        long endTime;

        public AnimFrame(Image image, long endTime) {
            this.image = image;
            this.endTime = endTime;
        }
    }
}