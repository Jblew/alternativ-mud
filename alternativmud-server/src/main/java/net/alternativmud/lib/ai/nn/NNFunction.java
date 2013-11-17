/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.ai.nn;

/**
 *
 * @author jblew
 */
public interface NNFunction {
    public double processSignal(double signal);
    
    public static final NNFunction BIPOLAR_ACTIVATION = new NNFunction() {
        @Override
        public double processSignal(double signal) {
            return (signal > 0? 1 : -1);
        }
    };
    
    public static final NNFunction UNIPOLAR_ACTIVATION = new NNFunction() {
        @Override
        public double processSignal(double signal) {
            return (signal > 0? 1 : 0);
        }
    };
    
    public static final NNFunction SIGMOID = new NNFunction() {
        @Override
        public double processSignal(double signal) {
            return 1/(1+Math.exp(-signal));
        }
    };
    
    public static final NNFunction PASS = new NNFunction() {
        @Override
        public double processSignal(double signal) {
            return signal;
        }
    };
}
