package ar.com.datos.buffer;

import ar.com.datos.buffer.exception.BufferException;

/**
 * Se encarga de manejar un buffer interno para operaciones de lectura.
 * Utiliza un array interno como buffer para los bytes que se van leyendo
 */
public interface InputBuffer {
	/**
	 * Lee una cantidad n de datos del buffer, siendo n el size del array de data.
	 * 
	 * @param data un array con el size que se quiera leer en el buffer
	 * @return el array recibido por parametro ya modificado con los datos leidos
	 * @throws BufferException si no hay suficientes datos para leer desde el buffer
	 */
	public byte[] read(byte[] data) throws BufferException;
	
	/**
	 * Lee el siguiente byte de datos.
	 * 
	 * @return el siguiente byte leido desde el buffer.
	 * @throws BufferException si no hay suficientes datos para leer desde el buffer
	 */
	public byte read() throws BufferException;
}
