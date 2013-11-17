/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.ai.nn;

import java.util.LinkedList;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author jblew
 */
/*
public class BackpropagationNetworkTest extends TestCase {
    
    public BackpropagationNetworkTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testTrainingNeuralNetwork() {
        BackpropagationNetwork net = new BackpropagationNetwork();
        net.setNumberOfLearningIterations(100);
        InputLayer input = new InputLayer("input", 2);
        Layer output = new Layer("output", 4, 1);
        net.setLayers(new Layer[] {
            input,
            new Layer("hidden-1", 2, 4), //hidden layer
            new Layer("hidden-2", 4, 4),
            output
        });
        //Siec powinna rozwiazywac problem XOR
        List<LearningSample> samples = new LinkedList<>();
        samples.add(new LearningSample(new double [] {0, 0}, new double [] {0}));
        samples.add(new LearningSample(new double [] {1, 0}, new double [] {1}));
        samples.add(new LearningSample(new double [] {0, 1}, new double [] {1}));
        samples.add(new LearningSample(new double [] {1, 1}, new double [] {0}));     
        
        net.setSamples(samples);
        
        net.learn();
        
        double out1 [] = net.work(new double [] {1, 1});
        double out2 [] = net.work(new double [] {0, 1});
        double out3 [] = net.work(new double [] {0, 0});
        double out4 [] = net.work(new double [] {1, 0});
        
        System.out.println("out: {"+out1[0]+", "+out2[0]+", "+out3[0]+", "+out4[0]+"}");
        
        assertTrue("Output should be < 0.5. Was: "+out3[0], out3[0] < 0.5);
        assertTrue("Output should be < 0.5. Was: "+out1[0], out1[0] < 0.5);
        assertTrue("Output should be > 0.5. Was: "+out2[0], out2[0] > 0.5);
        assertTrue("Output should be > 0.5. Was: "+out4[0], out4[0] > 0.5);
    }
}*/
