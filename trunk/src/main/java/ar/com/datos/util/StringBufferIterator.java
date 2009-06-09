package ar.com.datos.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterador de un {@link StringBuffer}
 * 
 * @author fvalido
 */
public class StringBufferIterator implements Iterator<Character> {
	/** {@link StringBuffer} a iterar */
	private StringBuffer stringBuffer;
	/** Posición siguiente a iterar */
	private int nextPosition;
	
	/**
	 * Constructor. Recibe el {@link StringBuffer} a iterar y la posición inicial 
	 * por la cual arrancar.
	 */
	public StringBufferIterator(StringBuffer stringBuffer, int initialPosition) {
		this.stringBuffer = stringBuffer;
		this.nextPosition = initialPosition;
	}

	/**
	 * Constructor. Recibe el {@link StringBuffer} a iterar y lo itera desde
	 * el principio.
	 */
	public StringBufferIterator(StringBuffer stringBuffer) {
		this(stringBuffer, 0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.nextPosition < this.stringBuffer.length() - 1;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Character next() {
		try {
			Character returnValue = this.stringBuffer.charAt(this.nextPosition);
			this.nextPosition++;
			
			return returnValue;
		} catch (IndexOutOfBoundsException e) {
			throw new NoSuchElementException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
