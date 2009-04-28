package ar.com.datos.trie;

import ar.com.datos.serializer.Serializable;

public interface Trie <K extends Serializable<T> , T>{

	/**
	 * Agrega un K al trie
	 * @param key
	 * @param element
	 */
	void add(String key, K element);
	
	/**
	 * Retorna el K elemento por su key
	 * Sino existe retorna NULL
	 * 
	 * @param key
	 * @return
	 */
	K find(String key);
	
	
	/**
	 * Elimina un elemento y lo devuelve
	 * @param key
	 */
	K delete(String key);
	
	
}
