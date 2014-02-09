/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.db.variables;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * H2, because there is MERGE command. In mysql it should be renamed to REPLACE
 * @author teofil
 */
class H2SQLPersistenceProvider implements PersistenceProvider {

    public static final String TABLE_NAME = "variables";
    private final AtomicReference<Connection> connectionRef = new AtomicReference<Connection>(null);
    private PreparedStatement getValueStatement = null;
    private PreparedStatement setValueStatement = null;
    private final Lock lock = new ReentrantLock();

    public H2SQLPersistenceProvider() {
    }

    @Override
    public boolean connect(String driverClass, String url) {
        lock.lock();
        try {
            try {
                Class.forName(driverClass);

                if (connectionRef.get() != null) {
                    Logger.getLogger(H2SQLPersistenceProvider.class.getName()).log(Level.WARNING, "SQLPersistenceProvider: Already connected.");
                    return true;
                } else {
                    try {
                        Connection c = DriverManager.getConnection(url);
                        try (Statement createTableStatement = c.createStatement()) {
                            createTableStatement.execute("CREATE TABLE IF NOT EXISTS `"+TABLE_NAME+"` ("
                                    + "`key` varchar(250) NOT NULL PRIMARY KEY,"
                                    + "`value` varchar(250)"
                                    + ") engine = InnoDB");
                        }
                        
                        getValueStatement = c.prepareStatement("SELECT value FROM " + TABLE_NAME + " WHERE key= ? ; ");
                        setValueStatement = c.prepareStatement("MERGE INTO " + TABLE_NAME + " (key, value) VALUES (?, ?) ; ");
                        
                        connectionRef.set(c);
                        return true;
                    } catch (SQLException ex) {
                        Logger.getLogger(H2SQLPersistenceProvider.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(H2SQLPersistenceProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            if(getValueStatement != null) {
                try {
                    getValueStatement.close();
                } catch (SQLException ex) {
                    Logger.getLogger(H2SQLPersistenceProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
                getValueStatement = null;
            }
            
            if(setValueStatement != null) {
                try {
                    setValueStatement.close();
                } catch (SQLException ex) {
                    Logger.getLogger(H2SQLPersistenceProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
                setValueStatement = null;
            }
            
            Connection c = connectionRef.getAndSet(null);
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException ex) {
                    Logger.getLogger(H2SQLPersistenceProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getValue(String key) {
        lock.lock();
        try {
            Connection c = connectionRef.get();
            if (c == null) {
                return null;
            }
            if (getValueStatement == null) {
                Logger.getLogger(H2SQLPersistenceProvider.class.getName()).log(Level.SEVERE, "SQLPersistenceProvider: getValueStatement is null!");
                return null;
            }
            try {
                getValueStatement.setString(1, key);
                try (ResultSet rs = getValueStatement.executeQuery()) {
                    return rs.getString(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(H2SQLPersistenceProvider.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setValue(String key, String value) {
        lock.lock();
        try {
            Connection c = connectionRef.get();
            if (c == null) {
            }
            if (setValueStatement == null) {
                Logger.getLogger(H2SQLPersistenceProvider.class.getName()).log(Level.SEVERE, "SQLPersistenceProvider: setValueStatement is null!");
            }
            try {
                setValueStatement.setString(1, key);
                setValueStatement.setString(2, value);
                setValueStatement.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(H2SQLPersistenceProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        } finally {
            lock.unlock();
        }
    }

}
