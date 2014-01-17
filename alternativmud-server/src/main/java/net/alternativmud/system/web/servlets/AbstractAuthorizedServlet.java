/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.system.web.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.alternativmud.logic.User;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author jblew
 */
public abstract class AbstractAuthorizedServlet extends HttpServlet {
    protected final static Map<String, User> SESSION_IDENTIFIERS = Collections.synchronizedMap(new HashMap<String, User>());
    protected User authorizedUser = null;

    public AbstractAuthorizedServlet() {

    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if(!checkLogin(req)) {
            res.sendRedirect("/auth?redirect="+Base64.encodeBase64String(getAuthRedirectPath().getBytes()));
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if(!checkLogin(req)) {
            res.sendRedirect("/auth?redirect="+Base64.encodeBase64String(getAuthRedirectPath().getBytes()));
        }
    }

    private boolean checkLogin(HttpServletRequest req) {
        HttpSession session = req.getSession();

        if(session.getAttribute("ssid") != null && session.getAttribute("loggedin") != null && session.getAttribute("login") != null) {
            String ssid = (String) session.getAttribute("ssid");
            if (SESSION_IDENTIFIERS.containsKey(ssid)) {
                authorizedUser = SESSION_IDENTIFIERS.get(ssid);
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

    public abstract String getAuthRedirectPath();
}
