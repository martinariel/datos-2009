package ar.com.datos.compressor.lzp.text.impl;

import java.util.Iterator;

import ar.com.datos.compressor.lzp.text.TextEmisor;
import ar.com.datos.compressor.lzp.text.TextReceiver;
import ar.com.datos.util.StringBufferIterator;

/**
 * Implementación de {@link TextEmisor} y de {@link TextReceiver} en memoria basada en un
 * {@link StringBuffer}.
 * 
 * @author fvalido
 */
public class MemoryTextEmisorAndReceiver implements TextEmisor, TextReceiver {
	/** Estructura usada para guardar el texto recibido y emitir lo pedido. */
	private StringBuffer stringBuffer;

	/**
	 * Constructor.
	 */
	public MemoryTextEmisorAndReceiver() {
		this.stringBuffer = new StringBuffer();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.lzp.text.TextEmisor#iterator(int)
	 */
	@Override
	public Iterator<Character> iterator(int position) {
		return new StringBufferIterator(this.stringBuffer, position);

	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.lzp.text.TextReceiver#addChar(char)
	 */
	@Override
	public void addChar(char character) {
		this.stringBuffer.append(character);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.lzp.text.TextReceiver#getText()
	 */
	@Override
	public String getText() {
		return this.stringBuffer.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getText();
	}
}
