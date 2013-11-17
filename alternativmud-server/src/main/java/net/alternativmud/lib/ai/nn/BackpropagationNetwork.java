/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.ai.nn;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.alternativmud.lib.util.DebugUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author jblew
 */
public class BackpropagationNetwork {

    private Layer[] layers;
    private int numberOfLearningIterations = 200;
    private List<LearningSample> samples = new LinkedList<>();

    public BackpropagationNetwork() {
    }

    public Layer[] getLayers() {
        return layers;
    }

    public void setLayers(Layer[] layers) {
        this.layers = layers;

        if (layers.length < 2) {
            throw new IllegalArgumentException("BackpropagationNetwork needs at least two layers (including input layer)");
        }

        for (int i = 0; i < layers.length; i++) {
            if (i > 0) { //don't check for first
                if (layers[i].getWeights().length != layers[i - 1].getSignals().length) {
                    throw new IllegalArgumentException("Layer " + i + " does not have proper number of input weights.");
                }
            }
        }
    }

    public int getNumberOfLearningIterations() {
        return numberOfLearningIterations;
    }

    public void setNumberOfLearningIterations(int numberOfLearningIterations) {
        this.numberOfLearningIterations = numberOfLearningIterations;
    }

    public List<LearningSample> getSamples() {
        return samples;
    }

    public void setSamples(List<LearningSample> samples) {
        this.samples = samples;
    }

    @JsonIgnore
    public void addSample(LearningSample s) {
        samples.add(s);
    }

    public double[] work(double[] data) {
        //System.out.println("net.work");
        AtomicReference<double[]> data_ = new AtomicReference<>(data.clone());
        for (Layer l : layers) {
            //System.out.println("   "+l.name+": "+DebugUtils.arrayToString(data_.get()));
            data_.set(l.work(data_.get()));
        }
        return data_.get();
    }

    public void learn() {
        for (Layer l : layers) {
            for (double[] weights : l.getWeights()) {
                for (int i = 0; i < weights.length; i++) {
                    weights[i] = Math.random() * 2 - 1; //<-1,1>
                }
            }
        }

        for (int ni = 1; ni < numberOfLearningIterations+1; ni++) {
            double learning_coeff = 1f / ni;
            for (LearningSample sample : samples) {
                //testujemy siec
                double[] out = work(sample.getInputs());

                /*
                 * double[] outputError = new double[out.length]; // obliczamy
                 * błąd na wyjściu for (int i = 0; i < out.length; i++) {
                 * outputError[i] = (sample.getOutputs()[i] - out[i]) * 2.0 *
                 * out[i] * (1.0 - out[i]); }
                 */

                AtomicReference<double[]> error = new AtomicReference<>();
                for (int lNum = layers.length - 1; lNum >= 0; lNum--) {
                    Layer l = layers[lNum];
                    if (lNum == layers.length - 1) { //warstwa wyjsciowa
                        double[] outputError = new double[out.length];
                        // obliczamy błąd na wyjściu
                        for (int j = 0; j < l.getSignals().length; j++) {
                            outputError[j] = (sample.getOutputs()[j] - l.getSignals()[j]) * 2.0 * l.getSignals()[j] * (1.0 - l.getSignals()[j]);
                        }
                        error.set(outputError);
                    } else {
                        Layer nextL = layers[lNum + 1];
                        double[] error_ = new double[l.getSignals().length];
                        for (int neuNum = 0; neuNum < l.getSignals().length; neuNum++) {
                            double sum = 0;
                            for (int wNum = 0; wNum < nextL.getWeights()[neuNum].length; wNum++) {
                                sum += nextL.getWeights()[neuNum][wNum] * error.get()[wNum];
                            }
                            error_[neuNum] = sum * 2.0f * l.getSignals()[neuNum] * (1.0f - l.getSignals()[neuNum]);
                        }

                        //obliczamy poprawke wag
                        for (int thisNeuNum = 0; thisNeuNum < l.getSignals().length; thisNeuNum++) {
                            for (int nextNeuNum = 0; nextNeuNum < nextL.getSignals().length; nextNeuNum++) {
                                double delta = nextL.getWeights()[thisNeuNum][nextNeuNum]
                                        + learning_coeff * error.get()[nextNeuNum]
                                        * l.getSignals()[thisNeuNum];
                                nextL.getWeights()[thisNeuNum][nextNeuNum] += delta;
                                //System.out.println("Delta = "+nextL.getWeights()[thisNeuNum][nextNeuNum]+" + "
                                //        +learning_coeff+" * "+error.get()[nextNeuNum]+" * "+l.getSignals()[thisNeuNum]
                                //        + " = "+delta);
                                //System.out.println("Weight set to: "+ nextL.getWeights()[thisNeuNum][nextNeuNum]);
                            }
                        }
                        
                        error.set(error_);//zapisujemy errory dla nastepnej warstwy
                    }
                }
            }
        }
    }
}
