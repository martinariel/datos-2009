package ar.com.datos.wordandsoundservice;

import java.io.InputStream;
import ar.com.datos.wordandsoundservice.exception.WordIsAlreadyRegisteredException;
import ar.com.datos.wordandsoundservice.exception.UnregisteredWordException;



/**
 * Words and Sound Service
 * Permite guardar y recuperar palabras con sus respectivos InputStream.
 * 
 */


public interface WaSService {

	/**
	 * Devuelve el InputStream de la palabra pasada como parámetro.
	 * Si la cadena no esta registrada anteriormente tira una excepción.
	 * 
	 */
	public InputStream readWord( String word )
		throws UnregisteredWordException;
	
	
	/**
	 * Permite agregar una palabra y su respectivo InputStream.
	 * Si la Palabra ya fue cargada anteriormente tira una excepción.
	 * 
	 */
	public void addWord( String word , InputStream stream )
		throws WordIsAlreadyRegisteredException;
}
