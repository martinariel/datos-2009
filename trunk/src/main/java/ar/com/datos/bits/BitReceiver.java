package ar.com.datos.bits;

/**
 * Interfaz para la entrada de bits a una secuencia que los almacena.  
 * 
 * @author fvalido
 */
public interface BitReceiver {
	/**
	 * Agrega un bit a continuaci�n en la secuencia.
	 * Por supuesto el bit pasado debe ser un 1 o un 0.
	 */
	public void addBit(byte bit);

	/**
	 * Agrega un 0 a continuaci�n en la secuencia.
	 */
	public void add0();
	
	/**
	 * Agrega un 1 a continuaci�n en la secuencia.
	 */
	public void add1();
	
	/**
	 * Permite indicar que ya no se agregar�n m�s bits a la secuencia. 
	 */
	public void close();
}
