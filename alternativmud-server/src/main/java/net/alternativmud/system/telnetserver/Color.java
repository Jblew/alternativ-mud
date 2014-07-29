/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.telnetserver;

/**
 *
 * @author jblew
 */
public enum Color {

    BLACK("v", "{v", "\033[0;30m"),
    DARK_GRAY("V", "{V", "\033[1;30m"),
    DARK_RED("r", "{r", "\033[0;31m"),
    LIGHT_RED("R", "{R", "\033[1;31m"),
    DARK_GREEN("g", "{g", "\033[0;32m"),
    LIGHT_GREEN("G", "{G", "\033[1;32m"),
    YELLOW("Y", "{Y", "\033[1;33m"),
    BROWN("y", "{y", "\033[0;33m"),
    LIGHT_BLUE("B", "{B", "\033[1;34m"),
    DARK_BLUE("b", "{b", "\033[0;34m"),
    MAGENTA("m", "{m", "\033[0;35m"),
    PINK("M", "{M", "\033[1;35m"),
    LIGHT_CYAN("C", "{C", "\033[1;36m"),
    DARK_CYAN("c", "{c", "\033[0;36m"),
    LIGHT_GRAY("w", "{w", "\033[0;37m"),
    WHITE("x", "{x", "\033[0m"),
    BOLD_WHITE("X", "{X", "\033[1;38m");
    
    private final String letter;
    private final String mark;
    private final String terminalCode;
    private final String htmlCode;

    private Color(String letter_, String mark_, String terminalCode_) {
        letter = letter_;
        mark = mark_;
        terminalCode = terminalCode_;
        htmlCode = "</span><span class=\""+this.name().toLowerCase()+"\">";
    }

    public String getLetter() {
        return letter;
    }

    public String getMark() {
        return mark;
    }

    public String getTerminalCode() {
        return terminalCode;
    }
    
    public String getHtmlCode() {
        return htmlCode;
    }

    public static String colorify(String in) {
        in = in.replace("{{", "85,,[123egr43453453455hrth6*&]");
        for (Color c : Color.values()) {
            in = in.replace(c.getMark(), c.getTerminalCode());
        }
        return in.replace("85,,[123egr43453453455hrth6*&]", "{");
    }
    
    public static String colorifyHtml(String in) {
        in = in.replace("  ", "&nbsp;&nbsp;").replace(">", "&gt;").replace("<", "&lt;").replace("\"", "&quot;").replace("\n", "<br />") + "<span>";
        for (Color c : Color.values()) {
            in = in.replace(c.getMark(), c.getHtmlCode());
        }
        return in+"</span>";
    }
}
