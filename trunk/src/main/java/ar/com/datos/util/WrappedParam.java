package ar.com.datos.util;

/**
 * Permite wrappear un objeto para poder devolver cosas extras en un m�todo a trav�s
 * de los m�todos que recibe.
 * En general debe evitarse su uso, pero hay casos en que simplifica mucho las cosas
 * y permite generar c�digo m�s limpio (usar con criterio :P ).
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
	 * Construye un wrapper vacio. Deber�a ser llenado mediante {@link #setParameter(Object)}.
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
	 * Obtiene el par�metro almacenado actualmente.
	 */
	public T getValue() {
		return this.parameter;
	}
	/**
	 * Permite establecer el par�metro a wrappear. 
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
