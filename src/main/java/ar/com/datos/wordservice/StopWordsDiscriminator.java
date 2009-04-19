package ar.com.datos.wordservice;

import java.util.Set;

/**
 * 
 * @author Marcos J. Medrano
 */
public interface StopWordsDiscriminator extends SessionHandler {

	public boolean isStopWord();
	
	public void processWord(String word);
	
	public Set<String> getStopWords();
	
	public Set<String> getNonStopWords();
}
