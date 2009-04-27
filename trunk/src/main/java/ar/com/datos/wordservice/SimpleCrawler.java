package ar.com.datos.wordservice;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import ar.com.datos.documentlibrary.IDocument;
import ar.com.datos.indexer.Indexer;
import ar.com.datos.indexer.SessionIndexer;
import ar.com.datos.parser.Parser;
import ar.com.datos.wordservice.stopwords.StopWordsDiscriminator;


public class SimpleCrawler<T> implements Crawler{

	private SessionIndexer<T> indexer;
	private Parser parser;
	private StopWordsDiscriminator discriminator;


	public SimpleCrawler(SessionIndexer<T> indexer, StopWordsDiscriminator discriminator){
		this.indexer = indexer;
		this.discriminator = discriminator;
	}
	
	
	@Override
	public void addDocument(IDocument document) {
		Set<String> allTerms;
		Collection<String> words;
		
		// inicializo el parser con este documento
		this.parser = new Parser(document);
		this.parser.initParser();
		
		// obtengo las palabras normalizadas desde el documento 
		words = this.parser.getCurrentWords();
		
		// guardo el lexico completo (con todos los terminos)
		allTerms = new HashSet<String>(words);
		
		// filtro stopwords y stopphrases
		this.discriminator.processPhrase(new LinkedList<String>(words));
		
		// inicializo una sesion con el indexer
		this.indexer.startSession();
		
		// agrego uno a uno los terminos del lexico al indexer
		Iterator<String> it = allTerms.iterator();
		while(it.hasNext()){
			//this.indexer.addTerms(dato, terms);  ???
		}
		
		// finalizo la sesion con el indexer
		this.indexer.endSession();
	}

}
