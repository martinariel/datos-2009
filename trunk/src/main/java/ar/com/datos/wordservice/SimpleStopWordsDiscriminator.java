package ar.com.datos.wordservice;

import java.util.Set;

import ar.com.datos.wordservice.exception.ActiveSessionException;

public class SimpleStopWordsDiscriminator implements StopWordsDiscriminator {

	private boolean sessionActive;
	private Set<String> stopwords;
	private Set<String> sessionStopWords;
	private Set<String> sessionNonStopWords;
	
	public SimpleStopWordsDiscriminator(Set stopwords){
		this.stopwords = stopwords;
	}
	
	@Override
	public Set<String> getNonStopWords() {
		if (this.isActive()) throw new ActiveSessionException("Cannot get session NonStopWords, the session is still active.");
		return this.sessionNonStopWords;
	}

	@Override
	public Set<String> getStopWords() {
		if (this.isActive()) throw new ActiveSessionException("Cannot get session StopWords, the session is still active.");
		return this.sessionStopWords;
	}

	private boolean isStopWord(String word){
		return this.stopwords.contains(word);
	}
	
	@Override
	public void processWord(String word) {
		if (this.isStopWord(word)){ 
			this.sessionStopWords.add(word);
		} else {
			this.sessionNonStopWords.add(word);
		}
	}

	@Override
	public void endSession() {
		this.sessionActive = false;
	}
	@Override
	public void startSession() {
		this.sessionActive = true;
	}
	@Override
	public boolean isActive() {
		return this.sessionActive;
	}

}
