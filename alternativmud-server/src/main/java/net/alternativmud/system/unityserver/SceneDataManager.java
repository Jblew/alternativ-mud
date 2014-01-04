/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.unityserver;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author teofil
 */
class SceneDataManager {

    private static final int MAX_CHARACTERS_PER_PACKET = 5;//5*25=125 bytes
    private final String sceneName;
    private final byte sceneID;
    private final BitSet idManager = new BitSet(0xFF);
    private final Map<String, Byte> characters = new HashMap<String, Byte>();
    private final float[] positionX = new float[0xFF];
    private final float[] positionY = new float[0xFF];
    private final float[] positionZ = new float[0xFF];
    private final float[] rotationX = new float[0xFF];
    private final float[] rotationY = new float[0xFF];
    private final float[] rotationZ = new float[0xFF];

    public SceneDataManager(String sceneName, byte sceneId) {
        this.sceneName = sceneName;
        this.sceneID = sceneId;

        idManager.clear();
    }

    public synchronized byte addCharacter(String name) {
        byte id = (byte) idManager.nextClearBit(0);
        idManager.set(id);
        characters.put(name, id);
        return id;
    }

    public synchronized void removeCharacter(String name) {
        byte id = characters.get(name);
        characters.remove(name);
        idManager.clear(id);
        positionX[id] = 0f;
        positionY[id] = 0f;
        positionZ[id] = 0f;
        rotationX[id] = 0f;
        rotationY[id] = 0f;
        rotationZ[id] = 0f;
    }

    public synchronized byte getCharacterId(String name) {
        return characters.get(name);
    }

    public synchronized byte[] decodePacketAndComputeResponse(byte[] data) {
        if (data.length >= 25) {
            //short id = (short)( ((data[1]&0xFF)<<8) | (data[0]&0xFF) );
            ByteBuffer inbuf = ByteBuffer.wrap(data);
            byte id = inbuf.get();
            if (characters.containsValue(id)) {
                //byte sceneId = inbuf.get();
                positionX[id] = inbuf.getFloat();
                positionY[id] = inbuf.getFloat();
                positionZ[id] = inbuf.getFloat();
                rotationX[id] = inbuf.getFloat();
                rotationY[id] = inbuf.getFloat();
                rotationZ[id] = inbuf.getFloat();

                ByteBuffer outbuf = ByteBuffer.allocate(25 * characters.size());//send data of every character
                for (byte writeId : characters.values()) {
                    //if (writeId != id) {
                        outbuf.put(writeId);
                        //outbuf.put(sceneId);
                        outbuf.putFloat(positionX[writeId]);
                        outbuf.putFloat(positionY[writeId]);
                        outbuf.putFloat(positionZ[writeId]);
                        outbuf.putFloat(rotationX[writeId]);
                        outbuf.putFloat(rotationY[writeId]);
                        outbuf.putFloat(rotationZ[writeId]);
                    //}
                }
                return outbuf.array();
            } else {
                //System.out.println("Character("+id+") not registered on this scene ("+sceneID+").");
                return null;
            }
        } else {
            //System.out.println("Incorrect packet length: "+data.length);
            return null;
        }
    }

    public synchronized List<byte[]> encodeBroadcastPackets() {
        /*int numOfPackets = (int)Math.ceil((float)characters.size()/(float)MAX_CHARACTERS_PER_PACKET);
         List<byte[]> output = new ArrayList<byte []>(numOfPackets);
         int characterIndex = 0;
         Collection<Byte> characterIds = characters.values();
         int numOfCharacters = characterIds.size();
         for(int i = 0;i < numOfPackets;i++) {
         ByteBuffer bb = ByteBuffer.allocate(Math.min(25*MAX_CHARACTERS_PER_PACKET, numOfCharacters-characterIndex-1));
         for(int j = 0;j < MAX_CHARACTERS_PER_PACKET;j++) {
         byte id = characterIds.get(characterIndex);
         characterIndex++;
         bb.put(id);
         bb.putFloat(positionX[id]);
         bb.putFloat(positionY[id]);
         bb.putFloat(positionZ[id]);
         bb.putFloat(rotationX[id]);
         bb.putFloat(rotationY[id]);
         bb.putFloat(rotationZ[id]);
         if(!) break;
         }
         output.add(bb.array());
                
         if(!charactersIterator.hasNext()) break;
         }
         return output;*/
        int numOfPackets = (int) Math.ceil((float) characters.size() / (float) MAX_CHARACTERS_PER_PACKET);
        List<byte[]> output = new ArrayList<byte[]>(numOfPackets);
        int charactersLeft = characters.size();
        int leftInPacket = 0;
        ByteBuffer bb = null;
        for (byte id : characters.values()) {
            if (leftInPacket == 0) {
                if (bb != null) {
                    output.add(bb.array());
                }
                bb = ByteBuffer.allocate(Math.min(25 * MAX_CHARACTERS_PER_PACKET, charactersLeft));
            }

            bb.put(id);
            //bb.put(sceneId);
            bb.putFloat(positionX[id]);
            bb.putFloat(positionY[id]);
            bb.putFloat(positionZ[id]);
            bb.putFloat(rotationX[id]);
            bb.putFloat(rotationY[id]);
            bb.putFloat(rotationZ[id]);

            charactersLeft--;
            leftInPacket--;
        }
        return output;
    }
}
