package ar.com.datos.test.serializer;

import java.util.Collection;
import java.util.LinkedList;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.exception.BufferException;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.BooleanSerializer;
import ar.com.datos.serializer.common.ByteSerializer;
import ar.com.datos.serializer.common.CharacterSerializer;
import ar.com.datos.serializer.common.CollectionSerializer;
import ar.com.datos.serializer.common.DoubleSerializer;
import ar.com.datos.serializer.common.FloatSerializer;
import ar.com.datos.serializer.common.IntegerSerializer;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.ShortSerializer;
import ar.com.datos.serializer.common.StringSerializerDelimiter;
import ar.com.datos.serializer.common.StringSerializerSize;
import ar.com.datos.test.ExtendedTestCase;
import ar.com.datos.test.serializer.mock.OutputBufferTest;

/**
 * Test para Serializadores.
 *  
 * @author fvalido
 */
public class TestSerializer extends ExtendedTestCase {
	/**
	 * Test para la clase {@link PrimitiveTypeSerializer}
	 */
	public void testPrimitiveTypeSerializer() {
		System.out.println("Este test (testPrimitiveTypeSerializer()) tarda mucho! Por ahi queres detener el test y comentarlo.");
		// Boolean
		assertEquals(PrimitiveTypeSerializer.toBoolean(PrimitiveTypeSerializer.toByte(true)), true);
		assertEquals(PrimitiveTypeSerializer.toBoolean(PrimitiveTypeSerializer.toByte(false)), false);
		assertEquals(PrimitiveTypeSerializer.toByte(true).length, 1);
		assertEquals(PrimitiveTypeSerializer.toBooleanArray(PrimitiveTypeSerializer.toByte(new boolean[] {true, false, true})), new boolean[] {true, false, true});

		// Byte
		// No hay nada para probar.

		// Char
		for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; c++) {
			assertEquals(c, PrimitiveTypeSerializer.toChar(PrimitiveTypeSerializer.toByte(c)));
		}
		assertEquals(2, PrimitiveTypeSerializer.toByte('p').length);
		assertEquals(new char[] {'a','b','c','d','1','2','3'}, PrimitiveTypeSerializer.toCharArray(PrimitiveTypeSerializer.toByte(new char[] {'a','b','c','d','1','2','3'})));

		// Short
		for (short s = Short.MIN_VALUE; s < Short.MAX_VALUE; s++) {
			assertEquals(s, PrimitiveTypeSerializer.toShort(PrimitiveTypeSerializer.toByte(s)));
		}
		assertEquals(2, PrimitiveTypeSerializer.toByte((short)44).length);
		assertEquals(new short[] {(short)2121, (short)452, (short)-451, (short)0}, PrimitiveTypeSerializer.toShortArray(PrimitiveTypeSerializer.toByte(new short[] {(short)2121, (short)452, (short)-451, (short)0})));

		// Int
		for (int count = ((int)Short.MIN_VALUE) * 256; count < ((int)Short.MAX_VALUE) * 256; count++) {
			byte signo = (Math.random() > 0.5) ? (byte)-1 : (byte)1;
			int i = ((int)(Math.random() * Integer.MAX_VALUE)) * signo;
			assertEquals(i, PrimitiveTypeSerializer.toInt(PrimitiveTypeSerializer.toByte(i)));
		}
		assertEquals(4, PrimitiveTypeSerializer.toByte(44).length);
		assertEquals(new int[] {2121, 452, -451, 0}, PrimitiveTypeSerializer.toIntArray(PrimitiveTypeSerializer.toByte(new int[] {2121, 452, -451, 0})));

		// Long
		for (int count = ((int)Short.MIN_VALUE) * 256; count < ((int)Short.MAX_VALUE) * 256; count++) {
			byte signo = (Math.random() > 0.5) ? (byte)-1 : (byte)1;
			long l = ((long)(Math.random() * Long.MAX_VALUE)) * signo;
			assertEquals(l, PrimitiveTypeSerializer.toLong(PrimitiveTypeSerializer.toByte(l)));
		}
		assertEquals(8, PrimitiveTypeSerializer.toByte((long)44).length);
		assertEquals(new long[] {(long)2121, (long)452, (long)-451, (long)0}, PrimitiveTypeSerializer.toLongArray(PrimitiveTypeSerializer.toByte(new long[] {(long)2121, (long)452, (long)-451, (long)0})));

