package ar.com.datos.btree.sharp.util;

import java.util.List;

import ar.com.datos.serializer.Serializer;

/**
 * Permite juntar una lista con su tama�o de serializaci�n.
 * Utilizada como facilidad auxiliar para obtener tercios en implementaciones
 * de Node.
 * 
 * @author fvalido
 */
public class EspecialListForThirdPart<T> {
	/** Lista con la que se trabaja */
	protected List<T> realList;
	/** Serializador de la lista */
	protected Serializer<List<T>> serializer;
	/** 
	 * Indica donde se pondr�n los elementos a agregar o de donde se sacar�n
	 * los elementos a sacar en el m�todo {@link #giveOneElementTo(EspecialListForThirdPart)} 
	 */
	private boolean left;
	
	public EspecialListForThirdPart(List<T> realList, Serializer<List<T>> serializer, boolean left) {
		this.realList = realList;
		this.serializer = serializer;
		this.left = left;
	}
	
	/**
	 * Agrega un elemento en la lista.
	 */
	public boolean add(T t) {
		return this.realList.add(t);
	}

	/**
	 * Agrega un elemento en la lista en la posici�n pasada.
	 */
	public void add(int index, T t) {
		this.realList.add(index, t);
	}

	/**
	 * Quita un elemento de la lista en la posici�n pasada y lo devuelve.
	 */
	public T remove(int index) {
		return this.realList.remove(index);
	}

	/**
	 * Obtiene el tama�o de serializaci�n de la lista.
	 */
	public long size() {
		return this.serializer.getDehydrateSize(this.realList);
	}

	/**
	 * Obtiene el tama�o real de la lista original.
	 */
	public int listSize() {
		return this.realList.size();
	}
	
	/**
	 * Saca un elemento de aqu� y lo agrega en end. La posici�n de sacado o de agregado dependen
	 * de lo especificado por el par�metro left en el constructor.
	 */
	public void giveOneElementTo(EspecialListForThirdPart<T> end) {
		end.add(end.getAddPosition(), remove(getRemovePosition()));
	}
	
	/**
	 * Obtiene la posici�n para remover elementos
	 */
	private int getRemovePosition() {
		return (left) ? 0 : listSize() - 1;
	}
	
	/**
	 * Obtiene la posici�n para agregar elementos
	 */
	private int getAddPosition() {
		return (left) ? 0 : listSize();
	}

}
