package net.alternativmud.logic.world.characters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.lib.persistence.PersistenceManager;
import net.alternativmud.logic.geo.Coordinates3l;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author noosekpl
 */
public class UCharacter {
    private String name;
    private String owner = "defalut";//Wlasciciel postaci
    private Race race = Race.HUMAN; //Rasa
    private Position position = Position.STAND;
    private String fight = "";
    private Coordinates3l locationCoordinates = Coordinates3l.CENTER;
    private float strength;
    private float condition;
    private float dexterity;
    private float luck;
    private float maximalHealthPoints;
    private float healthPoints;
    private float armorClass;

    public UCharacter() {
        name = "null";
        strength = 1f;
        condition = 1f;
        dexterity = 1f;
        luck = 1f;
        setMaximalHealthPoints();
        setHealthPoints(maximalHealthPoints);
    }
    
    public void dealDamage(UCharacter character, int damage) {
        for(int i = 0; i <= damage+1; i++) {
        healthPoints--;
        Random random = new Random();
        if(random.nextFloat()*500*condition-luck+1 < 1) condition++;
        }
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String text) {
        name = text;
    }

    public Coordinates3l getLocationCoordinates() {
        return locationCoordinates;
    }

    public void setLocationCoordinates(Coordinates3l locationCoordinates) {
        this.locationCoordinates = locationCoordinates;
    }
    
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public float getStrength() {
        return strength;
    }
    
    public void setStrength(float value) {
        strength = value;
    }
    
    public float getCondition() {
        return condition;
    }
    
    public void setCondition(float value) {
        condition = value;
    }

    public float getDexterity() {
        return dexterity;
    }
    
    public void setDexterity(float value) {
        dexterity = value;
    }
    
    public float getLuck() {
        return luck;
    }
    
    public void setLuck(float value) {
        luck = value;
    }
    
    public float getHealthPoints() {
        return healthPoints;
    }
    
    public float getMaximalHealthPoints() {
        setMaximalHealthPoints();
        return maximalHealthPoints;
    }
    
    public void setMaximalHealthPoints() {
        maximalHealthPoints = (float) (condition * 1.2 + luck); //ustawianie życia wg. mojego widzimisie
    }
    
    public void setHealthPoints(float value) {
        healthPoints = value;
    }

    public String getOwnerName() {
        return owner;
    }

    public void setOwnerName(String ownerName) {
        this.owner = ownerName;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
    
    @JsonIgnore
    public boolean isInFight() {
        return !fight.isEmpty();
    }

    @JsonIgnore
    public String getFight() {
        return fight;
    }

    @JsonIgnore
    public void setFight(String fight) {
        this.fight = fight;
    }
    
    public static class Manager {
        private final ArrayList<UCharacter> list = new ArrayList<>();
        
        public Manager(PersistenceManager persistenceManager) {
            try {
                list.addAll(persistenceManager.loadCollection("characters", new UCharacter [] {}));
            } catch (IOException ex) {
                Logger.getLogger(UCharacter.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (list.size() < 1) {
                UCharacter c1 = new UCharacter();
                c1.setName("harald");
                c1.setOwnerName("admin");
                list.add(c1);
                
                UCharacter c2 = new UCharacter();
                c2.setName("elea");
                c2.setRace(Race.ELF);
                c2.setOwnerName("admin");
                list.add(c2);

                UCharacter c3 = new UCharacter();
                c3.setName("bofgal");
                c3.setRace(Race.ELF);
                c3.setOwnerName("tester");
                list.add(c3);
            }
        }
        
        public UCharacter getCharacter(String name) {
            for(UCharacter c : list) {
                if(c.getName().equals(name))
                    return c;
            }
            return null;
        }
        
        public List<UCharacter> getCharacters() {
            return list;
        }
        
        public List<UCharacter> getCharactersForUser(String login) {
            ArrayList<UCharacter> out = new ArrayList<>();
            for(UCharacter c : list) {
                if(c.getOwnerName().equals(login)) out.add(c);
            }
            return out;
        }
        
        public void save(PersistenceManager persistenceManager) throws IOException {
            persistenceManager.saveCollection("characters", list);
        }
    }
}
