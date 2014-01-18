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

/**
 *
 * @author jblew
 */
public class TestServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        try {
            out.println("OK");
        } catch (Exception ex) {
            ex.printStackTrace(out);
        }
        out.close();
    }
}
