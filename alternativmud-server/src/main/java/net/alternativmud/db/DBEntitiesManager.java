/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.Config;
import net.alternativmud.logic.User;
import net.alternativmud.system.lifecycle.RunnableTask;

/**
 *
 * @author teofil
 */
public class DBEntitiesManager {

    private final AtomicReference<ConnectionSource> connectionSourceRef = new AtomicReference<ConnectionSource>(null);
    private final Config config;

    private final Lock usersDaoLock = new ReentrantLock();
    private Dao<User, String> usersDao = null;

    public DBEntitiesManager(Config config) {
        this.config = config;
    }
    
    public void connect() {
        if (config.getDatabaseConfig().enabled) {
            ConnectionSource connectionSource = null;
            try {
                if (!config.getDatabaseConfig().getUsername().isEmpty()) {
                    connectionSource = new JdbcConnectionSource(config.getDatabaseConfig().getUrl(),
                            config.getDatabaseConfig().getUsername(),
                            config.getDatabaseConfig().getPassword()
                    );
                } else {
                    connectionSource = new JdbcConnectionSource(config.getDatabaseConfig().getUrl());
                }

                synchronized (connectionSourceRef) {
                    connectionSourceRef.set(connectionSource);

                    usersDaoLock.lock();
                    try {
                        usersDao = DaoManager.createDao(connectionSource, User.class);
                        if(!usersDao.isTableExists()) {
                            Logger.getLogger(getClass().getName()).info("Creating table for User.class");
                            TableUtils.createTable(connectionSource, User.class);
                        }
                        if(usersDao.countOf() == 0) {
                            for(User u : User.createInitialUsers()) {
                                usersDao.create(u);
                            }
                        }
                    } finally {
                        usersDaoLock.unlock();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBEntitiesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Logger.getLogger(getClass().getName()).warning("Database is disabled in config");
        }
    }
    
    public void close() {
        if (connectionSourceRef.get() != null) {
            try {
                connectionSourceRef.get().close();
            } catch (SQLException ex) {
                Logger.getLogger(DBEntitiesManager.class.getName()).log(Level.SEVERE, "Exception while closing DB ConnectionSource", ex);
            }
            connectionSourceRef.set(null);
        }
    }
    
    public boolean isConnected() {
        return (connectionSourceRef.get() != null);
    }

    public Dao<User, String> getUsersDao() {
        usersDaoLock.lock();
        try {
            return usersDao;
        } finally {
            usersDaoLock.unlock();
        }
    }

    public static class DatabaseConfig {

        private boolean enabled = true;
        private String url = "jdbc:h2:mem:trialdb";
        private String username = "";
        private String password = "";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
