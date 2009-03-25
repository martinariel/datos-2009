package ar.com.datos.buffer;

import ar.com.datos.buffer.exception.BufferException;

/**
 * Interfacez creada para hacer algunas pruebas
 * debería ser reemplazada por la de Marcos
 * @author Juan Manuel Barreneche
 *
 */
public interface InputBuffer {
	public byte[] read(byte[] data) throws BufferException;
	public byte read() throws BufferException;
}
