/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.lib.annotation.Enity;
import net.alternativmud.lib.persistence.PersistenceManager;
import net.alternativmud.security.PasswordHasher;

/**
 *
 * @author maciek
 */
@Enity
public class User {
    private String passwordHash;
    private String selectedCharacter = "";
    private String login;
    private boolean admin = false;

    public User() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isPasswordCorrect(String password) {
        return passwordHash.equals(PasswordHasher.generateHash(password));
    }

    public void setPassword(String password) {
        this.passwordHash = PasswordHasher.generateHash(password);
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getSelectedCharacter() {
        return selectedCharacter;
    }

    public void setSelectedCharacter(String selectedCharacter) {
        this.selectedCharacter = selectedCharacter;
    }

    @Override
    public String toString() {
        return "User{" + "login=" + login + ", admin=" + admin + '}';
    }

    public static class Manager {
        private final ArrayList<User> list = new ArrayList<>();
        
        public Manager(PersistenceManager persistenceManager) {
            try {
                list.addAll(persistenceManager.loadCollection("users", new User [] {}));
            } catch (IOException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (list.size() < 1) {
                User u1 = new User();
                u1.setLogin("admin");
                u1.setPassword("admin");
                u1.setAdmin(true);
                list.add(u1);

                User u2 = new User();
                u2.setLogin("tester");
                u2.setPassword("tester");
                list.add(u2);
                
                User mob_statue = new User();
                mob_statue.setLogin("mob_statue");
                mob_statue.setPassword("mob_statue");
                list.add(mob_statue);
                
                User mob_ninja = new User();
                mob_ninja.setLogin("mob_ninja");
                mob_ninja.setPassword("mob_ninja");
                list.add(mob_ninja);

                Logger.getLogger(User.Manager.class.getName())
                        .info("Added 'admin', 'tester', 'mob_statue', 'mob_ninja', users to database.");
            } else {
                Logger.getLogger(User.Manager.class.getName())
                        .log(Level.INFO, "Users count: {0}", list.size());
            }
            
            //Musimy miec przynajmniej domyslnego uzytkownika, ktory bedzie
            //przechowywal bezpanskie Charactery.
            if(getUser("default") == null) {
                User defaultUser = new User();
                defaultUser.setLogin("default");
                defaultUser.setPassword("default#user#password#1234567890");
                list.add(defaultUser);
            }
        }
        
        public User getUser(String login) {
            for(User u : list) {
                if(u.getLogin().equals(login))
                    return u;
            }
            return null;
        }
        
        public List<User> getUsers() {
            return list;
        }

        public User authenticate(String login, String password) {
            for(User u : list) {
                if(u.getLogin().equals(login) && u.isPasswordCorrect(password))
                    return u;
            }
            return null;
        }
        
        public void save(PersistenceManager persistenceManager) throws IOException {
            persistenceManager.saveCollection("users", list);
        }
    }
}
