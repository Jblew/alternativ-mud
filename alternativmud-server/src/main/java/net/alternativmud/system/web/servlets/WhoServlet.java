/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.system.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.alternativmud.system.web.servlets.templates.Template;
import net.alternativmud.system.web.servlets.templates.Templates;

/**
 *
 * @author jblew
 */
public class WhoServlet  extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        try {
            out.println(generatePage());
        } catch (Exception ex) {
            ex.printStackTrace(out);
        }
        out.close();
    }

    private String generatePage() throws Exception {
                Template tpl = Templates.get("admin");
                tpl.assign("pageTitle", "Who is online");

                String alllist = "nobody";
                String onlinelist = "nobody";
                
                /*for(Being b : Being.Manager.getInstance().getElements()) {
                    if(b instanceof Player) {
                        Player p = (Player)b;
                        alllist += "<li>"+p.getName()+(p.isAdmin()? " (immortal)" : "")+(p.isLoggedIn()? " (online)" : "")+(p.isPlaying()? " (playing)" : "")+"</li>";
                        if(p.isLoggedIn()) onlinelist += "<li>"+p.getName()+(p.isAdmin()? " (immortal)" : "")+(p.isPlaying()? " (playing)" : "")+"</li>";
                    }
                }*/

                tpl.assign("pageContent", "<h2>Who is online</h2><p><ul>"+onlinelist+"</ul></p><h2>All users</h2><p><ul>"+alllist+"</ul></p>");
                return (tpl.out());
        }
}
