/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.persistence;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Ta klasa będzie używana przy testach, aby uniknąć używania zewnętrznych
 * zewnętrznych zasobów i przyspieszyć wykonywanie testów.
 *
 * @author jblew
 */
public class NullPersistenceProvider implements PersistenceProvider {

    @Override
    public <T> void saveCollection(String name, ArrayList<T> collection) throws IOException {
    }

    @Override
    public <T> ArrayList<T> loadCollection(String name, T[] sampleArray) throws IOException {
        return new ArrayList<>();
    }

    @Override
    public void shutdown() {
    }

    @Override
    public <T> void saveObject(String name, T object) throws IOException {
    }

    @Override
    public <T> T loadObject(String name, T objectToUpdate) {
        return objectToUpdate;
    }
}
