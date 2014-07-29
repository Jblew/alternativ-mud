/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.system.web.servlets.templates;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.StaticConfig;


/**
 *
 * @author jblew
 */
public class Templates {
    private Templates() {
    }

    public static Template get(String name) {
        try {
            Template t = new Template(Resources.toString(Resources.getResource(Templates.class, name+".html"), Charsets.UTF_8));
            t.assign("pageTitle", "AlternativMUD");
            t.assign("scripts", "");
            t.assign("alternativ-mud-website", StaticConfig.ALTERNATIV_MUD_WEBSITE);
            t.assign("pageContent", "<p>Sorry, page content is empty.</p>");
            return t;
        } catch (IOException ex) {
            Logger.getLogger(Templates.class.getName()).log(Level.SEVERE, "Could not get WebTemplate "+name, ex);
            return null;
        }
    }
}
