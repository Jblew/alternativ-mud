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

                tpl.assign("pageContent", "<h2>Welcome to Alteriativ MUD!</h2><p>This software is still in development phase. "
                        + "Please visit <a href=\""+StaticConfig.ALTERNATIV_MUD_WEBSITE+"\">our page on Github</a>. If you would like to contribute, write to jblew[at]blew.pl.</p>"
                        + "<ul>"
                        + "   <li><a href=\"/admin\">Go to admin panel</a></li>"
                        + "</ul>");
                return (tpl.out());
        }
}
