package net.alternativmud.system;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author noosekpl
 */
public class Tick implements Runnable {

    private final ScheduledExecutorService scheduler;
    AtomicInteger minitick = new AtomicInteger(0);
    AtomicInteger tick = new AtomicInteger(0);

    Tick() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this, 4, 4, TimeUnit.SECONDS);
    }
    
    public int getTime() {
        return tick.get();
    }
    
    @Override
    public void run() {
        minitick.incrementAndGet();
        if(minitick.get() % 15 == 0) { 
            tick.incrementAndGet();
            if(tick.get() == 25) tick.set(1);
            System.out.println("TICK! ["+tick+"]");
        }
    }
}
