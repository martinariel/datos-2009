package ar.com.datos.bits;

import java.util.Iterator;

/**
 * Interfaz para la obtenci�n de bits desde una secuencia que los almacena.
 * 
 * @author fvalido
 */
public interface BitEmisor {
	/**
	 * El m�todo next() devolver� un bit representado por un byte cuyo valor,
	 * por supuesto, ser� 1 o 0.   
	 */
	public Iterator<Byte> iterator();
}
