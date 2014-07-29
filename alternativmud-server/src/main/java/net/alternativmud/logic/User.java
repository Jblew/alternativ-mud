/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
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
@DatabaseTable(tableName = "users")
public class User {
    @DatabaseField(canBeNull = false)
    private String passwordHash;
    
    @DatabaseField(canBeNull = true)
    private String selectedCharacter = "";
    
    @DatabaseField(id = true, canBeNull = false, unique = true)
    private String login;
    
    @DatabaseField(canBeNull = false, defaultValue = "false")
    private boolean admin = false;
    
    @DatabaseField(canBeNull = false, defaultValue = "false")
    private boolean online = false;

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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "User{" + "login=" + login + ", admin=" + admin + '}';
    }
    
    public static User[] createInitialUsers() {
        User[] initialUsers = new User[4];
        User u1 = new User();
        u1.setLogin("admin");
        u1.setPassword("admin");
        u1.setAdmin(true);
        initialUsers[0] = u1;

        User u2 = new User();
        u2.setLogin("tester");
        u2.setPassword("tester");
        initialUsers[1] = u2;

        User mob_statue = new User();
        mob_statue.setLogin("mob_statue");
        mob_statue.setPassword("mob_statue");
        initialUsers[2] = mob_statue;

        User mob_ninja = new User();
        mob_ninja.setLogin("mob_ninja");
        mob_ninja.setPassword("mob_ninja");
        initialUsers[3] = mob_ninja;
        
        return initialUsers;
    }
}
