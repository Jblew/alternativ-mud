/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.system.unityserver;

import com.google.common.eventbus.EventBus;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.alternativmud.logic.world.characters.UCharacter;

/**
 *
 * @author teofil
 */
public class UnityBusCharacterPool {
    public final Map<Byte, Map<Byte, UCharacter>> charactersInScenes = Collections.synchronizedMap(new HashMap<Byte, Map<Byte, UCharacter>>());
    public final Map<UCharacter, EventBus> characterBuses = Collections.synchronizedMap(new HashMap<UCharacter, EventBus>());
}
