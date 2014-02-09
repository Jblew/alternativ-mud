/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.db.variables;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author teofil
 */
class DefaultVariables {
    public static final Map<String, String> VARIABLES;
    
    static {
        Map<String, String> variables = new HashMap<String, String>();
        
        variables.put("global.power", "1");
        
        VARIABLES = Collections.unmodifiableMap(variables);
    }
    
    private DefaultVariables() {}
}
