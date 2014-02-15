/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.logic.time;

import net.alternativmud.Config;

/**
 *
 * @author teofil
 */
public class TimeManager {
    //private final PrecisionTimer precisionTimer;
    private final TimeConfig config;
    private final long constructLocalTime;
    private final long gameTimeCorrection;
    
    public TimeManager(Config config) {
        this.config = config.getTimeConfig();
        //precisionTimer = new NtpPrecisionTimer(this.config.getNtpServers(), new TimeValue(this.config.getTimeSynchronizationFrequencyMs(), TimeUnit.MILLISECONDS));
        this.constructLocalTime = System.currentTimeMillis();
        this.gameTimeCorrection = this.config.getYear1970Date().getTime();

    }

    public long getConstructTime() {
        return constructLocalTime;
    }
    
    public long getGameTime() {
        return gameTimeCorrection + System.currentTimeMillis();
    }
}
