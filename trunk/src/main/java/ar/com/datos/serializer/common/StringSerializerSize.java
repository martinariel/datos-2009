package ar.com.datos.serializer.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;


/**
 * {@link Serializer} para String que antepone en la deshidratacion el largo del String
 * en un campo definido por el cardinalitySerializer pasado (si no se pasa ningun, se
 * usa por defecto un {@link ByteSerializer}).
 * Si el String fuera mas largo que lo permitido por el cardinalitySerializer, se
 * truncara el String serializado.
 * La cantidad de caracteres se serializa como unsigned (la conversion se hace de manera
 * automatica) por lo que se dispondra del doble de la capacidad definida por el
 * cardinalitySerializer establecido.
 *
 * @author fvalido
 */
public class StringSerializerSize implements Serializer<String> {
	/**
	 * Serializer de colecciones a ser usado auxiliarmente.
	 */
	private CollectionSerializer<Character> collectionSerializer;
	/**
	 * Cantidad de bytes usada por el tipo especificado por el cardinalitySerializer.
	 */
	private long cardinalitySize;

	
	/**
	 * Permite construir una instancia. 
	 * Para el size se usara un {@link ByteSerializer}
	 */
	public StringSerializerSize() {
		this.collectionSerializer = new CollectionSerializer<Character>(
				(CharacterSerializer)SerializerCache.getInstance().getSerializer(CharacterSerializer.class),
				(ByteSerializer)SerializerCache.getInstance().getSerializer(ByteSerializer.class));
		this.cardinalitySize = ((ByteSerializer)SerializerCache.getInstance().getSerializer(ByteSerializer.class)).getDehydrateSize(null);
	}
	
	/**
	 * Permite construir una instancia.
	 * 
	 * @param cardinalitySerializer
	 * {@link Serializer} para el size.
	 */
	public StringSerializerSize(NumberSerializer<? extends Number> cardinalitySerializer) {
		this.collectionSerializer = new CollectionSerializer<Character>(
				(CharacterSerializer)SerializerCache.getInstance().getSerializer(CharacterSerializer.class),
				cardinalitySerializer);
		this.cardinalitySize = cardinalitySerializer.getDehydrateSize(null);		
	}
	
	/**
	 * Permite cambiar el {@link Serializer} para el size.
	 */
	public void setCardinalitySerializer(NumberSerializer<? extends Number> cardinalitySerializer) {
		this.collectionSerializer = new CollectionSerializer<Character>(
				(CharacterSerializer)SerializerCache.getInstance().getSerializer(CharacterSerializer.class),
				cardinalitySerializer);
		this.cardinalitySize = cardinalitySerializer.getDehydrateSize(null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	public void dehydrate(OutputBuffer output, String object) {
		String value = object;
		
		char[] charactersArray = new char[value.length()];
		value.getChars(0, value.length(), charactersArray, 0);
		Collection<Character> charactersCollection = new LinkedList<Character>();
		// FIXME: Si agregamos Commons de apache cambiar esto por un CollectionUtils.addAll
		for (int i = 0; i < charactersArray.length; i++) {
			charactersCollection.add(charactersArray[i]);
		}
		
		this.collectionSerializer.dehydrate(output, charactersCollection);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	public String hydrate(InputBuffer input) {
		Collection<Character> charactersCollection = this.collectionSerializer.hydrate(input);
		Iterator<Character> it = charactersCollection.iterator();
		char[] charsArray = new char[charactersCollection.size()];
		int i = 0;
		while (it.hasNext()) {
			charsArray[i] = it.next();
			i++;
		}

		return new String(charsArray);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public long getDehydrateSize(String object) {
		return object.length() * 2 + this.cardinalitySize;
	}
}
