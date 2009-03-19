package ar.com.datos.test.serializer;

import ar.com.datos.serializer.HydrateInfo;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.BooleanSerializer;
import ar.com.datos.serializer.common.ByteSerializer;
import ar.com.datos.serializer.common.CharacterSerializer;
import ar.com.datos.serializer.common.DoubleSerializer;
import ar.com.datos.serializer.common.FloatSerializer;
import ar.com.datos.serializer.common.IntegerSerializer;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.ShortSerializer;
import ar.com.datos.serializer.common.StringSerializerDelimiter;
import ar.com.datos.serializer.common.StringSerializerSize;
import ar.com.datos.test.ExtendedTestCase;

public class TestSerializer extends ExtendedTestCase {
	/**
	 * Test para la clase {@link PrimitiveTypeSerializer}
	 */
	public void testPrimitiveTypeSerializer() {
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
	 * Test para la clase {@link BooleanSerializer}
	 */
	@SuppressWarnings("unchecked")
	public void testBooleanSerializer() {
		Serializer<Boolean> serializer = SerializerCache.getInstance().getSerializer(BooleanSerializer.class);
		boolean first = true;
		boolean second = false;

		byte[] firstSerialized = serializer.dehydrate(first);
		byte[] secondSerialized = serializer.dehydrate(second);
		byte[] bytes = new byte[serializer.getDehydrateSize(first) + serializer.getDehydrateSize(second)];
		System.arraycopy(firstSerialized, 0, bytes, 0, firstSerialized.length);
		System.arraycopy(secondSerialized, 0, bytes, firstSerialized.length, secondSerialized.length);

		boolean recoveredFirst, recoveredSecond;
		HydrateInfo<Boolean> hydrateInfo = serializer.hydrate(bytes);
		recoveredFirst = hydrateInfo.getHydratedObject();
		hydrateInfo = serializer.hydrate(hydrateInfo.getHydrateRemaining());
		recoveredSecond = hydrateInfo.getHydratedObject();
		byte[] remaining = hydrateInfo.getHydrateRemaining();

		assertEquals(first, recoveredFirst);
		assertEquals(second, recoveredSecond);
		assertEquals(remaining.length, 0);
	}

	/**
	 * Test para la clase {@link ByteSerializer}
	 */
	@SuppressWarnings("unchecked")
	public void testByteSerializer() {
		Serializer<Byte> serializer = SerializerCache.getInstance().getSerializer(ByteSerializer.class);
		byte first = (byte)22;
		byte second = (byte)45;

		byte[] firstSerialized = serializer.dehydrate(first);
		byte[] secondSerialized = serializer.dehydrate(second);
		byte[] bytes = new byte[serializer.getDehydrateSize(first) + serializer.getDehydrateSize(second)];
		System.arraycopy(firstSerialized, 0, bytes, 0, firstSerialized.length);
		System.arraycopy(secondSerialized, 0, bytes, firstSerialized.length, secondSerialized.length);

		byte recoveredFirst, recoveredSecond;
		HydrateInfo<Byte> hydrateInfo = serializer.hydrate(bytes);
		recoveredFirst = hydrateInfo.getHydratedObject();
		hydrateInfo = serializer.hydrate(hydrateInfo.getHydrateRemaining());
		recoveredSecond = hydrateInfo.getHydratedObject();
		byte[] remaining = hydrateInfo.getHydrateRemaining();

		assertEquals(first, recoveredFirst);
		assertEquals(second, recoveredSecond);
		assertEquals(remaining.length, 0);
	}

	/**
	 * Test para la clase {@link CharacterSerializer}
	 */
	@SuppressWarnings("unchecked")
	public void testCharacterSerializer() {
		Serializer<Character> serializer = SerializerCache.getInstance().getSerializer(CharacterSerializer.class);
		char first = 'a';
		char second = 'Z';

		byte[] firstSerialized = serializer.dehydrate(first);
		byte[] secondSerialized = serializer.dehydrate(second);
		byte[] bytes = new byte[serializer.getDehydrateSize(first) + serializer.getDehydrateSize(second)];
		System.arraycopy(firstSerialized, 0, bytes, 0, firstSerialized.length);
		System.arraycopy(secondSerialized, 0, bytes, firstSerialized.length, secondSerialized.length);

		char recoveredFirst, recoveredSecond;
		HydrateInfo<Character> hydrateInfo = serializer.hydrate(bytes);
		recoveredFirst = hydrateInfo.getHydratedObject();
		hydrateInfo = serializer.hydrate(hydrateInfo.getHydrateRemaining());
		recoveredSecond = hydrateInfo.getHydratedObject();
		byte[] remaining = hydrateInfo.getHydrateRemaining();

		assertEquals(first, recoveredFirst);
		assertEquals(second, recoveredSecond);
		assertEquals(remaining.length, 0);
	}

	/**
	 * Test para la clase {@link ShortSerializer}
	 */
	@SuppressWarnings("unchecked")
	public void testShortSerializer() {
		Serializer<Short> serializer = SerializerCache.getInstance().getSerializer(ShortSerializer.class);
		short first = (short)22;
		short second = (short)-45;

		byte[] firstSerialized = serializer.dehydrate(first);
		byte[] secondSerialized = serializer.dehydrate(second);
		byte[] bytes = new byte[serializer.getDehydrateSize(first) + serializer.getDehydrateSize(second)];
		System.arraycopy(firstSerialized, 0, bytes, 0, firstSerialized.length);
		System.arraycopy(secondSerialized, 0, bytes, firstSerialized.length, secondSerialized.length);

		short recoveredFirst, recoveredSecond;
		HydrateInfo<Short> hydrateInfo = serializer.hydrate(bytes);
		recoveredFirst = hydrateInfo.getHydratedObject();
		hydrateInfo = serializer.hydrate(hydrateInfo.getHydrateRemaining());
		recoveredSecond = hydrateInfo.getHydratedObject();
		byte[] remaining = hydrateInfo.getHydrateRemaining();

		assertEquals(first, recoveredFirst);
		assertEquals(second, recoveredSecond);
		assertEquals(remaining.length, 0);
	}

	/**
	 * Test para la clase {@link IntegerSerializer}
	 */
	@SuppressWarnings("unchecked")
	public void testIntegerSerializer() {
		Serializer<Integer> serializer = SerializerCache.getInstance().getSerializer(IntegerSerializer.class);
		int first = 22;
		int second = -45;

		byte[] firstSerialized = serializer.dehydrate(first);
		byte[] secondSerialized = serializer.dehydrate(second);
		byte[] bytes = new byte[serializer.getDehydrateSize(first) + serializer.getDehydrateSize(second)];
		System.arraycopy(firstSerialized, 0, bytes, 0, firstSerialized.length);
		System.arraycopy(secondSerialized, 0, bytes, firstSerialized.length, secondSerialized.length);

		int recoveredFirst, recoveredSecond;
		HydrateInfo<Integer> hydrateInfo = serializer.hydrate(bytes);
		recoveredFirst = hydrateInfo.getHydratedObject();
		hydrateInfo = serializer.hydrate(hydrateInfo.getHydrateRemaining());
		recoveredSecond = hydrateInfo.getHydratedObject();
		byte[] remaining = hydrateInfo.getHydrateRemaining();

		assertEquals(first, recoveredFirst);
		assertEquals(second, recoveredSecond);
		assertEquals(remaining.length, 0);
	}

	/**
	 * Test para la clase {@link LongSerializer}
	 */
	@SuppressWarnings("unchecked")
	public void testLongSerializer() {
		Serializer<Long> serializer = SerializerCache.getInstance().getSerializer(LongSerializer.class);
		long first = (long)22;
		long second = (long)-45;

		byte[] firstSerialized = serializer.dehydrate(first);
		byte[] secondSerialized = serializer.dehydrate(second);
		byte[] bytes = new byte[serializer.getDehydrateSize(first) + serializer.getDehydrateSize(second)];
		System.arraycopy(firstSerialized, 0, bytes, 0, firstSerialized.length);
		System.arraycopy(secondSerialized, 0, bytes, firstSerialized.length, secondSerialized.length);

		long recoveredFirst, recoveredSecond;
		HydrateInfo<Long> hydrateInfo = serializer.hydrate(bytes);
		recoveredFirst = hydrateInfo.getHydratedObject();
		hydrateInfo = serializer.hydrate(hydrateInfo.getHydrateRemaining());
		recoveredSecond = hydrateInfo.getHydratedObject();
		byte[] remaining = hydrateInfo.getHydrateRemaining();

		assertEquals(first, recoveredFirst);
		assertEquals(second, recoveredSecond);
		assertEquals(remaining.length, 0);
	}

	/**
	 * Test para la clase {@link FloatSerializer}
	 */
	@SuppressWarnings("unchecked")
	public void testFloatSerializer() {
		Serializer<Float> serializer = SerializerCache.getInstance().getSerializer(FloatSerializer.class);
		float first = (float)22;
		float second = (float)-45;

		byte[] firstSerialized = serializer.dehydrate(first);
		byte[] secondSerialized = serializer.dehydrate(second);
		byte[] bytes = new byte[serializer.getDehydrateSize(first) + serializer.getDehydrateSize(second)];
		System.arraycopy(firstSerialized, 0, bytes, 0, firstSerialized.length);
		System.arraycopy(secondSerialized, 0, bytes, firstSerialized.length, secondSerialized.length);

		float recoveredFirst, recoveredSecond;
		HydrateInfo<Float> hydrateInfo = serializer.hydrate(bytes);
		recoveredFirst = hydrateInfo.getHydratedObject();
		hydrateInfo = serializer.hydrate(hydrateInfo.getHydrateRemaining());
		recoveredSecond = hydrateInfo.getHydratedObject();
		byte[] remaining = hydrateInfo.getHydrateRemaining();

		assertEquals(first, recoveredFirst);
		assertEquals(second, recoveredSecond);
		assertEquals(remaining.length, 0);
	}

	/**
	 * Test para la clase {@link DoubleSerializer}
	 */
	@SuppressWarnings("unchecked")
	public void testDoubleSerializer() {
		Serializer<Double> serializer = SerializerCache.getInstance().getSerializer(DoubleSerializer.class);
		double first = (double)22;
		double second = (double)-45;

		byte[] firstSerialized = serializer.dehydrate(first);
		byte[] secondSerialized = serializer.dehydrate(second);
		byte[] bytes = new byte[serializer.getDehydrateSize(first) + serializer.getDehydrateSize(second)];
		System.arraycopy(firstSerialized, 0, bytes, 0, firstSerialized.length);
		System.arraycopy(secondSerialized, 0, bytes, firstSerialized.length, secondSerialized.length);

		double recoveredFirst, recoveredSecond;
		HydrateInfo<Double> hydrateInfo = serializer.hydrate(bytes);
		recoveredFirst = hydrateInfo.getHydratedObject();
		hydrateInfo = serializer.hydrate(hydrateInfo.getHydrateRemaining());
		recoveredSecond = hydrateInfo.getHydratedObject();
		byte[] remaining = hydrateInfo.getHydrateRemaining();

		assertEquals(first, recoveredFirst);
		assertEquals(second, recoveredSecond);
		assertEquals(remaining.length, 0);
	}

	/**
	 * Test para la clase {@link StringSerializerDelimiter}
	 */
	@SuppressWarnings("unchecked")
	public void testStringSerializerDelimiter() {
		// Uso un delimitador originado a partir de un String.
		Serializer<String> serializer = new StringSerializerDelimiter("$$");
		String first = "Nelson Marotte, alias \"Tentaculos\"";
		String second = "La caca de la nona (primer banda de Nelson)";

		byte[] firstSerialized = serializer.dehydrate(first);
		byte[] secondSerialized = serializer.dehydrate(second);
		byte[] bytes = new byte[serializer.getDehydrateSize(first) + serializer.getDehydrateSize(second)];
		System.arraycopy(firstSerialized, 0, bytes, 0, firstSerialized.length);
		System.arraycopy(secondSerialized, 0, bytes, firstSerialized.length, secondSerialized.length);

		String recoveredFirst, recoveredSecond;
		HydrateInfo<String> hydrateInfo = serializer.hydrate(bytes);
		recoveredFirst = hydrateInfo.getHydratedObject();
		hydrateInfo = serializer.hydrate(hydrateInfo.getHydrateRemaining());
		recoveredSecond = hydrateInfo.getHydratedObject();
		byte[] remaining = hydrateInfo.getHydrateRemaining();

		assertEquals(first, recoveredFirst);
		assertEquals(second, recoveredSecond);
		assertEquals(remaining.length, 0);

		// Uso un delimitador originado a partir de un byte[]
		serializer = new StringSerializerDelimiter(new byte[] {(byte)0, (byte)0});
		firstSerialized = serializer.dehydrate(first);
		secondSerialized = serializer.dehydrate(second);
		bytes = new byte[serializer.getDehydrateSize(first) + serializer.getDehydrateSize(second)];
		System.arraycopy(firstSerialized, 0, bytes, 0, firstSerialized.length);
		System.arraycopy(secondSerialized, 0, bytes, firstSerialized.length, secondSerialized.length);

		hydrateInfo = serializer.hydrate(bytes);
		recoveredFirst = hydrateInfo.getHydratedObject();
		hydrateInfo = serializer.hydrate(hydrateInfo.getHydrateRemaining());
		recoveredSecond = hydrateInfo.getHydratedObject();
		remaining = hydrateInfo.getHydrateRemaining();

		assertEquals(first, recoveredFirst);
		assertEquals(second, recoveredSecond);
		assertEquals(remaining.length, 0);
	}


	/**
	 * Test para la clase {@link StringSerializerSize}
	 */
	@SuppressWarnings("unchecked")
	public void testStringSerializerSize() {
		// Uso un delimitador originado a partir de un String.
		Serializer<String> serializer = new StringSerializerSize((short)40);
		String first = "Nelson Marotte, alias \"Tentaculos\"";
		String second = "La caca de la nona (primer banda de Nelson)";

		byte[] firstSerialized = serializer.dehydrate(first);
		byte[] secondSerialized = serializer.dehydrate(second);
		byte[] bytes = new byte[serializer.getDehydrateSize(first) + serializer.getDehydrateSize(second)];
		System.arraycopy(firstSerialized, 0, bytes, 0, firstSerialized.length);
		System.arraycopy(secondSerialized, 0, bytes, firstSerialized.length, secondSerialized.length);

		String recoveredFirst, recoveredSecond;
		HydrateInfo<String> hydrateInfo = serializer.hydrate(bytes);
		recoveredFirst = hydrateInfo.getHydratedObject();
		hydrateInfo = serializer.hydrate(hydrateInfo.getHydrateRemaining());
		recoveredSecond = hydrateInfo.getHydratedObject();
		byte[] remaining = hydrateInfo.getHydrateRemaining();

		assertEquals(first, recoveredFirst);
		assertEquals(second.substring(0, 40), recoveredSecond);
		assertEquals(0, remaining.length);
	}
}
