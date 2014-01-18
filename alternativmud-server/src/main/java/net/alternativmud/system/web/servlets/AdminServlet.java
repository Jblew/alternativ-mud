/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.web.servlets;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.alternativmud.system.web.servlets.templates.Template;
import net.alternativmud.system.web.servlets.templates.Templates;

/**
 *
 * @author jblew
 */
public class AdminServlet extends AbstractAuthorizedServlet {
    public static final String URL = "/admin";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        super.doGet(req, res);
        PrintWriter out = res.getWriter();
        try {
            out.println(generateResponse(req, res));
        } catch (Exception ex) {
            ex.printStackTrace(out);
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "", ex);
        }
        out.close();
    }

    private String generateResponse(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Template tpl = Templates.get("admin");
        tpl.assign("pageTitle", "AlternativMUD Admin Panel");
        tpl.assign("scripts", "");
        if (authorizedUser != null && authorizedUser.getAdmin()) {
            String content = "<div class=\"span-17 prepend-1 colborder\">";
            content += generatePage(req, res);
            content += menu(req, res);
            tpl.assign("pageContent", content);
        } else {
            tpl.assign("pageContent", "<h2>Sorry, you must be admin to access this page.</h2>");
        }
        return (tpl.out());
    }

    private String generatePage(HttpServletRequest req, HttpServletResponse res) {
        String action = "main";
        String method = "main";
        String parameter = "";
        String parameter2 = "";
        boolean confirmation = false;
        if (req.getParameter("action") != null) {
            action = req.getParameter("action");
        }
        if (req.getParameter("method") != null) {
            method = req.getParameter("action");
        }
        if (req.getParameter("parameter") != null) {
            parameter = req.getParameter("parameter");
        }
        if (req.getParameter("parameter2") != null) {
            parameter2 = req.getParameter("parameter2");
        }
        if (req.getParameter("confirmation") != null && req.getParameter("confirmation").trim().equalsIgnoreCase("true")) {
            confirmation = true;
        }

        if (action.equals("main") || action.isEmpty()) {
            return main(method, parameter, parameter2, confirmation, req, res);
        } else if (action.equals("power")) {
            return power(method, parameter, parameter2, confirmation, req, res);
        } else if (action.equals("logs")) {
            return logs(method, parameter, parameter2, confirmation, req, res);
        } else if (action.equals("reports")) {
            return reports(method, parameter, parameter2, confirmation, req, res);
        } else if (action.equals("runtime")) {
            return runtime(method, parameter, parameter2, confirmation, req, res);
        } else if (action.equals("currentsession")) {
            return currentsession(method, parameter, parameter2, confirmation, req, res);
        } else if (action.equals("changepassword")) {
            return changepassword(method, parameter, parameter2, confirmation, req, res);
        } else {
            return error404(req, res);
        }
    }

    private String menu(HttpServletRequest req, HttpServletResponse res) {
        return "</div>"
                + "<div class=\"span-4 last\">"
                + "   <h3>Menu</h3>"
                + "   <ul>"
                + "      <li><a href=\"" + URL + "\"?action=main\">Main</a></li>"
                + "      <li><a href=\"" + URL + "?action=currentsession\">Current session</a></li>"
                + "      <li><a href=\"" + URL + "?action=runtime\">Runtime</a></li>"
                + "      <li><a href=\"" + URL + "?action=reports\">Reports</a></li>"
                + "      <li><a href=\"" + URL + "?action=notifications\">Notifications</a></li>"
                + "      <li><a href=\"" + URL + "?action=channels\">Channels</a></li>"
                + "      <li><a href=\"" + URL + "?action=logs\">Logs</a></li>"
                + "      <li><a href=\"" + URL + "?action=power\">Power</a></li>"
                + "      <li><a href=\"" + URL + "?action=changepassword\">Change password</a></li>"
                + "      <li><a href=\"/editor\">Editor</a></li>"
                + "      <li><a href=\"/auth?logout=true\">Logout</a></li>"
                + "   </ul>"
                + "</div>";
    }

    @Override
    public String getAuthRedirectPath() {
        return "/admin";
    }

    private String main(String method, String parameter, String parameter2, boolean confirmation, HttpServletRequest req, HttpServletResponse res) {
        return "<h2>" + "Welcome in AlteriativMUD Administration Panel" + ".</h2>"
                + "<p>" + "Welcome in AlternativMUD Administration Panel.</p>"
                //+ "<h2>Time</h2><p>Time synchronization with atomic clock is <strong>" + (PrecisionTime.isSynchronizationEnabled() ? "Enabled" : "Disabled") + "</strong>."
                //+ "Current time correction: <strong>" + PrecisionTime.getCorrectionMs() + "ms</strong>. Actual time: <strong>" + PrecisionTime.getStringRealTime() + "</strong>.</p>"
                //+ "<p>Server's uptime: <strong>" + UptimeKeeper.getTextUptime() + "</strong></p>"
                + "</p>";
                //+ "<h2>License</h2>"
                //+ "<p>" + License.LICENSE + "</p>";
    }

    private String power(String method, String parameter, String parameter2, boolean confirmation, HttpServletRequest req, HttpServletResponse res) {
        if (method.equals("main")) {
            return "<h2>Power</h2>"
                    + "<p>Here, you can restart or shut down the mud server, but be carefoul. To restart server it must be run using the ./run.sh script."
                    + " Otherwise, restart will also shutdown the server, so you will have to start it manually.</p>"
                    + ""
                    + "<ul>"
                    + "   <li><a href=\"" + URL + "?action=power&method=restart\">Restart</a></li>"
                    + "   <li><a href=\"" + URL + "?action=power&method=shutdown\">Shutdown</a></li>"
                    + "</ul>";
        } else if (method.equals("restart")) {
            if (confirmation) {
                //MudShutdown.restart();
                return "RESTART";
            } else {
                return confirmationFormHtml("Restart server", "<strong>Are you sure, you want restart marinesmud server?</strong> Remember that,"
                        + " if server hasn't been run using run.sh script, this action will shutdown mud, and you will have to start it manually.",
                        "power", "restart", "", "");
            }
        } else if (method.equals("shutdown")) {
            if (confirmation) {
                //MudShutdown.shutdown();
                return "SHUTDOWN";
            } else {
                return confirmationFormHtml("Shutdown server", "<strong>Are you sure, you want shutdown marinesmud server?</strong>"
                        + "Remember that you will have to start server manually after shutdown.", "power", "shutdown", "", "");
            }
        } else {
            return error404(req, res);
        }
    }

    private String logs(String method, String parameter, String parameter2, boolean confirmation, HttpServletRequest req, HttpServletResponse res) {
        String content = "";
        //File dir = new File(Config.get("logs dir"));
        //File[] files = dir.listFiles((FileFilter) FileFileFilter.FILE);
        //Arrays.sort(files, NameFileComparator.NAME_COMPARATOR);
        if (method.equals("main")) {
            content += "<h2>Logs</h2>"
                    + "<p>Here, you can view logs generated by whole mud server.</p>"
                    + "<p class=\"info\">Do you want to <a href=\"" + URL + "?action=logs&method=current\">view current log</a>?</p>"
                    + "<ul>";

            //for (File file : files) {
            //    content += "<li><a href=\"" + URL + "?action=logs&method=view&parameter=" + file.getName() + "\">" + file.getName() + "</a></li>";
            //}
            content += "</ul>";
        } else if (method.equals("view")) {
            content += "<h2>Log file: '" + parameter + "'</h2><p>" + "Do you want to go back to <a href=\"" + URL + "?action=logs\">log file list</a>?</p>";
            //boolean found = false;
            /*for (File file : files) {
                if (file.getName().equals(parameter)) {
                    try {
                        content += "<div style=\"overflow: auto;\"><pre>" + FileUtils.getFileContents(file).replace("&", "&amp;") + "</pre></div>";
                    } catch (FileNotFoundException ex) {
                        content += "<p class=\"error\">Sorry. There is no uch file in log file list.</p>";
                    } catch (IOException ex) {
                        content += "<p class=\"error\">Sorry. IOException: " + ex.getMessage() + ".</p>";
                    }
                    found = true;
                    break;
                }
            }*/
            //if (!found) {
                content += "<p class=\"error\">Sorry. There is no such file in log file list.</p>";
            //}
        } else if (method.equals("current")) {
            content += "<h2>Current log</h2>Do you want to go back to <a href=\"" + URL + "?action=logs\">log file list</a>?</p>";
            content += "<div style=\"overflow: auto;\"><pre>" + "" + "</pre></div>";
        } else {
            content += error404(req, res);
        }
        return content;
    }

    private String reports(String method, String parameter, String parameter2, boolean confirmation, HttpServletRequest req, HttpServletResponse res) {
        String content = "";
        //List<String> reports = World.getInstance().getReports();
        boolean showReportsList = false;
        if (method.equals("add")) {
            if (confirmation && !parameter.isEmpty() && !parameter2.isEmpty()) {
                //World.getInstance().report(parameter.toUpperCase() + "[" + authorizedUser.getName() + "] " + parameter2);
                //reports = World.getInstance().getReports();
                showReportsList = true;
            } else {
                content += "<h2>Add new report</h2>"
                        + "<form action=\"" + URL + "\" method=\"get\">"
                        + "   <p class=\"info\">In this form you can add new report. Please choose type of report: idea, bug or todo. Report will be signed with you username.</p>";
                if (confirmation) {
                    content += "<p class=\"error\">You must fill all fields.</p>";
                }
                content += "   <input type=\"hidden\" name=\"action\" value=\"reports\" />"
                        + "   <input type=\"hidden\" name=\"method\" value=\"add\" />"
                        + "   <input type=\"hidden\" name=\"confirmation\" value=\"true\" />"
                        + "   <table>"
                        + "      <tr><td>Type of report: </td>"
                        + "         <td><select id=\"parameter\" name=\"parameter\">"
                        + "         <option value=\"todo\" selected>Todo</option>"
                        + "         <option value=\"idea\">Idea</option>"
                        + "         <option value=\"bug\">Bug</option>"
                        + "      </select></td></tr>"
                        + "      <tr><td>Content:</td><td><textarea id=\"parameter2\" name=\"parameter2\" style=\"width: 100%; height: 120px;\"></textarea></td></tr>"
                        + "   <tr><td style=\"text-align: right;\"><a href=\"" + URL + "?action=reports\">Cancel</a>&nbsp;</td><td style=\"text-align: left;\">&nbsp;<input type=\"submit\" value=\"Send report\" /></td></tr>"
                        + "   </table>"
                        + "</form>";
            }
        } else if (method.equals("delete") && !parameter.isEmpty()) {
            if (confirmation) {
                //if (TypeUtils.isInteger(parameter)) {
                //    World.getInstance().removeReport(Integer.valueOf(parameter));
                //}
                //reports = World.getInstance().getReports();
                showReportsList = true;
            } else {
                /*if (TypeUtils.isInteger(parameter) && Integer.valueOf(parameter) < reports.size()) {
                    int id = Integer.valueOf(parameter);
                    content += confirmationFormHtml("Delete report", "<strong>Are you sure, you want delete report (id: " + id + ")?</strong>"
                            + " Content of this report: <i>" + reports.get(id) + "</i>.", "reports", "delete", id + "", "");
                } else {
                    content += "<h2>Delete report</h2><p class=\"error\">There is no such report.</p>";
                }*/
            }
        } else {
            content += error404(req, res);
        }
        if (method.equals("main") || showReportsList) {
            content += "<h2>Reports</h2>"
                    + "<p>Here you can see list of idea/bug reports reported by all users of mud and todo reports reported by other admin users.</p>";
            content += "<p class=\"info\">Do you want to <a href=\"" + URL + "?action=reports&method=add\">add new report</a>?</p>"
                    + "<table>"
                    + "<thead>"
                    + "   <tr><th>#</th><th>Content</th><th>Actions</th></tr>"
                    + "</thead>"
                    + "<tbody>";

            /*ListIterator<String> it = reports.listIterator();
            while (it.hasNext()) {
                int id = it.nextIndex();
                String description = it.next();
                content += "<tr><td>" + id + "</td><td>" + description + "</td><td><a href=\"" + URL + "?action=reports&method=delete&parameter=" + id + "\">Delete</a></td></tr>";
            }*/

            content += "</tbody>"
                    + "</table>";
        }
        return content;
    }

    private String runtime(String method, String parameter, String parameter2, boolean confirmation, HttpServletRequest req, HttpServletResponse res) {
        String content = "";
        if (method.equalsIgnoreCase("gc")) {
            System.gc();
            Logger.getLogger("www").log(Level.INFO, "Asked java to perform garbage collection.");
        }
        content += "<h2>Runtime</h2>"
                + "<p>This page contains information about running threads, used memory, and other useful stats.</p>"
                //+ "<h3>RunMode: " + Main.getRunMode().name() + "</h3>"
                //+ "<p>" + Main.getRunMode().getInfo() + "</p>"
                //+ "<p>" + (Main.isInDaemonMode() ? "Application is now started as <strong>daemon</strong>." : "Application is now started <strong>without daemon mode</strong>.</p>")
                + "<h3>Memory</h3>"
                + "<p>Free memory: <strong>" + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + "MB</strong>;"
                + " Used memory: <strong>" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024) + "MB</strong>;"
                + " Total memory: <strong>" + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "MB</strong>;"
                + " Memory limit: <strong>" + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "Unlimited" : (Runtime.getRuntime().maxMemory() / 1024 / 1024)) + "MB</strong>.</p>";
        content += "<p class=\"notice\">If you want, you can ask java to <a href=\"" + URL + "\"?action=runtime&method=gc\">perform Garbage Collection</a>."
                + " It may delete unused objects and freely some memory, but it may also increase total amount of memory, so be carefoul."
                + " If something would go bad, you can always restart the server in <a href=\"" + URL + "?action=power\">Power</a> section.</p>";
        content += "<h3>Threads</h3>"
                + "<table>"
                + "<thead><tr><th>Id</th><th>Name</th><th>Priority</th><th>State</th><th>Group</th></tr></thead>"
                + "<tbody>";
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        for (Thread thread : threads) {
            long id = thread.getId();
            String name = thread.getName();
            int priority = thread.getPriority();
            Thread.State state = thread.getState();
            String groupName = thread.getThreadGroup().getName();
            content += "<tr><td>" + id + "</td><td>" + name + "</td><td>" + priority + "</td><td>" + state.name() + "</td><td>" + groupName + "</td></tr>";
        }
        content += "</tbody></table><p>Threads count: <strong>" + threads.size() + "</strong>.</p>";
        return content;
    }

    private String currentsession(String method, String parameter, String parameter2, boolean confirmation, HttpServletRequest req, HttpServletResponse res) {
        String content = "";
        //MudDate dateTime = MudCalendar.getInstance().getDateTime();
        content += "<h2>Current session data</h2>";
        content += "<p>This section provides data about current session, for example count of running user threads, or current security params.</p>";
        content += "<h3>Time</h3>"
                + "<p>Output of the 'time' command: <hr />"
                //+ "<pre>" + dateTime.getTimeCommandResponse() + "</pre>"
                //+ "<hr /> Precision time data: <strong>" + dateTime.getStringTime() + "</strong>.</p>"
                + "<h3>Security</h3>";
                //+ "<p>" + (LANGUAGE == Language.PL ? "UID bieżącej sesji" : "Current session UID") + ": <strong>" + MudSecurityManager.getSessionUID() + "</strong>."
                //+ " Hosts blacklist: <strong>[" + ListBuilder.createSimpleList(MudSecurityManager.IP_BLACKLIST, ", ") + "]</strong>."
                //+ " Temporarily banned hosts: <strong>" + MudSecurityManager.getTemporarilyBannedHostsCount() + "</strong>.</p>";
        //content += "<h3>Flash policy</h3>"
        //        + "<p>"+FlashPolicyServer.getInstance().POLICY+"</p>";
        return content;
    }

    private String changepassword(String method, String parameter, String parameter2, boolean confirmation, HttpServletRequest req, HttpServletResponse res) {
        String content = "<h2>Change password</h2>";
        if (confirmation && !parameter.isEmpty()) {
            authorizedUser.setPassword(parameter);
        } else {
            content += "<form action=\"" + URL + "\" method=\"get\">"
                    + "   <p class=\"info\">Here, you can change your password. Think for a while, before you do this. Password should'n be same (or similar) as login."
                    + " The longer password, the safer it will be. It is also good, to add some numbers or characters like !@#$%^&*().</p>";
            if (confirmation) {
                content += "<p class=\"error\">You must fill all fields.</p>";
            }
            content += "  <input type=\"hidden\" name=\"action\" value=\"changepassword\" />"
                    + "   <input type=\"hidden\" name=\"confirmation\" value=\"true\" />"
                    + "   <table>"
                    + "      <tr><td>New password: </td>"
                    + "         <td><input type=\"password\" name=\"parameter\" /></tr>"
                    + "   <tr><td style=\"text-align: right;\"><a href=\"" + URL + "?action=changepassword\">Cancel</a>&nbsp;</td><td style=\"text-align: left;\">&nbsp;<input type=\"submit\" value=\"Send report\" /></td></tr>"
                    + "   </table>"
                    + "</form>";
        }
        return content;
    }

    private String error404(HttpServletRequest req, HttpServletResponse res) {
        return "<h2>Page not found</h2><p>Sorry. The page you are looking for was not found. Do you wish to go to the <a href=\"" + URL + "?action=main\">main page</a>?</p>";
    }

    private static String confirmationFormHtml(String title, String message, String action, String method, String parameter, String parameter2) {
        return "<h2>" + title + "</h2>"
                + "<form action=\"" + URL + "\" method=\"get\">"
                + "   <p class=\"notice\">" + message + "</p>"
                + "   <input type=\"hidden\" name=\"action\" value=\"" + action + "\" />"
                + "   <input type=\"hidden\" name=\"method\" value=\"" + method + "\" />"
                + "   <input type=\"hidden\" name=\"parameter\" value=\"" + parameter + "\" />"
                + "   <input type=\"hidden\" name=\"parameter2\" value=\"" + parameter2 + "\" />"
                + "   <table>"
                + "      <tr><td>Are you sure? </td><td><select id=\"confirmation\" name=\"confirmation\">"
                + "      <option value=\"false\" selected>No</option>"
                + "      <option value=\"true\">Yes</option>"
                + "   </select></td></tr>"
                + "   <tr><td style=\"text-align: right;\">"
                + "      <a href=\"" + URL + "?action=" + action + "\">Cancel</a>&nbsp;</td>"
                + "<td>&nbsp;<input type=\"submit\" value=\"Confirm\" /></td></tr>"
                + "</table></form>";
    }
}
