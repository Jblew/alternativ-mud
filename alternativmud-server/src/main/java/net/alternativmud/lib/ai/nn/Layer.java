/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.ai.nn;

/**
 *
 * @author user
 */
public class Layer {
    public final String name;
    private double [][] weights;
    private double [] signals;
    private NNFunction function = NNFunction.SIGMOID;
    
    public Layer(String name, int numOfNeuronsInPrevLayer, int numOfNeuronsInThisLayer) {
        this.name = name;
        signals = new double[numOfNeuronsInThisLayer];
        weights = new double[numOfNeuronsInPrevLayer][numOfNeuronsInThisLayer];
    }
    
    public Layer() {
        name = "unnamed";
    }

    public double[][] getWeights() {
        return weights;
    }

    public void setWeights(double[][] weights) {
        this.weights = weights;
    }

    public NNFunction getFunction() {
        return function;
    }

    public void setFunction(NNFunction function) {
        this.function = function;
    }

    public double[] getSignals() {
        return signals;
    }

    public void setSignals(double[] values) {
        this.signals = values;
    }
    
    public double [] work(double [] in) {        
        for(int neuNum = 0;neuNum < signals.length;neuNum++) {
            double sum = 0;
            for(int inNum = 0;inNum < in.length;inNum++) {
                sum += in[inNum]
                        *weights[inNum][neuNum];
            }
            signals[neuNum] = function.processSignal(sum);
        }
        return signals.clone();
    }
}
