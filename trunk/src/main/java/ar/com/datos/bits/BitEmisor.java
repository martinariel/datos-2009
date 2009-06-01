package ar.com.datos.bits;

import java.util.Iterator;

/**
 * Interfaz para la obtención de bits desde una secuencia que los almacena.
 * 
 * @author fvalido
 */
public interface BitEmisor {
	/**
	 * El método next() devolverá un bit representado por un byte cuyo valor,
	 * por supuesto, será 1 o 0.   
	 */
	public Iterator<Byte> iterator();
}
