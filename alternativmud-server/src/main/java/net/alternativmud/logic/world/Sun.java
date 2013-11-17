/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.world;

import net.alternativmud.lib.persistence.PersistenceManager;

/**
 *
 * @author jblew
 */
public class Sun {
    private float visibleRadiationRate = 1.0f; //współczynnik promieniowania widzialnego
    private float infraRadiationRane = 0.8f; //współczynnik promieniowania niskich częstotliwości
    private float ultraRadiationRate = 0.5f; //współczynnik promieniowania wysokich częstotliwości
    
    public Sun() {
        
    }

    public float getInfraRadiationRane() {
        return infraRadiationRane;
    }

    public void setInfraRadiationRane(float infraRadiationRane) {
        this.infraRadiationRane = infraRadiationRane;
    }

    public float getUltraRadiationRate() {
        return ultraRadiationRate;
    }

    public void setUltraRadiationRate(float ultraRadiationRate) {
        this.ultraRadiationRate = ultraRadiationRate;
    }

    public float getVisibleRadiationRate() {
        return visibleRadiationRate;
    }

    public void setVisibleRadiationRate(float visibleRadiationRate) {
        this.visibleRadiationRate = visibleRadiationRate;
    }
}
