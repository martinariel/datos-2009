package ar.com.datos.wordservice;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Collection;

import ar.com.datos.parser.Parser;
import ar.com.datos.indexer.SessionIndexer;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.DocumentLibrary;
import ar.com.datos.wordservice.stopwords.StopWordsDiscriminator;
import ar.com.datos.file.variableLength.address.OffsetAddress;

/**
 * Implementacion sencilla de Crawler.
 * 
 * No tiene demasiada logica ya que solo se encarga de orquestar varias clases
 * para agregar un documento.
 * 
 * Deberia ser usado por el Backend principal para agregar documentos.
 * 
 * @author marcos
 */
public class SimpleCrawler implements Crawler{

	private SessionIndexer<OffsetAddress> indexer;
	private Parser parser;
	private StopWordsDiscriminator discriminator;
	private DocumentLibrary documentLibrary;


	public SimpleCrawler(SessionIndexer<OffsetAddress> indexer, StopWordsDiscriminator discriminator, DocumentLibrary documentLibrary){
		this.indexer = indexer;
		this.discriminator = discriminator;
		this.documentLibrary = documentLibrary;
	}
	
	
	@Override
	public Collection<String> addDocument(Document document) {
		List<String> nonStopWords;

		// lexico completo (todos los terminos)
		Set<String> allTerms = new HashSet<String>();
		
		// agrego el documento a la libreria
		OffsetAddress offset = documentLibrary.add(document);
		
		// inicializo una sesion con el indexer
		this.indexer.startSession();
		
		// inicializo el parser con este documento
		this.parser = new Parser(document);
					
		// recorro las frases (ya normalizadas) del documento 
		for(List<String> phrase: this.parser){
			// Agrego los terminos normalizados al lexico
			allTerms.addAll(phrase);
		
			// filtro posibles stopwords y stopphrases
			nonStopWords = this.discriminator.processPhrase(phrase);
			
			// agrego los terminos del lexico al indexer
			this.indexer.addTerms(offset, nonStopWords.toArray(new String[0]));
		}
		
		// finalizo la sesion con el indexer
		this.indexer.endSession();
		
		return allTerms;
	}

}
