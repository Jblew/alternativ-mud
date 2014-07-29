/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.ai.nn;

/**
 * Próbka do uczenia sieci
 * @author jblew
 */
public class LearningSample {
    private double [] inputs;
    private double [] outputs;
    
    public LearningSample(double [] inputs, double [] outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public double[] getInputs() {
        return inputs;
    }

    public void setInputs(double[] inputs) {
        this.inputs = inputs;
    }

    public double[] getOutputs() {
        return outputs;
    }

    public void setOutputs(double[] outputs) {
        this.outputs = outputs;
    }

}
