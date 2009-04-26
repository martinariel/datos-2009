/**
 * 
 */
package ar.com.datos.test.wordservice;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ar.com.datos.wordservice.stopwords.SimpleStopWordsDiscriminator;
import ar.com.datos.wordservice.stopwords.StopWordsDiscriminator;
import junit.framework.TestCase;

/**
 * @author marcos
 *
 */
public class TestStopWordsDiscriminator extends TestCase {
	private StopWordsDiscriminator discriminator;
	
	public TestStopWordsDiscriminator(){
		
	}
	public void setUp(){
		Set<String> stopWords = new HashSet<String>();
		List<List<String>> stopPhrases = new LinkedList<List<String>>();
		
		this.loadStopWords(stopWords);
		this.loadStopPhrases(stopPhrases);

		this.discriminator = new SimpleStopWordsDiscriminator(stopWords, stopPhrases);
	}
	
	// A continuacion testeo algunas frases que son articulos de la 
	// Declaracion Universal de los Derechos Humanos
	
	
	public void testPhraseArt1(){
		LinkedList<String> phrase = new LinkedList<String>();
		String art1 = "Todos los seres humanos nacen libres e iguales en dignidad " +
			"y derechos y dotados como estan de razon y conciencia deben comportarse " +
			"fraternalmente los unos con los otros";
		String expected = "seres humanos nacen libres iguales dignidad derechos " +
				"dotados razon conciencia deben comportarse fraternalmente";
		phrase.addAll(Arrays.asList(art1.split(" ")));
		assertEquals(expected, formatResult(this.discriminator.processPhrase(phrase)));
	}
	
	public void testPhraseArt2(){
		LinkedList<String> phrase = new LinkedList<String>();
		String art2 = "Toda persona tiene los derechos y libertades proclamados en " +
				"esta Declaracion sin distincion alguna de raza color sexo idioma religion " +
				"opinion politica o de cualquier otra indole origen nacional o social " +
				"posicion economica nacimiento o cualquier otra condicion";
		String expected = "persona derechos libertades proclamados Declaracion " +
				"raza color sexo idioma religion opinion politica nacional social " +
				"economica nacimiento condicion";
		phrase.addAll(Arrays.asList(art2.split(" ")));
		assertEquals(expected, formatResult(this.discriminator.processPhrase(phrase)));
	}
	
	public void testPhraseArt3(){
		LinkedList<String> phrase = new LinkedList<String>();
		String art3 = "Todo individuo tiene derecho a la vida a la libertad y " +
				"a la seguridad de su persona";
		String expected = "individuo vida libertad seguridad persona";
		phrase.addAll(Arrays.asList(art3.split(" ")));
		assertEquals(expected, formatResult(this.discriminator.processPhrase(phrase)));
	}
	
	public void testPhraseArt4(){
		LinkedList<String> phrase = new LinkedList<String>();
		String art4 = "Nadie estara sometido a esclavitud ni a servidumbre la " +
				"esclavitud y la trata de esclavos estan prohibidas en todas sus formas";
		String expected = "sometido esclavitud servidumbre esclavitud esclavos prohibidas";
		phrase.addAll(Arrays.asList(art4.split(" ")));
		assertEquals(expected, formatResult(this.discriminator.processPhrase(phrase)));
	}
	
	public void testPhraseArt5(){
		LinkedList<String> phrase = new LinkedList<String>();
		String art29 = "Toda persona tiene deberes respecto a la comunidad puesto que " +
			"solo en ella puede desarrollar libre y plenamente su personalidad";
		String expected = "persona deberes comunidad desarrollar libre personalidad";
		phrase.addAll(Arrays.asList(art29.split(" ")));
		assertEquals(expected, formatResult(this.discriminator.processPhrase(phrase)));
	}
	public void testPhrase(){
		LinkedList<String> phrase = new LinkedList<String>();
		String art29 = "en consecuencia en consecuencia cualquier otra sin distincion " +
				"alguna cualquier otra sin distincion otra tiene en consecuencia";
		String expected = "distincion";
		phrase.addAll(Arrays.asList(art29.split(" ")));
		assertEquals(expected, formatResult(this.discriminator.processPhrase(phrase)));
	}
	
	// Metodos utilitarios a continuacion
	
	private void loadStopWords(Set<String> stopWords){
		String stringStopWords = "los,e,en,y,como,estan,esta,de,unos,con,otros,todos," +
			"todas,Todos,Todas,Todo,Toda,tiene,sin,alguna,o,u,a,otra,cualquier,la,su," +
			"estara,ni,no,si,trata,indole,origen,posicion,Nadie,respecto,plenamente," +
			"solo,ella,puede,que";
		stopWords.addAll(Arrays.asList(stringStopWords.split(",")));
	}
	private void loadStopPhrases(List<List<String>> stopPhrases){
		String stopPhrase1 = "en consecuencia";
		String stopPhrase2 = "sin distincion alguna";
		String stopPhrase3 = "cualquier otra";
		String stopPhrase4 = "tiene derecho";
		String stopPhrase5 = "puesto que";
		String stopPhrase6 = "en todas sus formas";
		
		stopPhrases.add(Arrays.asList(stopPhrase1.split(" ")));
		stopPhrases.add(Arrays.asList(stopPhrase2.split(" ")));
		stopPhrases.add(Arrays.asList(stopPhrase3.split(" ")));
		stopPhrases.add(Arrays.asList(stopPhrase4.split(" ")));
		stopPhrases.add(Arrays.asList(stopPhrase5.split(" ")));
		stopPhrases.add(Arrays.asList(stopPhrase6.split(" ")));
	}
	private String formatResult(List<String> result){
		String stringResult = "";
		for (int i=0; i<result.size(); i++){
			stringResult = stringResult + result.get(i) + " ";
		}
		return stringResult.substring(0, stringResult.length()-1);
	}
	
}
