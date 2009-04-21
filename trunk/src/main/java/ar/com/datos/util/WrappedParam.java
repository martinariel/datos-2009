package ar.com.datos.util;

/**
 * Permite wrappear un objeto para poder devolver cosas extras en un método a través
 * de los métodos que recibe.
 * En general debe evitarse su uso, pero hay casos en que simplifica mucho las cosas
 * y permite generar código más limpio (usar con criterio :P ).
 * 
 * @author fvalido
 *
 * @param <T>
 * Clase del Parametro a usar.
 */
public class WrappedParam<T> {
	/** Objeto wrappeado */
	private T parameter;
	
	/**
	 * Construye un wrapper vacio. Debería ser llenado mediante {@link #setParameter(Object)}.
	 */
	public WrappedParam() {
	}
	/**
	 * Construye un wrapper conteniendo inicialmente el parametro pasado.
	 */
	public WrappedParam(T parameter) {
		this.parameter = parameter;
	}
	/**
	 * Obtiene el parámetro almacenado actualmente.
	 */
	public T getValue() {
		return this.parameter;
	}
	/**
	 * Permite establecer el parámetro a wrappear. 
	 */
	public void setValue(T parameter) {
		this.parameter = parameter;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.parameter.toString();
	}
}
