package ar.com.datos.serializer.common;

import java.util.HashMap;
import java.util.Map;

import ar.com.datos.serializer.Serializer;

/**
 * Cache para serializadores. Simplemente para no tener que crear un serializador
 * cada vez que se lo va a usar.
 * (Patron Singleton)
 *
 * @author fvalido
 */
public class SerializerCache {
	/**
	 * Mapa usa para guardar las instancias de serializadores.
	 */
	private Map<Class, Serializer> cache;

	/**
	 * Clase para guardar la unica instancia.
	 */
	private static class SingletonHolder {
		/**
		 * Unica instancia.
		 */
		private static SerializerCache instance = new SerializerCache();
	}

	/**
	 * Constructor privado.
	 * (Patron Singleton)
	 * Al ser creado el cache contiene los serializadores de tipos primitivos.
	 */
	private SerializerCache() {
		this.cache = new HashMap<Class, Serializer>();
		this.cache.put(BooleanSerializer.class, new BooleanSerializer());
		this.cache.put(ByteSerializer.class, new ByteSerializer());
		this.cache.put(CharacterSerializer.class, new CharacterSerializer());
		this.cache.put(ShortSerializer.class, new ShortSerializer());
		this.cache.put(IntegerSerializer.class, new IntegerSerializer());
		this.cache.put(LongSerializer.class, new LongSerializer());
		this.cache.put(FloatSerializer.class, new FloatSerializer());
		this.cache.put(DoubleSerializer.class, new DoubleSerializer());
	}

	/**
	 * Obtiene una instancia de la clase pasada desde el cache.
	 * Si no se encuentra se devuelve null.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getSerializer(Class<T> clazz) {
		return (T) this.cache.get(clazz);
	}

	/**
	 * Agrega la instancia pasada al cache.
	 */
	public void addSerializer(Serializer serializer) {
		this.cache.put(serializer.getClass(), serializer);
	}

	/**
	 * Obtiene la unica instancia del cache.
	 * (Patron Singleton)
	 */
	public static SerializerCache getInstance() {
		return SingletonHolder.instance;
	}
}
