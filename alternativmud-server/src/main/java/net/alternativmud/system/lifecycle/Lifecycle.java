/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.lifecycle;

import com.google.common.eventbus.EventBus;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.logic.time.TimeValue;

/**
 * Ta klasa odpowiada za zarządzanie cyklem życia aplikacji.
 *
 * @author jblew
 */
public class Lifecycle {
    private TimeValue timeout = new TimeValue(6, TimeUnit.SECONDS);
    private AtomicReference<State> state = new AtomicReference<>(State.OFF);
    private final Queue<RunnableTask> bootstrapQueue = new LinkedBlockingQueue<>();
    private final Queue<RunnableTask> shutdownQueue = new LinkedBlockingQueue<>();
    private final EventBus ebus;
    private final boolean doSystemExit;

    public Lifecycle(EventBus ebus, boolean doSystemExit) {
        this.ebus = ebus;
        this.doSystemExit = doSystemExit;
    }
    
    public Lifecycle(EventBus ebus) {
        this.ebus = ebus;
        this.doSystemExit = true;
    }

    /**
     * Dodaje zadanie do kolejki.
     */
    public void registerBootstrapTask(RunnableTask t) {
        bootstrapQueue.add(t);
    }

    /**
     * Dodaje zadanie do kolejki.
     */
    public void registerShutdownTask(RunnableTask t) {
        shutdownQueue.add(t);
    }

    public State getState() {
        return state.get();
    }

    private void changeState(State newState) {
        State oldState = state.get();
        state.set(newState);
        ebus.post(new LifecycleStateChanged(oldState, newState));
    }

    public void bootstrap() {
        changeState(State.BOOTSTRAP);
        Logger.getLogger(getClass().getName()).info("Lifecycle.bootstrap(): Running tasks from bootstrap queue.");
        while (!bootstrapQueue.isEmpty()) {
            RunnableTask r = bootstrapQueue.poll();

            if (r.shouldBeExecuted()) {
                try {
                    r.execute();
                    Logger.getLogger(getClass().getName()).log(Level.INFO, " [bootstrap] {0}: [OK]", r.getDescription());
                } catch (Exception ex) {
                    /*
                     * Jeżeli zadanie zgłasza wyjątek, aplikacja jest przechodzi
                     * do wyłączania.
                     */
                    Logger.getLogger(Lifecycle.class.getName()).log(Level.SEVERE, "[bootstrap] " + r.getDescription() + ": [EXCEPTION] Shutting application down.", ex);
                    shutdown();
                    return;
                }
            }
        }
        changeState(State.ON);
    }

    public void shutdown() {
        changeState(State.SHUTDOWN);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> f = executor.submit(new Runnable() {

            @Override
            public void run() {
                Logger.getLogger(getClass().getName()).info("Lifecycle.shutdown(): Running tasks from shutdown queue.");
                while (!shutdownQueue.isEmpty()) {
                    RunnableTask r = shutdownQueue.poll();

                    if (r.shouldBeExecuted()) {
                        try {
                            r.execute();
                            Logger.getLogger(getClass().getName()).log(Level.INFO, " [shutdown] {0}: [OK]", r.getDescription());
                        } catch (Exception ex) {
                            Logger.getLogger(Lifecycle.class.getName()).log(Level.SEVERE, "[shutdown] " + r.getDescription() + ": [EXCEPTION]", ex);
                        }
                    }
                }
                changeState(State.OFF);
            }
        }, true);
        boolean ok = false;
        try {
                if(f.get(timeout.value, timeout.unit) == true) {
                    ok = true;
                }
        } catch (InterruptedException ex) {
            //do nothing on interrputed exception
        } catch(TimeoutException | ExecutionException ex) {
            Logger.getLogger(Lifecycle.class.getName()).log(Level.WARNING, "Exception in shutdown.", ex);
        }
        if(!ok) executor.shutdownNow();
        
        if(doSystemExit) System.exit(0);
    }

    /**
     * Ta klasa jest wysyłana, gdy zmieni się stan cyklu życia aplikacji.
     */
    public static class LifecycleStateChanged {

        public final State oldState;
        public final State newState;

        private LifecycleStateChanged(State oldState, State newState) {
            this.oldState = oldState;
            this.newState = newState;
        }

        @Override
        public String toString() {
            return "Lifecycle state change: from " + oldState.name() + " to " + newState.name() + ".";
        }
    }

    /**
     * Cykl życiowy aplikacji: OFF -> BOOTSTRAP -> ON \----------\--> SHUTDOWN
     * -> OFF
     */
    public static enum State {
        OFF, BOOTSTRAP, ON, SHUTDOWN
    }
}
