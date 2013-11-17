/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ta klasa generuje kolejne liczby dla wielu wątków, mogące służyć jako
 * wewnętrzne identyfikatory, bezpieczne dla jednej sesji. Te identyfikatory,
 * warto np. dodawać do nazw wątków, tak, aby nie powtarzały się one.
 *
 * @author jblew
 */
public class IdManager {

    private static final AtomicInteger id = new AtomicInteger(0);
    //private static final AtomicLong multiSessionLong = new AtomicLong(0);

    public IdManager() throws IOException {
        /*File f = new File("data/long_sequence.id");
        if (f.exists()) {
            try(FileInputStream fis = new FileInputStream(f)) {
                
            }
            multiSessionLong.set();
        } else {
            f.createNewFile();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                save();
            }
        }));*/
    }

    /*public long getMultiSessionLong() {
        CommandResult res = db.doEval("function() {"
                + "var o = db.settings.find({key: \"long_id_sequence\"}).next();"
                + "o.value++;"
                + "db.settings.save(o);"
                + "return o;"
                + "}");
        if (!res.ok()) {
            throw new DatabaseManager.QueryException(res.getErrorMessage());
        }
        //System.out.println("//.../"+res.toString());
        //System.out.println("//////"+res.get("retval").toString()+"::::"+res.get("retval").getClass().getName());
        return ((BasicDBObject) res.get("retval")).getLong("value");
    }*/

    public static int getSessionSafe() {
        return id.incrementAndGet();
    }

    /*private void save() {
        try {
            File f = new File("data/long_sequence.id");
            if (!f.exists()) {
                f.createNewFile();
            }
            try (FileOutputStream fos = new FileOutputStream(f, false);) {
                fos.write(((Long) multiSessionLong.get()).toString().getBytes(Charsets.UTF_8));
            }
        } catch (IOException ex) {
            Logger.getLogger(IdManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
}
