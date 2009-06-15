package ar.com.datos.compressor.lzp.table;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.util.UnsignedInt;

/**
 * Contexto junto con su posición de la tabla de trabajo de un LZP. Contiene los dos chars correspondientes al
 * contexto y la posición correspondiente.
 * Implementa {@link Element} de BTree# para que pueda ser persistido en un árbol.
 * 
 * @author fvalido
 */
public class LzpContextPosition implements Element<LzpContext> {
	private char firstChar;
	private char secondChar;
	private int position;
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.elements.Element#getKey()
	 */
	@Override
	public LzpContext getKey() {
		return new LzpContext(this.firstChar, this.secondChar);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.elements.Element#updateElement(ar.com.datos.btree.elements.Element)
	 */
	@Override
	public boolean updateElement(Element<LzpContext> element) {
		LzpContextPosition lzpContextPosition = (LzpContextPosition)element;
		boolean returnValue = (this.position == lzpContextPosition.position); 
		
		this.position = lzpContextPosition.position;
		
		return returnValue;
	}


	/**
	 * Constructor.
	 */
	public LzpContextPosition(LzpContext lzpContext, UnsignedInt position) {
		this.firstChar = lzpContext.getFirstChar();
		this.secondChar = lzpContext.getSecondChar();
		this.position = position.getAsSignedInt();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getKey().hashCode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !LzpContextPosition.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		
		LzpContextPosition o = (LzpContextPosition) obj;
		
		return this.getKey().equals(o.getKey());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "<" + this.getKey().toString() + "=" + getPosition() + ">"; 
	}

	/**
	 * Permite obtener la posición.
	 */
	public UnsignedInt getPosition() {
		return new UnsignedInt(this.position);
	}
}