		// Float
		for (int count = ((int)Short.MIN_VALUE) * 256; count < ((int)Short.MAX_VALUE) * 256; count++) {
			byte signo = (Math.random() > 0.5) ? (byte)-1 : (byte)1;
			float f = ((float)(Math.random() * Float.MAX_VALUE)) * signo;
			assertEquals(f, PrimitiveTypeSerializer.toFloat(PrimitiveTypeSerializer.toByte(f)));
		}
		assertEquals(4, PrimitiveTypeSerializer.toByte((float)44).length);
		assertEquals(new float[] {(float)2121, (float)452, (float)-451, (float)0}, PrimitiveTypeSerializer.toFloatArray(PrimitiveTypeSerializer.toByte(new float[] {(float)2121, (float)452, (float)-451, (float)0})));

		// Double
		for (int count = ((int)Short.MIN_VALUE) * 256; count < ((int)Short.MAX_VALUE) * 256; count++) {
			byte signo = (Math.random() > 0.5) ? (byte)-1 : (byte)1;
			double d = ((double)(Math.random() * Double.MAX_VALUE)) * signo;
			assertEquals(d, PrimitiveTypeSerializer.toDouble(PrimitiveTypeSerializer.toByte(d)));
		}
		assertEquals(8, PrimitiveTypeSerializer.toByte((double)44).length);
		assertEquals(new double[] {(double)2121, (double)452, (double)-451, (double)0}, PrimitiveTypeSerializer.toDoubleArray(PrimitiveTypeSerializer.toByte(new double[] {(double)2121, (double)452, (double)-451, (double)0})));
	}

	/**
	 * Esquema general de todos los tests.
	 */
	@SuppressWarnings("unchecked")
	private void generalTest(Serializer serializer, Object first, Object second) {
		// Deshidrato en un OutputBuffer los objetos recibido.
		OutputBufferTest oBuffer = new OutputBufferTest();
		serializer.dehydrate(oBuffer, first);
		serializer.dehydrate(oBuffer, second);
		
		// Rehidrato los objetos usando un InputBuffer obtenido a partir del OutputBuffer.
		Object recoveredFirst, recoveredSecond;
		InputBuffer iBuffer = oBuffer.getAsInputBuffer();
		recoveredFirst = serializer.hydrate(iBuffer);
		recoveredSecond = serializer.hydrate(iBuffer);

		// Realizo las comparaciones.
		assertEquals(first, recoveredFirst);
		assertEquals(second, recoveredSecond);
		try {
			iBuffer.read();
			assertFalse(true);
		} catch (BufferException e) {
			assertTrue(true);
		}
		
		// Cuento el dehydrateSize para compararlo con el metodo correspondiente.
		iBuffer = oBuffer.getAsInputBuffer();
		long count = 0;
		try {
			while (true) {
				iBuffer.read();
				count++;
			}
		} catch (BufferException e) {
		}
		assertEquals(count, serializer.getDehydrateSize(first) + serializer.getDehydrateSize(second));
	}
	
	/**
	 * Test para la clase {@link BooleanSerializer}
	 */
	public void testBooleanSerializer() {
		Serializer<Boolean> serializer = SerializerCache.getInstance().getSerializer(BooleanSerializer.class);
		boolean first = true;
		boolean second = false;
		
		generalTest(serializer, first, second);
	}

	/**
	 * Test para la clase {@link ByteSerializer}
	 */
	public void testByteSerializer() {
		Serializer<Byte> serializer = SerializerCache.getInstance().getSerializer(ByteSerializer.class);
		byte first = (byte)22;
		byte second = (byte)45;

		generalTest(serializer, first, second);
	}

	/**
	 * Test para la clase {@link CharacterSerializer}
	 */
	public void testCharacterSerializer() {
		Serializer<Character> serializer = SerializerCache.getInstance().getSerializer(CharacterSerializer.class);
		char first = 'a';
		char second = 'Z';

		generalTest(serializer, first, second);
	}

	/**
	 * Test para la clase {@link ShortSerializer}
	 */
	public void testShortSerializer() {
		Serializer<Short> serializer = SerializerCache.getInstance().getSerializer(ShortSerializer.class);
		short first = (short)22;
		short second = (short)-45;

		generalTest(serializer, first, second);
	}

	/**
	 * Test para la clase {@link IntegerSerializer}
	 */
	public void testIntegerSerializer() {
		Serializer<Integer> serializer = SerializerCache.getInstance().getSerializer(IntegerSerializer.class);
		int first = 22;
		int second = -45;

		generalTest(serializer, first, second);
	}

	/**
	 * Test para la clase {@link LongSerializer}
	 */
	public void testLongSerializer() {
		Serializer<Long> serializer = SerializerCache.getInstance().getSerializer(LongSerializer.class);
		long first = (long)22;
		long second = (long)-45;

		generalTest(serializer, first, second);
	}

	/**
	 * Test para la clase {@link FloatSerializer}
	 */
	public void testFloatSerializer() {
		Serializer<Float> serializer = SerializerCache.getInstance().getSerializer(FloatSerializer.class);
		float first = (float)22;
		float second = (float)-45;

		generalTest(serializer, first, second);
	}

	/**
	 * Test para la clase {@link DoubleSerializer}
	 */
	public void testDoubleSerializer() {
		Serializer<Double> serializer = SerializerCache.getInstance().getSerializer(DoubleSerializer.class);
		double first = (double)22;
		double second = (double)-45;

		generalTest(serializer, first, second);
	}

	/**
	 * Test para la clase {@link StringSerializerDelimiter}
	 */
	public void testStringSerializerDelimiter() {
		// Uso un delimitador originado a partir de un String.
		Serializer<String> serializer = new StringSerializerDelimiter("$$");
		String first = "La Ley, en su magnifica ecuanimidad, prohibe, tanto al rico como al pobre, dormir bajo los puentes, mendigar por las calles y robar pan.";
		String second = "Todos los pobres tienen la libertad de morirse de hambre bajo los puentes de Paris";

		generalTest(serializer, first, second);

		// Uso un delimitador originado a partir de un byte[]
		second = "";
		serializer = new StringSerializerDelimiter(new byte[] {(byte)0, (byte)0});
		generalTest(serializer, first, second);
	}
	
	/**
	 * Test para la clase {@link CollectionSerializer}
	 */
	public void testCollectionSerializer() {
		Serializer<String> stringSizeSerializer = new StringSerializerSize();
		Serializer<Collection<String>> serializer = new CollectionSerializer<String>(stringSizeSerializer);
		Collection<String> first = new LinkedList<String>();
		first.add("1");
		first.add("22");
		first.add("333");
		first.add("4444");
		Collection<String> second = new LinkedList<String>();
		
		generalTest(serializer, first, second);		
	}
	
	/**
	 * Test para la clase {@link StringSerializerSize}
	 */
	public void testStringSerializerSize() {
		// Uso un delimitador originado a partir de un String.
		Serializer<String> serializer = new StringSerializerSize(SerializerCache.getInstance().getSerializer(ShortSerializer.class));
		String first = "La Ley, en su magnifica ecuanimidad, prohibe, tanto al rico como al pobre, dormir bajo los puentes, mendigar por las calles y robar pan";
		String second = "Todos los pobres tienen la libertad de morirse de hambre bajo los puentes de Paris";

		generalTest(serializer, first, second);
		
		// Uso un serializador distinto para el size.
		first = "Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255.Sale hasta acaESTO NO SALE";
		String firstCompare = "Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255. Texto con mas de 255.Sale hasta aca";
		second = "";
		serializer = new StringSerializerSize(); // Usa ByteSerializer por defecto.
		
		OutputBufferTest oBuffer = new OutputBufferTest();
		serializer.dehydrate(oBuffer, first);
		serializer.dehydrate(oBuffer, second);
		
		Object recoveredFirst, recoveredSecond;
		InputBuffer iBuffer = oBuffer.getAsInputBuffer();
		recoveredFirst = serializer.hydrate(iBuffer);
		recoveredSecond = serializer.hydrate(iBuffer);

		assertEquals(firstCompare, recoveredFirst);
		assertEquals(second, recoveredSecond);
		try {
			iBuffer.read();
			assertFalse(true);
		} catch (BufferException e) {
			assertTrue(true);
		}
	}
}
