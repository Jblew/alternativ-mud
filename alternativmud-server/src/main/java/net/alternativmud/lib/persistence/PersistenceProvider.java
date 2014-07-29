/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.persistence;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author jblew
 */
public interface PersistenceProvider {
    /**
     * Saves objects collection
     * @param <T>
     * @param name
     * @param collection
     * @throws IOException 
     */
    public <T> void saveCollection(String name, ArrayList<T> collection) throws IOException;
    /**
     * Loads objects collection.
     * @param <T>
     * @param name
     * @param sampleArray
     * @return
     * @throws IOException 
     */
    public <T> ArrayList<T> loadCollection(String name, T [] sampleArray) throws IOException;
    /**
     * Saves single object
     * @param <T>
     * @param name entry name
     * @param object object to save
     * @throws IOException 
     */
    public <T> void saveObject(String name, T object) throws IOException;
    /**
     * Updates given object with loaded data.
     * @param <T>
     * @param name entry name
     * @param objectToUpdate
     */
    public <T> T loadObject(String name, T objectToUpdate);
    /**
     * Shutdowns this provider
     */
    public void shutdown();
}
