/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.lifecycle;

import net.alternativmud.system.lifecycle.Lifecycle;
import net.alternativmud.system.lifecycle.RunnableTask;
import com.google.common.eventbus.EventBus;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;

/**
 *
 * @author jblew
 */
public class LifecycleTest extends TestCase {
    private final Lifecycle lifecycle = new Lifecycle(new EventBus("test"), false);

    public LifecycleTest(String testName) {
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
    
    //public static Test suite() {
    //    return AppTest.suite();
    //}

    /**
     * Testuję czy dodawanie zadań działa prawidłowo.
     */
    public void testRegisterBootstrapTask() {
        //jeśli wykonają się wszystkie zadania, licznik będzie miał wartość 5.
        final AtomicInteger counter = new AtomicInteger(0);
        lifecycle.registerBootstrapTask(new RunnableTask() {

            @Override
            public String getDescription() {
                return "Zadanie pierwsze";
            }

            @Override
            public boolean shouldBeExecuted() {
                return true;
            }

            @Override
            public void execute() throws Exception {
                counter.incrementAndGet();
            }
        });

        lifecycle.registerBootstrapTask(new RunnableTask() {

            @Override
            public String getDescription() {
                return "Zadanie drugie";
            }

            @Override
            public boolean shouldBeExecuted() {
                return true;
            }

            @Override
            public void execute() throws Exception {
                counter.incrementAndGet();

                lifecycle.registerBootstrapTask(new RunnableTask() {

                    @Override
                    public String getDescription() {
                        return "Zadanie trzecie, dodane w trakcie wykonywania drugiego";
                    }

                    @Override
                    public boolean shouldBeExecuted() {
                        return true;
                    }

                    @Override
                    public void execute() throws Exception {
                        counter.incrementAndGet();
                    }
                });
            }
        });

        lifecycle.registerBootstrapTask(new RunnableTask() {

            @Override
            public String getDescription() {
                return "Zadanie czwarte";
            }

            @Override
            public boolean shouldBeExecuted() {
                return true;
            }

            @Override
            public void execute() throws Exception {
                counter.incrementAndGet();
                lifecycle.registerShutdownTask(new RunnableTask() {

                    @Override
                    public String getDescription() {
                        return "Zadanie piąte, uruchamiane podczas wyłączania";
                    }

                    @Override
                    public boolean shouldBeExecuted() {
                        return true;
                    }

                    @Override
                    public void execute() throws Exception {
                        counter.incrementAndGet();

                    }
                });
            }
        });

        lifecycle.bootstrap();
        lifecycle.shutdown();

        assertEquals("Nie wykonało się wszystkie pięć zadań.", 5, counter.get());
    }
    
    /*
     * W tym teście zadanie zgłasza wyjątek, co powinno uruchomić wyłączanie cyklu i wykonanie zadania wyłączającego.
     */
    public void testShuttingDownOnException() {
       
        final AtomicBoolean executed1 = new AtomicBoolean(false);
        final AtomicBoolean executed2 = new AtomicBoolean(false);
        final AtomicBoolean executedShutdown = new AtomicBoolean(false);
        
        lifecycle.registerShutdownTask(new RunnableTask() {

            @Override
            public String getDescription() {
                return "Zadanie wykonywane przy wyłączaniu, powinno się wykonać.";
            }

            @Override
            public boolean shouldBeExecuted() {
                return true;
            }

            @Override
            public void execute() throws Exception {
                executedShutdown.set(true);
            }
        });
        
        lifecycle.registerBootstrapTask(new RunnableTask() {

            @Override
            public String getDescription() {
                return "Zadanie pierwsze, powinno się wykonać i zgłosić wyjątek.";
            }

            @Override
            public boolean shouldBeExecuted() {
                return true;
            }

            @Override
            public void execute() throws Exception {
                executed1.set(true);
                throw new Exception();
            }
        });
        
        lifecycle.registerBootstrapTask(new RunnableTask() {

            @Override
            public String getDescription() {
                return "Zadanie drugie, nie powinno się wykonać.";
            }

            @Override
            public boolean shouldBeExecuted() {
                return true;
            }

            @Override
            public void execute() throws Exception {
                executed2.set(true);
            }
        });
        
        lifecycle.bootstrap();
        
        assertTrue("Po wystąpieniu wyjątku w uruchamianej aplikacji cykl życiowy nie rozpoczął wyłączania aplikacji.", executedShutdown.get());
        assertTrue("Zadania wogóle nie zostały wykonane", executed1.get());
        assertFalse("Po wyłączeniu aplikacji, cykl życiowy nadal uruchamia początkowe zadania.", executed2.get());
    }
}
