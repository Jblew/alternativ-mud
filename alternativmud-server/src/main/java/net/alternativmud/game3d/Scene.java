/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.game3d;

/**
 *
 * @author teofil
 */
public class Scene {
    private final String name;
    private final String [] variables;

    public Scene(String name, String[] variables) {
        this.name = name;
        this.variables = variables;
    }

    public String getName() {
        return name;
    }

    public String[] getVariables() {
        return variables;
    }
}
