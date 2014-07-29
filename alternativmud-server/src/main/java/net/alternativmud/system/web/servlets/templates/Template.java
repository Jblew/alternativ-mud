/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.system.web.servlets.templates;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author teofil
 */
public class Template {
    private final String html;
    private final Map<String, String> variables = new HashMap<String, String>();
    
    public Template(String html) {
        this.html = html;
    }
    
    public synchronized void assign(String key, String value) {
        variables.put(key, value);
    }
    
    public synchronized String out() {
        String out = html;
        for(String key : variables.keySet()) {
            out = out.replace("{"+key+"}", variables.get(key));
        }
        return out;
    }
}
