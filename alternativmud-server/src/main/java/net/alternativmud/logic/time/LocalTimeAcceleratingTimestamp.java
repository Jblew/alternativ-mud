/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.time;

import net.alternativmud.StaticConfig;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author jblew
 */
public class LocalTimeAcceleratingTimestamp {
    private long startValue = 0;
    private long startTime = 0;
    private long accelerationFactor = 1;
    private PrecisionTimer precisionTimer;
    public LocalTimeAcceleratingTimestamp() {
        this(0, 1);
    }
    
     public LocalTimeAcceleratingTimestamp(long startValue, long accelerationFactor) {
        this(new PrecisionTimer() {
            @Override
            public long getCurrentTimeMillis() {
                return System.currentTimeMillis();
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }

        }, 0, 1);
    }

    public LocalTimeAcceleratingTimestamp(PrecisionTimer precisionTimer, long startValue, long accelerationFactor) {
        this.startValue = startValue;
        this.startTime = System.currentTimeMillis();
        this.accelerationFactor = accelerationFactor;
        this.precisionTimer = precisionTimer;

        if (accelerationFactor < 1) {
            throw new IllegalArgumentException("AccelerationFactor must be greater than 0.");
        }
    }

    @JsonIgnore
    public PrecisionTimer getPrecisionTimer() {
        return precisionTimer;
    }

    @JsonIgnore
    public void setPrecisionTimer(PrecisionTimer precisionTimer) {
        this.precisionTimer = precisionTimer;
    }

    public long getTimestamp() {
        return (precisionTimer.getCurrentTimeMillis() - this.startTime) * accelerationFactor + this.startValue;
    }

    public void setTimestamp(long startValue) {
        this.startValue = startValue;
        this.startTime = precisionTimer.getCurrentTimeMillis();
    }

    public long getAccelerationFactor() {
        return accelerationFactor;
    }

    public void setAccelerationFactor(long accelerationFactor) {
        this.accelerationFactor = accelerationFactor;
    }

}
