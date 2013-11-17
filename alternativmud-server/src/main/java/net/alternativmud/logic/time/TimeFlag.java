/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.time;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author jblew
 */
public class TimeFlag {

    public final TimeValue time;
    public final long startTime;
    public final long endTime;

    public TimeFlag(TimeValue time) {
        this.time = time;

        startTime = System.nanoTime();
        endTime = startTime + time.unit.toNanos(time.value);
    }
    
    public long leftTime(TimeUnit unit) {
        if(hasExpired()) return -1;
        else return unit.convert(endTime-System.nanoTime(), TimeUnit.NANOSECONDS);
    }
    
    public boolean hasExpired() {
        return (endTime-System.nanoTime() < 0);
    }
    
    public void sleepLeftTime() throws InterruptedException {
        TimeUnit.NANOSECONDS.sleep(endTime);
    }
}
