package ar.com.datos.compressor.lzp.text;

import java.util.Iterator;

/**
 * Interface para un emisor de texto.
 * 
 * @author fvalido
 */
public interface TextEmisor {
//	/**
//	 * Cantidad de caracteres que posee actualmente el texto.
//	 */
//	public int textSize();
// No es necesario.
	
	/**
	 * Devuelve un iterador de caracteres a partir de la posición pasada.
	 * El primer caracter es el 0.
	 */
	public Iterator<Character> iterator(int position);
}
