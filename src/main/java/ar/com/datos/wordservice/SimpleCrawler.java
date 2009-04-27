package ar.com.datos.wordservice;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Collection;

import ar.com.datos.parser.Parser;
import ar.com.datos.indexer.SessionIndexer;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.wordservice.stopwords.StopWordsDiscriminator;

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

	private SessionIndexer<Document> indexer;
	private Parser parser;
	private StopWordsDiscriminator discriminator;


	public SimpleCrawler(SessionIndexer<Document> indexer, StopWordsDiscriminator discriminator){
		this.indexer = indexer;
		this.discriminator = discriminator;
	}
	
	
	@Override
	public void addDocument(Document document) {
		List<String> nonStopWords;
		
		// lexico completo (todos los terminos)
		Set<String> allTerms = new HashSet<String>();
		
		// inicializo una sesion con el indexer
		this.indexer.startSession();
		
		// inicializo el parser con este documento
		this.parser = new Parser(document);
		this.parser.initParser();
		
		// recorro las frases (ya normalizadas) del documento 
		for(Collection<String> phrase: this.parser){
			// Agrego los terminos normalizados al lexico
			allTerms.addAll(phrase);
		
			// filtro posibles stopwords y stopphrases
			nonStopWords = this.discriminator.processPhrase(new LinkedList<String>(phrase));
			
			// agrego los terminos del lexico al indexer
			this.indexer.addTerms(document, nonStopWords.toArray(new String[0]));
		}
		
		// finalizo la sesion con el indexer
		this.indexer.endSession();
	}

}
