/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.framework;

/**
 *
 * @author jblew
 */
public interface ExternalService extends Service {
    public int getPort();
    public boolean isBind();
}
