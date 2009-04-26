package ar.com.datos.serializer;

import java.util.LinkedList;
import java.util.List;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.common.CollectionSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.ShortSerializer;
import ar.com.datos.serializer.common.StringSerializerDelimiter;
import ar.com.datos.util.Tuple;

public class FrontCodingSerializer implements Serializer<List<String>>{
	
	private CollectionSerializer<Tuple<Short,String>> collectionSerializer;

	public FrontCodingSerializer(){
		this.collectionSerializer = new CollectionSerializer<Tuple<Short,String>>(
			new FrontCodingTupleSerializer()
		);
	}
	
	@Override
	public void dehydrate(OutputBuffer output, List<String> object) {
		List<Tuple<Short,String>> compressedWords = this.compress(object);
		this.collectionSerializer.dehydrate(output, compressedWords);
	}

	@Override
	public long getDehydrateSize(List<String> object) {
		List<Tuple<Short,String>> compressedWords = this.compress(object);
		return this.collectionSerializer.getDehydrateSize(compressedWords);
	}

	@Override
	public List<String> hydrate(InputBuffer input) {
		//return this.collectionSerializer.hydrate(input);
		return null
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
	private List<Tuple<Short,String>> compress(List<String> words){
		short from;
		String currentWord, previousWord;

		List<Tuple<Short,String>> compressedWords = new LinkedList<Tuple<Short,String>>();
		
		// se necesita al menos una palabra
		if (words.size()==0) return compressedWords; 
		
		// agrego la primer palabra sin comprimir
		previousWord = words.get(0);
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
}

/**
 * Serializa un elemento de la lista de front coding elements.
 * Un elemento de la lista es una tupla que contiene un short y un string. 
 */
class FrontCodingTupleSerializer implements Serializer<Tuple<Short,String>>{

	private StringSerializerDelimiter stringSerializer = new StringSerializerDelimiter();
	private ShortSerializer shortserializer = SerializerCache.getInstance().getSerializer(ShortSerializer.class);

	@Override
	public void dehydrate(OutputBuffer output, Tuple<Short, String> object) {
		shortserializer.dehydrate(output, object.getFirst());
		stringSerializer.dehydrate(output, object.getSecond());
	}

	@Override
	public long getDehydrateSize(Tuple<Short, String> object) {
		// 2 bytes (for the Short) 
		// 1 byte * stringLength (for the String)
		return (2 + object.getSecond().length());
	}

	@Override
	public Tuple<Short, String> hydrate(InputBuffer input) {
		Short aShort = shortserializer.hydrate(input);
		String aString = stringSerializer.hydrate(input);
		return new Tuple<Short, String>(aShort, aString);
	}	
}
