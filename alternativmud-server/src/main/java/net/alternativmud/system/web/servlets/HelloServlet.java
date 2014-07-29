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
import net.alternativmud.StaticConfig;
import net.alternativmud.system.web.servlets.templates.Template;
import net.alternativmud.system.web.servlets.templates.Templates;

/**
 *
 * @author jblew
 */
public class HelloServlet extends HttpServlet {
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
                tpl.assign("pageTitle", "Welcome to AlternativMUD");
                tpl.assign("scripts", "");

                tpl.assign("pageContent", "<h2>Welcome to AlteriativMUD!</h2><p>Thank's for using this piece of code."
                        + " Please note, that this software is still in development phase. "
                        + "Here, at the panel you can review settings and logs, restart server and change some of the runtime settings."
                        + "Persistent configuration can be done via config.json file in the CWD directory."
                        + "Please visit <a href=\""+StaticConfig.ALTERNATIV_MUD_WEBSITE+"\">our page on Github</a>. Feel free to contribute. If you wish to contact me, please mail to: jblew[at]jblew.pl</p>"
                        + "<ul>"
                        + "   <li><a href=\"/admin\">Go to admin panel</a></li>"
                        + "</ul>");
                return (tpl.out());
        }
}
