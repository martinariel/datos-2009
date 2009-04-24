package ar.com.datos.wordservice;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class SimpleStopWordsDiscriminator implements StopWordsDiscriminator {
	
	private Hashtable stopPhrases;
	private Set<String> stopWords;
	
	public SimpleStopWordsDiscriminator(Set<String> stopWords, Hashtable stopPhrases){
		this.stopWords = stopWords;
		this.stopPhrases = stopPhrases;
	}
	
	private boolean isStopWord(String word){
		return this.stopWords.contains(word);
	}
	
	@ Override
	public List<String> processPhrase(List<String> phrase) {
		String word;
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
		
		int i = 0;
		while(!phrase.isEmpty()){
			word = phrase.get(i);
			if (hash.containsKey(word)){
				tempWords.add(word);
				hash = (Hashtable) hash.get(word);
				if (hash == null){ 			// stop phrase found!
					tempWords.clear();
					for (int j=0; j<i; j++) // remove stop phrase from 'phrase'
						phrase.remove(j);	// we only need NON-stop words/phrases
					i=0;
				}
				i++;
			} else {
				if (this.isStopWord(word)){
					phrase.remove(i);		// is stop word, forget it
				} else {
					nonStopWords.add(phrase.remove(i));	// non stop word, keep it!
				}
			}
		}
		return nonStopWords; 
	}
}
