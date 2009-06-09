package ar.com.datos.compressor.lzp.table;

import ar.com.datos.btree.elements.Key;

/**
 * Contexto de la tabla de contextos de un LZP. Contiene los dos chars correspondientes al
 * contexto.
 * Implementa {@link Key} de BTree# para que pueda ser persistido en un árbol.
 * 
 * @author fvalido
 */
public class LzpContext implements Key {
	private char firstChar;
	private char secondChar;
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Key o) {
		LzpContext obj = (LzpContext)o;
		
		long difference = ((long)this.firstChar) - ((long)obj.firstChar);
		if (difference == 0) {
			difference = ((long)this.secondChar) - ((long)obj.secondChar);
		}
		
		return (difference > 0) ? 1 : ((difference < 0) ? -1 : 0);
	}

	/**
	 * Constructor.
	 */
	public LzpContext(char firstChar, char secondChar) {
		super();
		this.firstChar = firstChar;
		this.secondChar = secondChar;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!LzpContext.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		
		LzpContext o = (LzpContext) obj;
		return (this.firstChar == o.firstChar && this.secondChar == o.secondChar);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new Character(this.firstChar).hashCode() + new Character(this.secondChar).hashCode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new String(new char[] {this.firstChar, this.secondChar});
	}

	/**
	 * Permite obtener el primer caracter del contexto.
	 */
	public char getFirstChar() {
		return firstChar;
	}

	/**
	 * Permite obtener el segundo caracter del contexto.
	 */
	public char getSecondChar() {
		return secondChar;
	}
}
