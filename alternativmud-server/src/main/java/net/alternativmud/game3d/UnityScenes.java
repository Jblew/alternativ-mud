/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.game3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author teofil
 */
public class UnityScenes {
    private static final Map<String, byte []> variablesScenes;
    //No more scenes than Byte.MAX_VALUE!
    public static final Scene [] SCENES = new Scene [] {
        new Scene("ElegantRoom", new String [] {}),
        new Scene("Passage", new String [] {"global.power"}),
        new Scene("Train", new String [] {"global.power"}),
        new Scene("Store", new String [] {"global.power"})
    };
    
    static {
        Map<String, List<Byte>> tmp = new HashMap<String, List<Byte>>();
        for(Scene s : SCENES) {
            for(String variable : s.getVariables()) {
                if(tmp.containsKey(variable)) {
                    tmp.get(variable).add(getSceneID(s.getName()));
                }
                else {
                    List<Byte> list = new ArrayList<Byte>();
                    list.add(getSceneID(s.getName()));
                    tmp.put(variable, list);
                }
            }
        }
        
        variablesScenes = new HashMap<String, byte[]>();
        for(String variable : tmp.keySet()) {
            byte [] scenes = new byte[tmp.get(variable).size()];
            for(int i = 0;i < tmp.get(variable).size();i++) {
                scenes[i] = tmp.get(variable).get(i);
            }
            variablesScenes.put(variable, scenes);
        }
    }
    
    public static byte getSceneID(String name) {
        byte id = 0;
        boolean found = false;
        for(byte i = 0;i < SCENES.length;i++) {
            if(name.equals(SCENES[i].getName())) {
                id = i;
                found = true;
                break;
            }
        }
        if(found) return id;
        else throw new NoSuchElementException("Scene "+name);
    }
    
    public static byte[] getScenesUsingVariable(String key) {
        return variablesScenes.get(key);
    }
    
    private UnityScenes() {}
}
