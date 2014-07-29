/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.ai.genetic;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author jblew
 */
public class ContinousChromosome {
    private Map<? extends Enum, Float> independentFeatures;
    private Class<? extends Enum> independentFeaturesClass;

    private ContinousChromosome(Class independentFeaturesClass) {
    //    this.independentFeatures = Collections.synchronizedMap(new EnumMap<?, Float>(independentFeaturesClass));
    //    this.independentFeaturesClass = independentFeaturesClass;
    }

    public Map<? extends Enum, Float> getIndependentFeatures() {
        return independentFeatures;
    }

    public void setIndependentFeatures(Map<? extends Enum, Float> independentFeatures) {
        this.independentFeatures = independentFeatures;
    }

    public Class<? extends Enum> getIndependentFeaturesClass() {
        return independentFeaturesClass;
    }

    public void setIndependentFeaturesClass(Class<? extends Enum> independentFeaturesClass) {
        this.independentFeaturesClass = independentFeaturesClass;
    }
}
