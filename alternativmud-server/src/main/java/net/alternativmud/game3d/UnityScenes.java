/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.game3d;

import java.util.NoSuchElementException;

/**
 *
 * @author teofil
 */
public class UnityScenes {
    //No more scenes than Byte.MAX_VALUE!
    public static String [] SCENES = new String [] {
        "ElegantRoom",
        "Passage",
        "Train"
    };
    
    public static byte getSceneID(String name) {
        byte id = 0;
        boolean found = false;
        for(byte i = 0;i < SCENES.length;i++) {
            if(name.equals(SCENES[i])) {
                id = i;
                found = true;
                break;
            }
        }
        if(found) return id;
        else throw new NoSuchElementException("Scene "+name);
    }
    
    private UnityScenes() {}
}
