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
	private LzpContext lzpContext;
	private UnsignedInt position;
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.elements.Element#getKey()
	 */
	@Override
	public LzpContext getKey() {
		return this.lzpContext;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.elements.Element#updateElement(ar.com.datos.btree.elements.Element)
	 */
	@Override
	public boolean updateElement(Element<LzpContext> element) {
		LzpContextPosition lzpContextPosition = (LzpContextPosition)element;
		boolean returnValue = (this.position.equals(lzpContextPosition.position)); 
		
		this.position = lzpContextPosition.position;
		
		return returnValue;
	}


	/**
	 * Constructor.
	 */
	public LzpContextPosition(LzpContext lzpContext, UnsignedInt position) {
		this.lzpContext = lzpContext;
		this.position = position;
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
		
		return this.lzpContext.equals(o.lzpContext);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "<" + this.lzpContext.toString() + "=" + this.position.toString() + ">"; 
	}

	/**
	 * Permite obtener la posición.
	 */
	public UnsignedInt getPosition() {
		return position;
	}
}
