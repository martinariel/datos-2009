package ar.com.datos.wordservice;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class SimpleStopWordsDiscriminator implements StopWordsDiscriminator {
	
	private Hashtable stopPhrases;
	private Set<String> stopWords;
	private Object nullReference;
	
	/**
	 * @param
	 * @param
	 */
	public SimpleStopWordsDiscriminator(Set<String> stopWords, List<List<String>> stopPhrases){
		this.nullReference = new Object();
		this.stopWords = stopWords;
		this.stopPhrases = new Hashtable();
		this.constructStopPhrasesHashChain(stopPhrases);
	}
	private void constructStopPhrasesHashChain(List<List<String>> phrases){
		List<String> phrase;
		for (int i=0; i < phrases.size(); i++){
			phrase = phrases.get(i);
			this.stopPhrases.put(phrase.get(0), 
                this.createHashChainElement(phrase.subList(1, phrase.size()-1)));
		}
	}
	private Object createHashChainElement(List<String> subphrase){
		if (subphrase.size()>1){
			Hashtable h = new Hashtable();
			h.put(subphrase.get(0), 
	            this.createHashChainElement(subphrase.subList(1, subphrase.size()-1)));
			return h;
		} else {
			return this.nullReference;
		}
	}
	private boolean isStopWord(String word){
		return this.stopWords.contains(word);
	}
	
	public List<String> processPhrase(List<String> phrase) {
		String word, auxWord;
		Hashtable hash = this.stopPhrases;
		List<String> tempWords = new LinkedList<String>();
		List<String> nonStopWords = new LinkedList<String>();
		
		// a phrase must contain at least 2 words.
		if (phrase.size()==0) return nonStopWords;
		if (phrase.size()==1){
			word = phrase.get(0);
			if (this.isStopWord(word)){
				nonStopWords.add(word);
			}
			return nonStopWords;
		}
		// we have at least 2 words in phrase
		
		while(!phrase.isEmpty()){
			word = phrase.remove(0);
			if (hash.containsKey(word)){
				tempWords.add(word);
				try{
					hash = (Hashtable) hash.get(word);
				} catch(Exception e){
					tempWords.clear();
					hash = this.stopPhrases;
				}
			} else if (this.stopPhrases.containsKey(word)){
				while(!tempWords.isEmpty()){
					auxWord = tempWords.remove(0);
					if (!isStopWord(auxWord)) nonStopWords.add(auxWord);
				}
				tempWords.add(word);
				hash = (Hashtable) hash.get(word);
			} else {
				while(!tempWords.isEmpty()){
					auxWord = tempWords.remove(0);
					if (!isStopWord(auxWord)) nonStopWords.add(auxWord);
				}
				hash = this.stopPhrases;
				if (!this.isStopWord(word)){
					nonStopWords.add(word);		// non stop word, keep it!
				}
			}
		}
		return nonStopWords; 
	}
}
