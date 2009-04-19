package ar.com.datos.buffer;

import java.util.Collection;
import java.util.Deque;

import ar.com.datos.buffer.variableLength.ArrayByte;

public interface RestrictedOutputBuffer extends EntityOutputBuffer {
	/**
	 * Indica si el tama�o almacenado por el buffer es mayor a la capacidad para
	 * la cual fue definida
	 * @return true si el tama�o almacenado es mayor. false en otro caso.  
	 */
	public Boolean isOverloaded();
	/**
	 * Devuelve una Deque donde cada elemento corresponde a una entidad que fue cerrada
	 */
	public Deque<Collection<ArrayByte>> retrieveEntities();
	
	/**
	 * Elimina la �ltima entidad que fue agregada
	 */
	public void removeLastEntity();
	
	/**
	 * Agrega una entidad completa (ejecuta closeEntity)
	 */
	public void addEntity(Collection<ArrayByte> last);

}
