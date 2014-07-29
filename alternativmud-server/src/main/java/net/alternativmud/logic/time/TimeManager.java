/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.logic.time;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        this.gameTimeCorrection = ((long)this.config.getYearCorrection())*365l*24l*3600l*1000l+((long)Math.floor(((double)this.config.getYearCorrection())/4d))*24l*3600l*1000l;
        Logger.getLogger(TimeManager.class.getName()).log(Level.INFO, "StartedTimeManager, Current time in game: {0} (correction: {1})", new Object [] {new Date(getGameTime()).toString(), this.gameTimeCorrection});
    }

    public long getConstructTime() {
        return constructLocalTime;
    }
    
    public long getGameTime() {
        return gameTimeCorrection + System.currentTimeMillis();
    }
}
