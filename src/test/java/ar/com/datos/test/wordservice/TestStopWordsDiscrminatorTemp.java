package ar.com.datos.test.wordservice;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import ar.com.datos.util.Tuple;

public class TestStopWordsDiscrminatorTemp extends TestCase {
	
	private List<List<String>> stopPhrases;
	
	public void setUp(){
		stopPhrases = new LinkedList<List<String>>();
		
		String stopPhrase1 = "en consecuencia";
		String stopPhrase2 = "en consecuencia de";
		String stopPhrase3 = "cualquier otra";
		String stopPhrase6 = "en todas sus formas";
		String stopPhrase7 = "en todas las formas";
		stopPhrases.add(Arrays.asList(stopPhrase1.split(" ")));
		stopPhrases.add(Arrays.asList(stopPhrase2.split(" ")));
		stopPhrases.add(Arrays.asList(stopPhrase3.split(" ")));
		stopPhrases.add(Arrays.asList(stopPhrase6.split(" ")));
		stopPhrases.add(Arrays.asList(stopPhrase7.split(" ")));
	}
	@SuppressWarnings("unchecked")
	public void testHashConstruction(){
		Hashtable h = this.constructStopPhrasesHashChain(stopPhrases);
		
		assertTrue(h.containsKey("en"));
		assertTrue(h.containsKey("cualquier"));
		
		Tuple<Boolean, Hashtable> t = (Tuple<Boolean, Hashtable>) h.get("cualquier");
		assertFalse(t.getFirst());
		assertTrue(t.getSecond().containsKey("otra"));
		
		t = (Tuple<Boolean, Hashtable>) t.getSecond().get("otra");
		assertTrue(t.getFirst());
		
		t = (Tuple<Boolean, Hashtable>) h.get("en");
		assertFalse(t.getFirst());
		assertTrue(t.getSecond().containsKey("consecuencia"));
		assertTrue(t.getSecond().containsKey("todas"));
		
		t = (Tuple<Boolean, Hashtable>) t.getSecond().get("consecuencia");
		assertTrue(t.getSecond().containsKey("de"));
		assertTrue(t.getFirst());
		
		t = (Tuple<Boolean, Hashtable>) t.getSecond().get("de");
		assertTrue(t.getFirst());
	}
	
	@SuppressWarnings("unchecked")
	private Hashtable constructStopPhrasesHashChain(List<List<String>> phrases){
		Hashtable h;
		List<String> phrase;
		Hashtable sessionStopPhrases = new Hashtable();
		
		for (int i=0; i < phrases.size(); i++){
			phrase = phrases.get(i);
			h = sessionStopPhrases;
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
		return sessionStopPhrases;
	}
	@SuppressWarnings("unchecked")
	private Tuple<Boolean, Hashtable> createHashChainElement(List<String> subphrase){
		Hashtable h = new Hashtable();
		boolean isPhraseEnd = subphrase.size() < 1;
		if (!isPhraseEnd){
			h.put(subphrase.get(0),
			    this.createHashChainElement(subphrase.subList(1, subphrase.size())));	
		}
		return new Tuple<Boolean, Hashtable>(isPhraseEnd,h);
	}
	
}
