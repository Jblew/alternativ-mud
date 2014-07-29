package net.alternativmud.gclient;

import com.google.common.eventbus.EventBus;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import net.alternativmud.framework.ServiceManager;
import net.alternativmud.gclient.actions.Connect;
import net.alternativmud.gclient.forms.FormFrame;
import net.alternativmud.system.lifecycle.Lifecycle;

public class App {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static App INSTANCE;
    private final EventBus systemBus = new EventBus("system");
    private final Lifecycle lifecycle;
    private final ServiceManager serviceManager;
    private App() {
        lifecycle = new Lifecycle(systemBus);
        serviceManager = new ServiceManager(systemBus);
    }

    public void start() {
        lifecycle.bootstrap();
    }

    public EventBus getSystemEventBus() {
        return systemBus;
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public static App getApp() {
        if (INSTANCE == null) {
            Logger.getLogger(net.alternativmud.App.class.getName()).severe("App is NOT initialized. Panic sutdown (Code: -1)!");
            System.exit(-1);
        }
        return INSTANCE;
    }

    public static void main(String[] args) {
        //Set Nimbus Look&Feel
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        FormFrame formFrame = new FormFrame();
        formFrame.setTitle("AlterntivMUD");
        formFrame.setVisible(true);
        executor.execute(new Connect(executor, formFrame));

        //INSTANCE = new App();
        //INSTANCE.start();
    }

    public void stop() {
        getLifecycle().shutdown();
    }

}
