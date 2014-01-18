/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud;

import com.google.common.eventbus.EventBus;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.db.DBEntitiesManager;
import net.alternativmud.framework.ServiceManager;
import net.alternativmud.lib.IdManager;
import net.alternativmud.lib.persistence.FilePersistenceProvider;
import net.alternativmud.lib.persistence.PersistenceManager;
import net.alternativmud.logic.GamesManager;
import net.alternativmud.logic.User;
import net.alternativmud.logic.world.World;
import net.alternativmud.system.bootstrap.*;
import net.alternativmud.system.lifecycle.Lifecycle;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.commons.daemon.DaemonInitException;

/**
 * Ta klasa to główna klasa muda. Na start jest ładowana jej metoda main(),
 * która konstruuje obiekt App i zapisuje go do INSTANCE, umożliwiając pobranie
 * go potem w sposób statyczny. Klasa App jest zatem singletonem.
 *
 * Alternativ-mud może być uruchamiany na dwa sposoby. Jako zwykła aplikacja
 * standalone albo jako Daemon przy użyciu programu jsvc.
 *
 * @author jblew, NoosekPL
 */
public class App {
    private static App INSTANCE;
    private final EventBus systemBus = new EventBus("system");
    private final Lifecycle lifecycle;
    private final ServiceManager serviceManager;
    private final PersistenceManager persistenceManager;
    private final IdManager idManager;
    private final DBEntitiesManager entitiesManager;
    private final GamesManager gamesManager;
    private final Config config;
    private final World world;

    public App(String configFile) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, IOException {
        try {
            new PrepareLogger().execute();
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(configFile == null || configFile.isEmpty()) {
            if(new File(StaticConfig.DEFAULT_CONFIG_FILE).exists()) {
                config = Config.LoadConfig(StaticConfig.DEFAULT_CONFIG_FILE);
            }
            else {
                config = new Config();
                config.save(StaticConfig.DEFAULT_CONFIG_FILE);
            }
        }
        else {
            if(new File(configFile).exists()) {
                config = Config.LoadConfig(configFile);
            }
            else {
                Logger.getLogger(App.class.getName()).warning("Could not find specified config file ("+configFile+")");
                config = new Config();
            }
        }
        
        lifecycle = new Lifecycle(systemBus);
        serviceManager = new ServiceManager(systemBus);
        persistenceManager = new PersistenceManager(new FilePersistenceProvider());
        idManager = new IdManager();
        entitiesManager = new DBEntitiesManager(config);
        gamesManager = new GamesManager();
        world = new World(config, persistenceManager, entitiesManager);

        //initialize tasks
        lifecycle.registerBootstrapTask(new InitBusDebug());
        lifecycle.registerBootstrapTask(new DisplayIntro());
        lifecycle.registerBootstrapTask(new CreateFileTree());
        lifecycle.registerBootstrapTask(new InitTester());
        lifecycle.registerBootstrapTask(new InitSecurity());
        lifecycle.registerBootstrapTask(new InitGamesManager());

        //initialize resources
        lifecycle.registerBootstrapTask(new InitPersistence());
        lifecycle.registerBootstrapTask(new ConnectToDatabase());
        
        //start tasks
        lifecycle.registerBootstrapTask(new StartRemoteAdminServer());
        lifecycle.registerBootstrapTask(new StartTelnetServer());
        lifecycle.registerBootstrapTask(new StartEBusServer());
        lifecycle.registerBootstrapTask(new StartWebServer());
        lifecycle.registerBootstrapTask(new StartUnityServer());
        lifecycle.registerBootstrapTask(new StartConsoleReader());
        

        //runtime tasks
        lifecycle.registerBootstrapTask(new RunTests());
        
        System.gc(); 
    }
    private DaemonController daemonController = null;

    /**
     * Daemon init
     */
    public void init(DaemonContext dc) throws DaemonInitException, Exception {
        daemonController = dc.getController();
        INSTANCE = this;
    }

    /**
     * Daemon init
     */
    public void init(String[] args) throws DaemonInitException, Exception {
        daemonController = null;
        INSTANCE = this;
    }

    /**
     * Daemon start
     */
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

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public IdManager getIdManager() {
        return idManager;
    }

    public DBEntitiesManager getEntitiesManager() {
        return entitiesManager;
    }

    public GamesManager getGamesManager() {
        return gamesManager;
    }

    public Config getConfig() {
        return config;
    }

    public World getWorld() {
        return world;
    }

    public static App getApp() {
        if (INSTANCE == null) {
            Logger.getLogger(App.class.getName()).severe("App is NOT initialized. Panic sutdown (Code: -1)!");
            System.exit(-1);
        }
        return INSTANCE;
    }

    public static void main(String[] args) throws Exception {
        if(args.length > 1) {
            INSTANCE = new App(args[1]);
        }
        else {
           INSTANCE = new App(null); 
        }
        INSTANCE.start();
    }

    /**
     * This metod allows to start this App inside other app.
     */
    public static App embeddedInit() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, IOException {
        INSTANCE = new App(null);
        new Thread(new Runnable() {

            @Override
            public void run() {
                INSTANCE.start();
            }
        }, "alternativmud-embeded-" + IdManager.getSessionSafe()).start();

        return INSTANCE;
    }

    /**
     * Daemon stop
     */
    public void stop() {
        getLifecycle().shutdown();
    }

    /**
     * Daemon destroy
     */
    public void destroy() {
    }
}
