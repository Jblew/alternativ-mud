package net.alternativmud.logic.world.characters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.game3d.UMARecipes;
import net.alternativmud.lib.persistence.PersistenceManager;
import net.alternativmud.logic.geo.Coordinates3l;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author noosekpl
 */
public class UCharacter {
    private String umaPackedRecipe;
    private String name;
    private String owner = "default";//Wlasciciel postaci
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
        setUmaPackedRecipe(UMARecipes.DEFAULT_RECIPES[(int)(Math.floor(Math.random()*UMARecipes.DEFAULT_RECIPES.length))]);
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

    public String getUmaPackedRecipe() {
        return umaPackedRecipe;
    }

    public void setUmaPackedRecipe(String umaPackedRecipe) {
        this.umaPackedRecipe = umaPackedRecipe;
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
                UCharacter c2 = new UCharacter();
                c2.setName("elea");
                c2.setRace(Race.ELF);
                c2.setOwnerName("admin");
                c2.setUmaPackedRecipe("{\"packedSlotDataList\":[{\"slotID\":\"FemaleEyes\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"EyeOverlay\",\"colorList\":null,\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"EyeOverlayAdjust\",\"colorList\":[207,195,201,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[64,64,128,128]}]},{\"slotID\":\"FemaleHead_Head\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleHead01\",\"colorList\":[155,154,160,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleEyebrow01\",\"colorList\":[32,15,15,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[384,256,256,64]},{\"overlayID\":\"FemaleLongHair01\",\"colorList\":[213,197,210,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleHead_Mouth\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleHead01\",\"colorList\":[155,154,160,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleEyebrow01\",\"colorList\":[32,15,15,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[384,256,256,64]},{\"overlayID\":\"FemaleLongHair01\",\"colorList\":[213,197,210,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleHead_Eyes\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleHead01\",\"colorList\":[155,154,160,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleEyebrow01\",\"colorList\":[32,15,15,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[384,256,256,64]},{\"overlayID\":\"FemaleLongHair01\",\"colorList\":[213,197,210,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleHead_Nose\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleHead01\",\"colorList\":[155,154,160,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleEyebrow01\",\"colorList\":[32,15,15,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[384,256,256,64]},{\"overlayID\":\"FemaleLongHair01\",\"colorList\":[213,197,210,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleHead_ElvenEars\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"ElvenEars\",\"colorList\":[155,154,160,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleEyelash\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleEyelash\",\"colorList\":[0,0,0,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleTorso\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleBody01\",\"colorList\":[155,154,160,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleUnderwear01\",\"colorList\":[107,42,90,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[0,512,1024,512]},{\"overlayID\":\"FemaleJeans01\",\"colorList\":[55,124,164,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleShirt02\",\"colorList\":[66,28,193,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[0,512,1024,512]}]},{\"slotID\":\"FemaleHands\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleBody01\",\"colorList\":[155,154,160,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleUnderwear01\",\"colorList\":[107,42,90,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[0,512,1024,512]},{\"overlayID\":\"FemaleJeans01\",\"colorList\":[55,124,164,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleShirt02\",\"colorList\":[66,28,193,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[0,512,1024,512]}]},{\"slotID\":\"FemaleFeet\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleBody01\",\"colorList\":[155,154,160,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleUnderwear01\",\"colorList\":[107,42,90,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[0,512,1024,512]},{\"overlayID\":\"FemaleJeans01\",\"colorList\":[55,124,164,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleShirt02\",\"colorList\":[66,28,193,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[0,512,1024,512]}]},{\"slotID\":\"FemaleInnerMouth\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"InnerMouth\",\"colorList\":null,\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleLegs\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleBody01\",\"colorList\":[155,154,160,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleUnderwear01\",\"colorList\":[107,42,90,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[0,512,1024,512]},{\"overlayID\":\"FemaleJeans01\",\"colorList\":[55,124,164,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleShirt02\",\"colorList\":[66,28,193,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[0,512,1024,512]}]},null,{\"slotID\":\"FemaleLongHair01\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleHead01\",\"colorList\":[155,154,160,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleEyebrow01\",\"colorList\":[32,15,15,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[384,256,256,64]},{\"overlayID\":\"FemaleLongHair01\",\"colorList\":[213,197,210,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleLongHair01_Module\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleLongHair01_Module\",\"colorList\":[213,197,210,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]}],\"race\":\"HumanFemale\",\"umaDna\":{},\"packedDna\":[{\"dnaType\":\"UMADnaHumanoid\",\"packedDna\":\"{\\\"height\\\":128,\\\"headSize\\\":128,\\\"headWidth\\\":128,\\\"neckThickness\\\":128,\\\"armLength\\\":128,\\\"forearmLength\\\":128,\\\"armWidth\\\":128,\\\"forearmWidth\\\":128,\\\"handsSize\\\":128,\\\"feetSize\\\":128,\\\"legSeparation\\\":128,\\\"upperMuscle\\\":128,\\\"lowerMuscle\\\":128,\\\"upperWeight\\\":128,\\\"lowerWeight\\\":128,\\\"legsSize\\\":128,\\\"belly\\\":128,\\\"waist\\\":128,\\\"gluteusSize\\\":128,\\\"earsSize\\\":128,\\\"earsPosition\\\":128,\\\"earsRotation\\\":128,\\\"noseSize\\\":128,\\\"noseCurve\\\":128,\\\"noseWidth\\\":128,\\\"noseInclination\\\":128,\\\"nosePosition\\\":128,\\\"nosePronounced\\\":128,\\\"noseFlatten\\\":128,\\\"chinSize\\\":128,\\\"chinPronounced\\\":128,\\\"chinPosition\\\":128,\\\"mandibleSize\\\":128,\\\"jawsSize\\\":128,\\\"jawsPosition\\\":128,\\\"cheekSize\\\":128,\\\"cheekPosition\\\":128,\\\"lowCheekPronounced\\\":128,\\\"lowCheekPosition\\\":128,\\\"foreheadSize\\\":128,\\\"foreheadPosition\\\":128,\\\"lipsSize\\\":128,\\\"mouthSize\\\":128,\\\"eyeRotation\\\":128,\\\"eyeSize\\\":128,\\\"breastSize\\\":230}\"}]}");
                list.add(c2);
                
                UCharacter c1 = new UCharacter();
                c1.setName("harald");
                c1.setOwnerName("admin");
                c1.setUmaPackedRecipe("{\"packedSlotDataList\":[{\"slotID\":\"MaleEyes\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"EyeOverlay\",\"colorList\":null,\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"EyeOverlayAdjust\",\"colorList\":[174,92,95,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[64,64,128,128]}]},{\"slotID\":\"MaleHead_Head\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleHead01\",\"colorList\":[206,206,206,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"MaleHead_PigNose\",\"colorList\":[206,206,206,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[480,192,64,128]}]},{\"slotID\":\"MaleTorso\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleBody01\",\"colorList\":[206,206,206,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleHands\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleBody01\",\"colorList\":[206,206,206,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleInnerMouth\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"InnerMouth\",\"colorList\":null,\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleJeans01\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleJeans01\",\"colorList\":[184,80,30,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleFeet\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleBody01\",\"colorList\":[206,206,206,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleHead_Eyes\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleHead01\",\"colorList\":[206,206,206,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"MaleHead_PigNose\",\"colorList\":[206,206,206,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[480,192,64,128]}]},{\"slotID\":\"MaleHead_ElvenEars\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"ElvenEars\",\"colorList\":[206,206,206,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleHead_Mouth\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleHead01\",\"colorList\":[206,206,206,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"MaleHead_PigNose\",\"colorList\":[206,206,206,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[480,192,64,128]}]},{\"slotID\":\"MaleHead_PigNose\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleHead01\",\"colorList\":[206,206,206,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"MaleHead_PigNose\",\"colorList\":[206,206,206,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[480,192,64,128]}]},null,null,null,null],\"race\":\"HumanMale\",\"umaDna\":{},\"packedDna\":[{\"dnaType\":\"UMADnaHumanoid\",\"packedDna\":\"{\\\"height\\\":128,\\\"headSize\\\":128,\\\"headWidth\\\":128,\\\"neckThickness\\\":128,\\\"armLength\\\":128,\\\"forearmLength\\\":128,\\\"armWidth\\\":128,\\\"forearmWidth\\\":128,\\\"handsSize\\\":128,\\\"feetSize\\\":128,\\\"legSeparation\\\":128,\\\"upperMuscle\\\":128,\\\"lowerMuscle\\\":128,\\\"upperWeight\\\":128,\\\"lowerWeight\\\":128,\\\"legsSize\\\":128,\\\"belly\\\":128,\\\"waist\\\":128,\\\"gluteusSize\\\":128,\\\"earsSize\\\":128,\\\"earsPosition\\\":128,\\\"earsRotation\\\":128,\\\"noseSize\\\":128,\\\"noseCurve\\\":128,\\\"noseWidth\\\":128,\\\"noseInclination\\\":128,\\\"nosePosition\\\":128,\\\"nosePronounced\\\":128,\\\"noseFlatten\\\":128,\\\"chinSize\\\":128,\\\"chinPronounced\\\":128,\\\"chinPosition\\\":128,\\\"mandibleSize\\\":128,\\\"jawsSize\\\":128,\\\"jawsPosition\\\":128,\\\"cheekSize\\\":128,\\\"cheekPosition\\\":128,\\\"lowCheekPronounced\\\":128,\\\"lowCheekPosition\\\":128,\\\"foreheadSize\\\":128,\\\"foreheadPosition\\\":128,\\\"lipsSize\\\":128,\\\"mouthSize\\\":128,\\\"eyeRotation\\\":128,\\\"eyeSize\\\":128,\\\"breastSize\\\":128}\"}]}");
                list.add(c1);

                UCharacter c3 = new UCharacter();
                c3.setName("bofgal");
                c3.setRace(Race.ELF);
                c3.setOwnerName("tester");
                c3.setUmaPackedRecipe("{\"packedSlotDataList\":[{\"slotID\":\"MaleEyes\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"EyeOverlay\",\"colorList\":null,\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"EyeOverlayAdjust\",\"colorList\":[170,43,68,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[64,64,128,128]}]},{\"slotID\":\"MaleFace\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleHead02\",\"colorList\":[189,177,195,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"MaleHair01\",\"colorList\":[41,8,39,63],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"MaleEyebrow01\",\"colorList\":[8,1,7,12],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[384,256,256,64]}]},{\"slotID\":\"MaleTorso\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleBody02\",\"colorList\":[189,177,195,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleHands\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleBody02\",\"colorList\":[189,177,195,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleInnerMouth\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"InnerMouth\",\"colorList\":null,\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleJeans01\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleJeans01\",\"colorList\":[73,123,154,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleFeet\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleBody02\",\"colorList\":[189,177,195,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},null,null,null,null,null,null,null,null],\"race\":\"HumanMale\",\"umaDna\":{},\"packedDna\":[{\"dnaType\":\"UMADnaHumanoid\",\"packedDna\":\"{\\\"height\\\":128,\\\"headSize\\\":128,\\\"headWidth\\\":128,\\\"neckThickness\\\":128,\\\"armLength\\\":128,\\\"forearmLength\\\":128,\\\"armWidth\\\":128,\\\"forearmWidth\\\":128,\\\"handsSize\\\":128,\\\"feetSize\\\":128,\\\"legSeparation\\\":128,\\\"upperMuscle\\\":128,\\\"lowerMuscle\\\":128,\\\"upperWeight\\\":128,\\\"lowerWeight\\\":128,\\\"legsSize\\\":128,\\\"belly\\\":128,\\\"waist\\\":128,\\\"gluteusSize\\\":128,\\\"earsSize\\\":128,\\\"earsPosition\\\":128,\\\"earsRotation\\\":128,\\\"noseSize\\\":128,\\\"noseCurve\\\":128,\\\"noseWidth\\\":128,\\\"noseInclination\\\":128,\\\"nosePosition\\\":128,\\\"nosePronounced\\\":128,\\\"noseFlatten\\\":128,\\\"chinSize\\\":128,\\\"chinPronounced\\\":128,\\\"chinPosition\\\":128,\\\"mandibleSize\\\":128,\\\"jawsSize\\\":128,\\\"jawsPosition\\\":128,\\\"cheekSize\\\":128,\\\"cheekPosition\\\":128,\\\"lowCheekPronounced\\\":128,\\\"lowCheekPosition\\\":128,\\\"foreheadSize\\\":128,\\\"foreheadPosition\\\":128,\\\"lipsSize\\\":128,\\\"mouthSize\\\":128,\\\"eyeRotation\\\":128,\\\"eyeSize\\\":128,\\\"breastSize\\\":128}\"}]}");
                list.add(c3);
                
                UCharacter c4 = new UCharacter();
                c4.setName("mob_statue");
                c4.setOwnerName("mob_statue");
                //c4.setUmaPackedRecipe("{\"packedSlotDataList\":[{\"slotID\":\"FemaleEyes\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"EyeOverlay\",\"colorList\":null,\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"EyeOverlayAdjust\",\"colorList\":[182,66,204,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[64,64,128,128]}]},{\"slotID\":\"FemaleHead_Head\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleHead01\",\"colorList\":[219,206,220,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleEyebrow01\",\"colorList\":[32,15,15,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[384,256,256,64]},{\"overlayID\":\"FemaleLipstick01\",\"colorList\":[286,217,254,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[480,203,64,32]},{\"overlayID\":\"FemaleLongHair01\",\"colorList\":[201,200,178,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleHead_Mouth\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleHead01\",\"colorList\":[219,206,220,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleEyebrow01\",\"colorList\":[32,15,15,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[384,256,256,64]},{\"overlayID\":\"FemaleLipstick01\",\"colorList\":[286,217,254,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[480,203,64,32]},{\"overlayID\":\"FemaleLongHair01\",\"colorList\":[201,200,178,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleHead_Eyes\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleHead01\",\"colorList\":[219,206,220,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleEyebrow01\",\"colorList\":[32,15,15,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[384,256,256,64]},{\"overlayID\":\"FemaleLipstick01\",\"colorList\":[286,217,254,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[480,203,64,32]},{\"overlayID\":\"FemaleLongHair01\",\"colorList\":[201,200,178,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleHead_Nose\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleHead01\",\"colorList\":[219,206,220,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleEyebrow01\",\"colorList\":[32,15,15,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[384,256,256,64]},{\"overlayID\":\"FemaleLipstick01\",\"colorList\":[286,217,254,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[480,203,64,32]},{\"overlayID\":\"FemaleLongHair01\",\"colorList\":[201,200,178,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleHead_Ears\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleHead01\",\"colorList\":[219,206,220,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleEyebrow01\",\"colorList\":[32,15,15,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[384,256,256,64]},{\"overlayID\":\"FemaleLipstick01\",\"colorList\":[286,217,254,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[480,203,64,32]},{\"overlayID\":\"FemaleLongHair01\",\"colorList\":[201,200,178,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleEyelash\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleEyelash\",\"colorList\":[0,0,0,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleTorso\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleBody02\",\"colorList\":[219,206,220,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleUnderwear01\",\"colorList\":[146,95,81,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[0,512,1024,512]},{\"overlayID\":\"FemaleJeans01\",\"colorList\":[60,225,199,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleHands\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleBody02\",\"colorList\":[219,206,220,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleUnderwear01\",\"colorList\":[146,95,81,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[0,512,1024,512]},{\"overlayID\":\"FemaleJeans01\",\"colorList\":[60,225,199,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleFeet\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleBody02\",\"colorList\":[219,206,220,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleUnderwear01\",\"colorList\":[146,95,81,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[0,512,1024,512]},{\"overlayID\":\"FemaleJeans01\",\"colorList\":[60,225,199,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleInnerMouth\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"InnerMouth\",\"colorList\":null,\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleLegs\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleBody02\",\"colorList\":[219,206,220,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleUnderwear01\",\"colorList\":[146,95,81,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[0,512,1024,512]},{\"overlayID\":\"FemaleJeans01\",\"colorList\":[60,225,199,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleTshirt01\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleTshirt01\",\"colorList\":[75,175,165,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleLongHair01\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleHead01\",\"colorList\":[219,206,220,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"FemaleEyebrow01\",\"colorList\":[32,15,15,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[384,256,256,64]},{\"overlayID\":\"FemaleLipstick01\",\"colorList\":[286,217,254,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[480,203,64,32]},{\"overlayID\":\"FemaleLongHair01\",\"colorList\":[201,200,178,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"FemaleLongHair01_Module\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"FemaleLongHair01_Module\",\"colorList\":[201,200,178,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]}],\"race\":\"HumanFemale\",\"umaDna\":{},\"packedDna\":[{\"dnaType\":\"UMADnaHumanoid\",\"packedDna\":\"{\\\"height\\\":128,\\\"headSize\\\":128,\\\"headWidth\\\":128,\\\"neckThickness\\\":128,\\\"armLength\\\":128,\\\"forearmLength\\\":128,\\\"armWidth\\\":128,\\\"forearmWidth\\\":128,\\\"handsSize\\\":128,\\\"feetSize\\\":128,\\\"legSeparation\\\":128,\\\"upperMuscle\\\":128,\\\"lowerMuscle\\\":128,\\\"upperWeight\\\":128,\\\"lowerWeight\\\":128,\\\"legsSize\\\":128,\\\"belly\\\":128,\\\"waist\\\":128,\\\"gluteusSize\\\":128,\\\"earsSize\\\":128,\\\"earsPosition\\\":128,\\\"earsRotation\\\":128,\\\"noseSize\\\":128,\\\"noseCurve\\\":128,\\\"noseWidth\\\":128,\\\"noseInclination\\\":128,\\\"nosePosition\\\":128,\\\"nosePronounced\\\":128,\\\"noseFlatten\\\":128,\\\"chinSize\\\":128,\\\"chinPronounced\\\":128,\\\"chinPosition\\\":128,\\\"mandibleSize\\\":128,\\\"jawsSize\\\":128,\\\"jawsPosition\\\":128,\\\"cheekSize\\\":128,\\\"cheekPosition\\\":128,\\\"lowCheekPronounced\\\":128,\\\"lowCheekPosition\\\":128,\\\"foreheadSize\\\":128,\\\"foreheadPosition\\\":128,\\\"lipsSize\\\":128,\\\"mouthSize\\\":128,\\\"eyeRotation\\\":128,\\\"eyeSize\\\":128,\\\"breastSize\\\":128}\"}]}");
                c4.setUmaPackedRecipe(UMARecipes.DEFAULT_RECIPES[0]);
                list.add(c4);
                
                UCharacter c5 = new UCharacter();
                c5.setName("mob_ninja");
                c5.setOwnerName("mob_ninja");
                c5.setUmaPackedRecipe("{\"packedSlotDataList\":[{\"slotID\":\"MaleEyes\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"EyeOverlay\",\"colorList\":null,\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null},{\"overlayID\":\"EyeOverlayAdjust\",\"colorList\":[55,111,66,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":[64,64,128,128]}]},{\"slotID\":\"MaleHead_Head\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleHead01\",\"colorList\":[159,124,155,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleTorso\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleBody01\",\"colorList\":[159,124,155,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleHands\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleBody01\",\"colorList\":[159,124,155,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleInnerMouth\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"InnerMouth\",\"colorList\":null,\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleJeans01\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleJeans01\",\"colorList\":[122,132,206,255],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleFeet\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleBody01\",\"colorList\":[159,124,155,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleHead_Eyes\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleHead01\",\"colorList\":[159,124,155,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleHead_Ears\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleHead01\",\"colorList\":[159,124,155,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleHead_Mouth\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleHead01\",\"colorList\":[159,124,155,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},{\"slotID\":\"MaleHead_Nose\",\"overlayScale\":100,\"copyOverlayIndex\":-1,\"OverlayDataList\":[{\"overlayID\":\"MaleHead01\",\"colorList\":[159,124,155,510],\"channelMaskList\":null,\"channelAdditiveMaskList\":null,\"rectList\":null}]},null,null,null,null],\"race\":\"HumanMale\",\"umaDna\":{},\"packedDna\":[{\"dnaType\":\"UMADnaHumanoid\",\"packedDna\":\"{\\\"height\\\":128,\\\"headSize\\\":128,\\\"headWidth\\\":128,\\\"neckThickness\\\":128,\\\"armLength\\\":128,\\\"forearmLength\\\":128,\\\"armWidth\\\":128,\\\"forearmWidth\\\":128,\\\"handsSize\\\":128,\\\"feetSize\\\":128,\\\"legSeparation\\\":128,\\\"upperMuscle\\\":128,\\\"lowerMuscle\\\":128,\\\"upperWeight\\\":128,\\\"lowerWeight\\\":128,\\\"legsSize\\\":128,\\\"belly\\\":128,\\\"waist\\\":128,\\\"gluteusSize\\\":128,\\\"earsSize\\\":128,\\\"earsPosition\\\":128,\\\"earsRotation\\\":128,\\\"noseSize\\\":128,\\\"noseCurve\\\":128,\\\"noseWidth\\\":128,\\\"noseInclination\\\":128,\\\"nosePosition\\\":128,\\\"nosePronounced\\\":128,\\\"noseFlatten\\\":128,\\\"chinSize\\\":128,\\\"chinPronounced\\\":128,\\\"chinPosition\\\":128,\\\"mandibleSize\\\":128,\\\"jawsSize\\\":128,\\\"jawsPosition\\\":128,\\\"cheekSize\\\":128,\\\"cheekPosition\\\":128,\\\"lowCheekPronounced\\\":128,\\\"lowCheekPosition\\\":128,\\\"foreheadSize\\\":128,\\\"foreheadPosition\\\":128,\\\"lipsSize\\\":128,\\\"mouthSize\\\":128,\\\"eyeRotation\\\":128,\\\"eyeSize\\\":128,\\\"breastSize\\\":128}\"}]}");
                c5.setUmaPackedRecipe(UMARecipes.DEFAULT_RECIPES[1]);
                list.add(c5);
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
