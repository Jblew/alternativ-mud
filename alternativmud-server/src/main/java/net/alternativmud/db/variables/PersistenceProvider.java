/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.db.variables;

/**
 *
 * @author teofil
 */
interface PersistenceProvider {
    public boolean connect(String driverClass, String url);
    public void close();
    
    public String getValue(String key);
    public void setValue(String key, String value);
}
