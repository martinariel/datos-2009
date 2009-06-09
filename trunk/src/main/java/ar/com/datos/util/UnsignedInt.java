package ar.com.datos.util;

import ar.com.datos.file.exception.OutOfBoundsException;

/**
 * Representación de un entero !!positivo!! de 32 bits usando solo 32 bits
 * para hacerlo.
 * 
 * @author fvalido
 */
public class UnsignedInt implements Comparable<UnsignedInt> {
	public static final long MAX_VALUE = (long)((1L << 32L) - 1L); 
	private int valueContainer;

	/**
	 * Construye una instancia.
	 * El valor pasado debe estar entre 0 y 2^32-1.
	 * 
	 * @throws OutOfBoundsException
	 * Si no se cumple la condición dicha.
	 */
	public UnsignedInt(long value) throws OutOfBoundsException {
		if (value < 0 || value > MAX_VALUE) {
			throw new OutOfBoundsException();
		}
		
		this.valueContainer = (int)(value + (long)Integer.MIN_VALUE);
	}
	
	/**
	 * Construye una instancia cuyo valor será:
	 * value + (-Integer.MIN_VALUE)
	 */
	public UnsignedInt(int value) {
		this.valueContainer = value;
	}
	
	/**
	 * Devuelve el valor representado como un long.
	 */
	public long longValue() {
		return ((long)this.valueContainer) - ((long)Integer.MIN_VALUE);
	}

	/**
	 * Devuelve la representación interna que se hace del integer guardado.
	 * En general no debería usarse.
	 */
	public int getAsSignedInt() {
		return this.valueContainer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(UnsignedInt o) {
		return new Long(longValue()).compareTo(o.longValue());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new Long(longValue()).hashCode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!UnsignedInt.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		
		UnsignedInt o = (UnsignedInt)obj;
		return this.valueContainer == o.valueContainer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new Long(longValue()).toString();
	}
}
