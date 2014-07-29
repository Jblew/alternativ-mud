/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.gclient.graphics;

import java.awt.Image;

/**
 *
 * @author jblew
 */
public class Sprite {
    private Animation anim;
    // Pozycja (piksele):
    private float x;
    private float y;
    // Prêdkoæ (piksele na milisekundê):
    private float dx;
    private float dy;

    /**
        Tworzy nowy obiekt Sprite z w³aciwym obiektem Animation.
    */
    public Sprite(Animation anim) {
        this.anim = anim;
    }

    /**
        Aktualizuje obiekt Animation dla bie¿¹cego Sprite 
        oraz swoj¹ pozycjê na podstawie prêdkoci.
    */
    public void update(long elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        anim.update(elapsedTime);
    }

    /**
        Zwraca wspó³rzêdn¹ x bie¿¹cego obiektu Sprite.
    */
    public float getX() {
        return x;
    }

    /**
        Zwraca wspó³rzêdn¹ x bie¿¹cego obiektu Sprite.
    */
    public float getY() {
        return y;
    }

    /**
        Ustawia wspó³rzêdn¹ x bie¿¹cego obiektu Sprite.
    */
    public void setX(float x) {
        this.x = x;
    }

    /**
        Ustawia wspó³rzêdn¹ y bie¿¹cego obiektu Sprite.
    */
    public void setY(float y) {
        this.y = y;
    }

    /**
        Zwraca szerokoæ bie¿¹cego obiektu Sprite, 
        korzystaj¹c z rozmiaru bie¿¹cego rysunku.
    */
    public int getWidth() {
        return anim.getImage().getWidth(null);
    }

    /**
        Zwraca wysokoæ bie¿¹cego obiektu Sprite, 
        korzystaj¹c z rozmiaru bie¿¹cego rysunku.
    */
    public int getHeight() {
        return anim.getImage().getHeight(null);
    }

    /**
        Zwraca prêdkoæ w poziomie bie¿¹cego obiektu Sprite 
        w pikselach na milisekundê.
    */
    public float getVelocityX() {
        return dx;
    }

    /**
        Zwraca prêdkoæ w pionie bie¿¹cego obiektu Sprite 
        w pikselach na milisekundê.
    */
    public float getVelocityY() {
        return dy;
    }

    /**
        Ustawia prêdkoæ w poziomie bie¿¹cego obiektu Sprite 
        w pikselach na milisekundê.
    */
    public void setVelocityX(float dx) {
        this.dx = dx;
    }

    /**
        Ustawia prêdkoæ w pionie bie¿¹cego obiektu Sprite 
        w pikselach na milisekundê.
    */
    public void setVelocityY(float dy) {
        this.dy = dy;
    }

    /**
        Zwraca bie¿¹cy rysunek dla tego obiektu Sprite.
    */
    public Image getImage() {
        return anim.getImage();
    }
}
