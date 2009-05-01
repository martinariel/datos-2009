package ar.com.datos.indexer.serializer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.ShortSerializer;
import ar.com.datos.serializer.common.StringSerializerDelimiter;
import ar.com.datos.util.Tuple;

public class FrontCodingSerializer implements Serializer<List<String>>{
	private StringSerializerDelimiter stringSerializer;
	private ShortSerializer shortSerializer;
	
	public FrontCodingSerializer(){
		this.shortSerializer = SerializerCache.getInstance().getSerializer(ShortSerializer.class);
		this.stringSerializer = new StringSerializerDelimiter();
	}
	
	
	private interface WordSizeStrategy {
		public void addWordSize(String word, short size);
	}
	
	private class WordSizeStrategyDehydatrer implements WordSizeStrategy {
		private OutputBuffer output;
		public WordSizeStrategyDehydatrer(OutputBuffer output) {
			this.output = output;
		}
		public void addWordSize(String word, short size) {
			shortSerializer.dehydrate(output, size);
			stringSerializer.dehydrate(output, word);
		}
	}

	private class WordSizeStrategySize implements WordSizeStrategy {
		private long size;
		private long shortSerializeSize;
		public WordSizeStrategySize() {
			this.size = 0;
			this.shortSerializeSize = shortSerializer.getDehydrateSize(null);
		}
		public void addWordSize(String word, short size) {
			this.size += this.shortSerializeSize;
			this.size += stringSerializer.getDehydrateSize(word);
		}
	}
	
	@Override
	public void dehydrate(OutputBuffer output, List<String> object) {
		// Pongo la cantidad de términos
		this.shortSerializer.dehydrate(output, (short)object.size());
		
		// Lo comprimo y lo agrego.
		compress(object, new WordSizeStrategyDehydatrer(output));
	}

	@Override
	public long getDehydrateSize(List<String> object) {
		// Veo el tamaño de los términos comprimidos.
		WordSizeStrategySize wordSizeStrategySize = new WordSizeStrategySize();
		compress(object, wordSizeStrategySize);
		
		// Devuelvo ese valor + el de serialización de la cantidad de elementos.
		return wordSizeStrategySize.size + this.shortSerializer.getDehydrateSize((short)object.size());
	}

	@Override
	public List<String> hydrate(InputBuffer input) {
		short count = this.shortSerializer.hydrate(input);

		List<String> words = new LinkedList<String>();
		List<Short> sizes = new LinkedList<Short>();
		for (int i = 0; i < count; i++) {
			sizes.add(this.shortSerializer.hydrate(input));
			words.add(this.stringSerializer.hydrate(input));
		}
		
		return this.decompress(words, sizes);
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
	 * @return un listado de {@link<Short,String>} con las palabras comprimidas
	 */
	private List<Tuple<Short,String>> compress(List<String> words, WordSizeStrategy wordSizeStrategy){
		String currentWord, previousWord;

		List<Tuple<Short,String>> compressedWords = new LinkedList<Tuple<Short,String>>();
		
		// se necesita al menos una palabra
		if (words.size()==0) return compressedWords; 
		
		previousWord = "";
		Iterator<String> it = words.iterator();
		short currentPosition;
		while (it.hasNext()) {
			currentWord = it.next();
			currentPosition = 0;
			// Recorro la palabra letra por letra buscando la primera posición en la que no haya
			// coincidencia.
			while (currentPosition < currentWord.length() && currentPosition < previousWord.length() && 
					currentWord.charAt(currentPosition) == previousWord.charAt(currentPosition)) {
				currentPosition++; 
			}
			// agrego la palabra "comprimida" solo la subcadena que no se repite
			// y guardo cuantos caracteres
			wordSizeStrategy.addWordSize(currentWord.substring(currentPosition, currentWord.length()), currentPosition);
			
			previousWord = currentWord;
		}

		return compressedWords;
	}
	
	/**
	 * Descomprime una lista de palabras comprimidas con el algoritmo de 
	 * FrontCoding y devuelve una lista de palabras.
	 * 
	 * input : (0,"codazo") (3,"earse") (4,"ra") (3,"icia") (6,"r")
	 * output: "codazo" "codearse" "codera" "codicia" "codiciar"
	 *  
	 * @param un listado de {@link<Short,String>} con las palabras comprimidas
	 * @return listado de palabras descomprimidas
	 */
	private List<String> decompress(List<String> comppresedWords, List<Short> sizes){
		List<String> words = new LinkedList<String>();

		Iterator<String> itWords = comppresedWords.iterator();
		Iterator<Short> itSizes = sizes.iterator();
		
		String partialWord, previousWord = "";
		short size;
		while (itWords.hasNext()) {
			partialWord = itWords.next();
			size = itSizes.next();
			
			// se arma la palabra en base a la palabra anterior
			previousWord = previousWord.substring(0, size) + partialWord;
			
			// se agrega a la lista de palabras descomprimidas
			words.add(previousWord);
		}

		return words;
	}
}