/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.ai.nn;

/**
 * Warstwa wejściowa
 * @author jblew
 */
public class InputLayer extends Layer {
    public InputLayer(String name, int inputs) {
        super(name, 1, inputs);
    }
    
    @Override
    public double [] work(double [] in) {
        return in;
    }
}
