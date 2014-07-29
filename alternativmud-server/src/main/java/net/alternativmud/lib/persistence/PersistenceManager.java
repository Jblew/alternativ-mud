/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.persistence;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Ta klasa odpowiada za zapewnienie obiektom trwałości (zapisywanie ich, przy
 * wyłączaniu).
 * @author jblew
 */
public class PersistenceManager {
    private final PersistenceProvider provider;
    public PersistenceManager(PersistenceProvider provider) {
        this.provider = provider;
    }

    public <T> void saveCollection(String name, ArrayList<T> collection) throws IOException {
        provider.saveCollection(name, collection);
    }

    public <T> ArrayList<T> loadCollection(String name, T [] sampleArray) throws IOException {
        return provider.loadCollection(name, sampleArray);
    }
    
    public <T> void saveObject(String name, T object) throws IOException {
        provider.saveObject(name, object);
    }
    
    public <T> T loadObject(String name, T objectToUpdate) {
        return provider.loadObject(name, objectToUpdate);
    }
    
    public void shutdown() {
        
    }
}
