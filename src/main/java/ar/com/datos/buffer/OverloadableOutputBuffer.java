package ar.com.datos.buffer;

import java.util.Collection;

import ar.com.datos.buffer.variableLength.ArrayByte;

public interface OverloadableOutputBuffer extends EntityOutputBuffer {
	/**
	 * Indica si el tama�o almacenado por el buffer es mayor a la capacidad para
	 * la cual fue definida
	 * @return true si el tama�o almacenado es mayor. false en otro caso.  
	 */
	public Boolean isOverloaded();
	/**
	 * Devuelve todos los byte[] que fueron escritos <b>excepto</b> los pertenecientes
	 * a la �ltima entidad que se cerr�
	 * De esta manera libera lo que ocupaban esos datos en el buffer. Esas entidades ya no cuentan c�mo entidades dentro del buffer 
	 */
	public Collection<ArrayByte> extractAllButLast();
	
	/**
	 * Extrae del buffer (permanentemente) todos los byte[] que fueron escritos pertenecientes a la �ltima entidad que se cerr�
	 * De esta manera libera lo que ocupaban esos datos en el buffer. Esas entidades ya no cuentan c�mo entidades dentro del buffer 
	 */
	public Collection<ArrayByte> extractLast();

}
