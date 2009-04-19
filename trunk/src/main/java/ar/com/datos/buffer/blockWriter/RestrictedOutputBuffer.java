package ar.com.datos.buffer.blockWriter;

import java.util.Collection;
import java.util.Deque;

import ar.com.datos.buffer.EntityOutputBuffer;
import ar.com.datos.buffer.variableLength.ArrayByte;

public interface RestrictedOutputBuffer extends EntityOutputBuffer {
	/**
	 * Indica si el tamaño almacenado por el buffer es mayor a la capacidad para
	 * la cual fue definida
	 * @return true si el tamaño almacenado es mayor. false en otro caso.  
	 */
	public Boolean isOverloaded();
	/**
	 * Devuelve una Deque donde cada elemento corresponde a una entidad que fue cerrada
	 */
	public Deque<Collection<ArrayByte>> retrieveEntities();
	
	/**
	 * Agrega una entidad completa (ejecuta closeEntity)
	 */
	public void addEntity(Collection<ArrayByte> last);

}
