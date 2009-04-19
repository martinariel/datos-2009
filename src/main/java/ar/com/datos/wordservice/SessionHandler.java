/**
 * 
 */
package ar.com.datos.wordservice;

/**
 * @author Marcos J. Medrano
 */
public interface SessionHandler {
	
	public void startSession();
	
	public void endSession();
	
	public boolean isActive();
}
