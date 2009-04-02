package ar.com.datos.persistencia;

import java.io.InputStream;
import ar.com.datos.persistencia.exception.WordIsAlreadyRegisteredException;
import ar.com.datos.persistencia.exception.UnregisteredWordException;


/**
 * Words and Sound Service
 * Permite guardar y recuperar palabras con sus respectivos InputStream.
 * 
 */


public interface SoundPersistenceService {

	/**
	 * Permite agregar una palabra y su respectivo InputStream.
	 * Si la Palabra ya fue cargada anteriormente entonces lanza una excepci�n.
	 * 
	 */
	public void addWord( String word , InputStream stream )
		throws WordIsAlreadyRegisteredException;
	
	
	/**
	 * Devuelve el InputStream de la palabra pasada como par�metro.
	 * Si la cadena no esta registrada anteriormente arroja una excepci�n.
	 * 
	 */
	public InputStream readWord( String word )
		throws UnregisteredWordException;
	
	
	
	/**
	 * Informa si la palabra se encuentra registrada o no.
	 * 
	 */
	public boolean isRegistered( String word );
}
