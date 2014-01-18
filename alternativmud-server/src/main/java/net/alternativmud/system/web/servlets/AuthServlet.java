/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.alternativmud.App;
import net.alternativmud.lib.IdManager;
import net.alternativmud.logic.User;
import net.alternativmud.security.PasswordHasher;
import net.alternativmud.system.web.servlets.templates.Template;
import net.alternativmud.system.web.servlets.templates.Templates;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author jblew
 */
public class AuthServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        generateResponse(req, res);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        generateResponse(req, res);
    }

    private void generateResponse(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String redirect = "/";
        if (req.getParameter("redirect") != null) {
            redirect = Charset.defaultCharset().decode(ByteBuffer.wrap(Base64.decodeBase64(req.getParameter("redirect")))).toString();
        }
        boolean loginTry = false;
        boolean loginOk = false;
        if (req.getParameter("login") != null && req.getParameter("password") != null) {
            try {
                loginTry = true;
                User u = App.getApp().getEntitiesManager().getUsersDao().queryForId(req.getParameter("login"));
                if (u != null) {
                    if (u.isPasswordCorrect(req.getParameter("password"))) {
                        HttpSession session = req.getSession();
                        session.setAttribute("loggedin", "true");
                        session.setAttribute("login", u.getLogin());
                        String ssid = PasswordHasher.generateHash(u.getLogin() + IdManager.getSessionSafe()+ "c" + IdManager.getSessionSafe() + "" + App.getApp().getConfig().getHttpSessionSalt());
                        session.setAttribute("ssid", ssid);
                        AbstractAuthorizedServlet.SESSION_IDENTIFIERS.put(ssid, u);
                        res.sendRedirect(redirect);
                        loginOk = true;
                        Logger.getLogger(getClass().getName()).log(Level.INFO, "{0} logged in.", u.getLogin());
                        return;
                    } else {
                        loginOk = false;
                    }
                } else {
                    loginOk = false;
                }
                loginOk = false;
            } catch (SQLException ex) {
                Logger.getLogger(AuthServlet.class.getName()).log(Level.SEVERE, null, ex);
                loginOk = false;
            }
        }

        if (req.getParameter("logout") != null) {
            HttpSession session = req.getSession();

            User u = null;

            if (session.getAttribute("ssid") != null) {
                u = AbstractAuthorizedServlet.SESSION_IDENTIFIERS.get((String)session.getAttribute("ssid"));
                AbstractAuthorizedServlet.SESSION_IDENTIFIERS.remove((String)session.getAttribute("ssid"));
            }

            session.setAttribute("loggedin", "false");
            session.setAttribute("login", "");
            session.setAttribute("ssid", "");

            Logger.getLogger(getClass().getName()).log(Level.INFO, "{0} logged out.", (u == null? "Unknown" : u.getLogin()));

            res.sendRedirect(redirect);
        } else if(checkLogin(req)){
            res.sendRedirect(redirect);
        } else {
            PrintWriter out = res.getWriter();
            try {
                Template tpl = Templates.get("admin");
                tpl.assign("pageTitle", "Please log in");
                tpl.assign("scripts", "");
                //System.out.println("login");
                tpl.assign("pageContent", "<div class=\"login-form-container\">"
                        + (loginTry ? "<p class=\"login-text info\">Wrong login or password</p>" : "<p class=\"login-text info\">You have to log in</p>")
                        + "<form class=\"login-form\" method=\"post\" action=\"/auth\">"
                        + "<table class=\"login-form-table\">"
                        + "<tr><td class=\"login-field-label\">Login: </td><td><input class=\"login-field\" type=\"text\" name=\"login\" /></td></tr>"
                        + "<tr><td class=\"login-field-label\">Password: </td><td><input class=\"login-field\" type=\"password\" name=\"password\" /></td></tr>"
                        + "<tr><td colspan=\"2\"><input class=\"login-button\" type=\"submit\" value=\"Log in &gt;\" /></td></tr>"
                        + "</table><input type=\"hidden\" name=\"redirect\" value=\"" + redirect + "\" />"
                        + "</form>");
                out.println(tpl.out());
            } catch (Exception ex) {
                ex.printStackTrace(out);
            }
            out.close();
        }
    }

    private boolean checkLogin(HttpServletRequest req) {
        HttpSession session = req.getSession();

        if(session.getAttribute("ssid") != null && session.getAttribute("loggedin") != null && session.getAttribute("login") != null) {
            String ssid = (String) session.getAttribute("ssid");
            if (AbstractAuthorizedServlet.SESSION_IDENTIFIERS.containsKey(ssid)) {
                User authorizedUser = AbstractAuthorizedServlet.SESSION_IDENTIFIERS.get(ssid);
                if (authorizedUser.getLogin().equals(session.getAttribute("login"))) {
                    session.setAttribute("loggedin", "true");
                    session.setAttribute("login", authorizedUser.getLogin());
                    session.setAttribute("ssid", ssid);
                    return true;
                }
            }
        }
        return false;
    }
}
