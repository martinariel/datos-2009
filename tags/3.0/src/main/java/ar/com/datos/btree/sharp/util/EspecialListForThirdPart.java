package ar.com.datos.btree.sharp.util;

import java.util.List;

import ar.com.datos.serializer.Serializer;

/**
 * Permite juntar una lista con su tamaño de serialización.
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
	 * Indica donde se pondrán los elementos a agregar o de donde se sacarán
	 * los elementos a sacar en el método {@link #giveOneElementTo(EspecialListForThirdPart)} 
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
	 * Agrega un elemento en la lista en la posición pasada.
	 */
	public void add(int index, T t) {
		this.realList.add(index, t);
	}

	/**
	 * Quita un elemento de la lista en la posición pasada y lo devuelve.
	 */
	public T remove(int index) {
		return this.realList.remove(index);
	}

	/**
	 * Obtiene el tamaño de serialización de la lista.
	 */
	public long size() {
		return this.serializer.getDehydrateSize(this.realList);
	}

	/**
	 * Obtiene el tamaño real de la lista original.
	 */
	public int listSize() {
		return this.realList.size();
	}
	
	/**
	 * Saca un elemento de aquí y lo agrega en end. La posición de sacado o de agregado dependen
	 * de lo especificado por el parámetro left en el constructor.
	 */
	public void giveOneElementTo(EspecialListForThirdPart<T> end) {
		end.add(end.getAddPosition(), remove(getRemovePosition()));
	}
	
	/**
	 * Obtiene la posición para remover elementos
	 */
	private int getRemovePosition() {
		return (left) ? 0 : listSize() - 1;
	}
	
	/**
	 * Obtiene la posición para agregar elementos
	 */
	private int getAddPosition() {
		return (left) ? 0 : listSize();
	}

}
