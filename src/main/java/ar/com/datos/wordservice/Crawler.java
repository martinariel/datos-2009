package ar.com.datos.wordservice;

import ar.com.datos.documentlibrary.IDocument;

/**
 * Se encarga de agregar documentos al indice y grabarlos en la DocumentLibrary
 * 
 * @author Marcos J. Medrano
 */
public interface Crawler {

	/**
	 * Agrega un Documento al Indexer y lo graba en la DocumentLibrary
	 * Utiliza el Parser para normalizar el documento y el StopWordsDiscriminator
	 * para filtrar stopphrases y stopwords. 
	 * @param {@link IDocument} el documento a indexar y grabar.
	 */
	public void addDocument(IDocument document);
}
