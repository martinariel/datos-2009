package ar.com.datos.test.serializer;

import java.util.LinkedList;
import java.util.List;

import ar.com.datos.util.Tuple;
import junit.framework.TestCase;

public class TestFrontCodingSerializer extends TestCase {

	private List<Tuple<Short,String>> compressedWords;
	
	public void testFrontCodingAlgorithm(){
		List<String> words = new LinkedList<String>();
		words.add("codazo");
		words.add("codearse");
		words.add("codera");
		words.add("codicia");
		words.add("codiciar");
		words.add("codiciosa"); 
		words.add("codicioso");
		words.add("codificar"); 
		words.add("codigo");
		
		List<Tuple<Short, String>> compressedWords = this.compress(words);
		
		assertEquals(0, compressedWords.get(0).getFirst().intValue());
		assertEquals("codazo", compressedWords.get(0).getSecond());
		assertEquals(3, compressedWords.get(1).getFirst().intValue());
		assertEquals("earse", compressedWords.get(1).getSecond());
		assertEquals(4, compressedWords.get(2).getFirst().intValue());
		assertEquals("ra", compressedWords.get(2).getSecond());
		assertEquals(3, compressedWords.get(3).getFirst().intValue());
		assertEquals("icia", compressedWords.get(3).getSecond());
		assertEquals(7, compressedWords.get(4).getFirst().intValue());
		assertEquals("r", compressedWords.get(4).getSecond());
		assertEquals(6, compressedWords.get(5).getFirst().intValue());
		assertEquals("osa", compressedWords.get(5).getSecond());
		assertEquals(8, compressedWords.get(6).getFirst().intValue());
		assertEquals("o", compressedWords.get(6).getSecond());
		assertEquals(4, compressedWords.get(7).getFirst().intValue());
		assertEquals("ficar", compressedWords.get(7).getSecond());
		assertEquals(4, compressedWords.get(8).getFirst().intValue());
		assertEquals("go", compressedWords.get(8).getSecond());
	
		// descomprimimos todo
		words = this.decompress(compressedWords);
		
		assertEquals("codazo", words.get(0));
		assertEquals("codearse", words.get(1));
		assertEquals("codera", words.get(2));
		assertEquals("codicia", words.get(3));
		assertEquals("codiciar", words.get(4));
		assertEquals("codiciosa", words.get(5));
		assertEquals("codicioso", words.get(6));
		assertEquals("codificar", words.get(7));
		assertEquals("codigo", words.get(8));
	}
	
	/**
	 * Comprime una lista de palabras con el algoritmo de FrontCoding y devuelve
	 * una lista de tuplas que contienen la siguiente estructura:
	 * 
	 * input : "codazo" "codearse" "codera" "codicia" "codiciar"
	 * output: (0,"codazo") (3,"earse") (4,"ra") (3,"icia") (6,"r")
	 * 
	 * Las tuplas contienen en su primer componente la cantidad de caracteres 
	 * que coinciden con la palabra anterior y como segundo componente 
	 * el substring que no coincide.
	 * 
	 * @param listado de palabras a comprimir
	 * @return un listado de {@link<Integer,String>} con las palabras comprimidas
	 */
	private List<Tuple<Short,String>> compress(List<String> words){
		short from;
		String currentWord, previousWord;
		
		// se necesita al menos una palabra
		if (words.size()==0) return compressedWords; 
		
		// agrego la primer palabra sin comprimir
		previousWord = words.get(0);
		compressedWords = new LinkedList<Tuple<Short,String>>();
		compressedWords.add(new Tuple<Short, String>((short) 0, previousWord));
		
		// no comprimo nada si hay solo una palabra
		if (words.size()==1)return compressedWords; 
		
		// comienzo la compresion (se que al menos hay 2 palabras)
		// comparo la previousWord (anterior) con la currentWord (actual)
		for (int i=1; i < words.size(); i++){
			from = 0;
			currentWord = words.get(i); // obtengo la siguiente palabra
			for (int j=currentWord.length(); j > 0; j--){
				
				// recorro la palabra letra por letra (desde atras) viendo si
				// la palabra anterior comienza con alguna subcadena de la actual
				if (previousWord.startsWith(currentWord.substring(0,j))){
					from = (short) j; // encontre una coincidencia, guardo el valor
					break;
				}
			}
			// agrego la palabra "comprimida" solo la subcadena que no se repite
			// y guardo cuantos caracteres
			compressedWords.add(new Tuple<Short, String>(from, currentWord.substring(from, currentWord.length())));
			previousWord = currentWord;
		}
		return compressedWords;
	}	
	
	private List<String> decompress(List<Tuple<Short,String>> compressedWords){
		List<String> words = new LinkedList<String>();
		Tuple<Short,String> compressedWord;
		short characters;
		String partialWord, previousWord = compressedWords.get(0).getSecond();
		words.add(previousWord);
		for (int i=1; i<compressedWords.size(); i++){
			compressedWord = compressedWords.get(i);
			characters = compressedWord.getFirst();
			partialWord = compressedWord.getSecond();
			previousWord = previousWord.substring(0, characters) + partialWord;
			words.add(previousWord);
		}
		return words;
	}
}
