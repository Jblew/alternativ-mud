/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author jblew
 */
public class FilePersistenceProvider implements PersistenceProvider {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public <T> void saveCollection(String name, ArrayList<T> collection) throws IOException {
        //Zobacz, to jest specjalny rodzaj try (dodany w javie 7), który
        //automatycznie zamyka utworzone w nawiasie () zasoby (tutaj FileInputStream)
        File f = new File("data/db/" + name + ".json");
        if (!f.exists()) {
            f.createNewFile();
        }

        try (FileOutputStream fos = new FileOutputStream(f, false)) {
            MAPPER.writeValue(fos, collection.toArray());
        }
    }

    @Override
    public <T> ArrayList<T> loadCollection(String name, T[] sampleArray) throws IOException {
        //Zobacz, to jest specjalny rodzaj try (dodany w javie 7), który
        //automatycznie zamyka utworzone w nawiasie () zasoby (tutaj FileInputStream)
        try (FileInputStream fis = new FileInputStream("data/db/" + name + ".json");) {
            ArrayList<T> out = new ArrayList<>();
            List<T> lst = (List<T>) Arrays.asList(MAPPER.readValue(fis, sampleArray.getClass()));

            //System.out.println(lst.getClass());
            out.addAll(lst);
            return out;
        } catch (FileNotFoundException ex) {
            return new ArrayList<>();
        }
    }

    @Override
    public <T> void saveObject(String name, T object) throws IOException {
        File f = new File("data/db/" + name + ".json");
        if (!f.exists()) {
            f.createNewFile();
        }

        try (FileOutputStream fos = new FileOutputStream(f, false)) {
            MAPPER.writeValue(fos, object);
        }
    }

    @Override
    public <T> T loadObject(String name, T objectToUpdate) {
        try (FileInputStream fis = new FileInputStream("data/db/" + name + ".json");) {
            return MAPPER.readerForUpdating(objectToUpdate).readValue(fis);
        } catch (FileNotFoundException ex) {
            return objectToUpdate;
        }catch (IOException ex) {
            Logger.getLogger(FilePersistenceProvider.class.getName())
                    .log(Level.WARNING, "Cannot load object", ex);
            return objectToUpdate;
        }
    }

    @Override
    public void shutdown() {
    }
}
