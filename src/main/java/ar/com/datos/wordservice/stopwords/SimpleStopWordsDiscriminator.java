package ar.com.datos.wordservice.stopwords;

import java.util.LinkedList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import ar.com.datos.util.Tuple;

@SuppressWarnings("unchecked")
public class SimpleStopWordsDiscriminator implements StopWordsDiscriminator {
	
	private Hashtable stopPhrases;
	private Set<String> stopWords;
	
	/**
	 * @param
	 * @param
	 */
	public SimpleStopWordsDiscriminator(Set<String> stopWords, List<List<String>> stopPhrases){
		this.stopWords = stopWords;
		this.stopPhrases = new Hashtable();
		this.constructStopPhrasesHashChain(stopPhrases);
	}
	
	public void constructStopPhrasesHashChain(List<List<String>> phrases){
		Hashtable h;					// auxiliar hastable
		List<String> phrase;			// auxliar list for every phrase
		
		for (int i=0; i < phrases.size(); i++){
			phrase = phrases.get(i);
			h = this.stopPhrases;
			for (int j=0; j < phrase.size(); j++){
				if (h.containsKey(phrase.get(j))){
					h = ((Tuple<Boolean, Hashtable>) h.get(phrase.get(j))).getSecond();
				} else {
					h.put(phrase.get(j),
    					this.createHashChainElement(phrase.subList(j+1, phrase.size())));
					break;
				}
			}
		}
	}
	public Tuple<Boolean, Hashtable> createHashChainElement(List<String> subphrase){
		Hashtable h = new Hashtable();
		boolean isPhraseEnd = subphrase.size() < 1;
		if (!isPhraseEnd){
			h.put(subphrase.get(0),
			    this.createHashChainElement(subphrase.subList(1, subphrase.size())));	
		}
		return new Tuple<Boolean, Hashtable>(isPhraseEnd,h);
	}
	private boolean isStopWord(String word){
		return this.stopWords.contains(word);
	}
	
	public List<String> processPhrase(List<String> phrase) {
		String word, auxWord;
		Hashtable hash = this.stopPhrases;
		List<Tuple<String,Boolean>> tempWords = new LinkedList<Tuple<String,Boolean>>();
		List<String> nonStopWords = new LinkedList<String>();
		Tuple<Boolean,Hashtable> t = null;
		
		// a phrase must contain at least 2 words.
		if (phrase.size()==0) return nonStopWords;
		if (phrase.size()==1){
			word = phrase.get(0);
			if (!this.isStopWord(word)){
				nonStopWords.add(word);
			}
			return nonStopWords;
		}
		// we have at least 2 words in phrase
		/**
		 * sin distincion alguna
		 * 
		 * 1) en todas sus formas
		 */
		int i=0;
		while(!phrase.isEmpty()){
			word = (phrase.size() > i) ? phrase.get(i) : "";
			if (hash.containsKey(word)){
				t = (Tuple<Boolean, Hashtable>) hash.get(word);
				tempWords.add(new Tuple<String,Boolean>(word, t.getFirst()));
				hash = t.getSecond();
				i++;
			} else if (tempWords.size() >= 1){
				boolean mustRemoveWord = true;
				for (int k=i-1; k>0; k--){
					if(tempWords.get(k).getSecond()){
						mustRemoveWord = false;
						for (int j=0; j <= k; j++){
							phrase.remove(0);
						}
					}
				}
				i=0;
				tempWords.clear();
				hash = this.stopPhrases;
				
				if (mustRemoveWord){
					auxWord = phrase.remove(0);
					if (!this.isStopWord(auxWord)){
						nonStopWords.add(auxWord);		// non stop word, keep it!
					}
				}
			} else {
				if (!this.isStopWord(word)){
					nonStopWords.add(word);		// non stop word, keep it!
				}
				phrase.remove(0);
				hash = this.stopPhrases;
				i = 0;
			}
		}
		return nonStopWords; 
	}
}


/*


/*
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


				/*while(!tempWords.isEmpty()){
					auxWord = tempWords.remove(0);
					if (!isStopWord(auxWord)) nonStopWords.add(auxWord);
				}*
*/
