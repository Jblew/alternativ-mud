/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.lifecycle;

/**
 *
 * @author jblew
 */
public interface RunnableTask {
    /**
     * Opis wykonywanego zadania.
     */
    public String getDescription();
    
    /**
     * Ta metoda mówi, czy RunnableTask powinno być wykonane.
     */
    public boolean shouldBeExecuted();
    
    /**
     * Wykonaj runnable task.
     */
    public void execute() throws Exception;
}
