/**
 * 
 */
package ar.com.datos.wordservice;

/**
 * Interfaz que deberian implementar las clases que deseen manejar una session.
 * Cada clase puede implementar la funcionalidad necesaria al iniciar o 
 * finalizar la sesion.
 * 
 * @author Marcos J. Medrano
 */
public interface SessionHandler {
	
	/** Inicializa la sesion */
	public void startSession();
	
	/** Finaliza la sesion */
	public void endSession();
	
	/** Indica si la session esta activa o no */
	public boolean isActive();
}
