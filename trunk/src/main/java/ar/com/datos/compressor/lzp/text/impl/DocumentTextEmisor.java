package ar.com.datos.compressor.lzp.text.impl;

import java.util.Iterator;

import ar.com.datos.compressor.lzp.text.TextEmisor;
import ar.com.datos.documentlibrary.Document;

/**
 * Implementación de {@link TextEmisor} basada en un {@link Document}.
 * 
 * @author fvalido
 */
public class DocumentTextEmisor implements TextEmisor {
	/** {@link Document} interno a ser usado. */
	private Document document;
	
	/**
	 * Constructor. Recibe el {@link Document} que será usado para las emisiones.
	 */
	public DocumentTextEmisor(Document document) {
		this.document = document;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.lzp.text.TextEmisor#iterator(long)
	 */
	@Override
	public Iterator<Character> iterator(int position) {
		return this.document.getCharacterIterator(position);
	}
}
