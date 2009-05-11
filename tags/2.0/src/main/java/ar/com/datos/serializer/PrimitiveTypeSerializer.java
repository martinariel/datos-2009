package ar.com.datos.serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.RandomAccessFile;

/**
 * Esta clase esta basada en el contrato de las interfaces {@link DataOutput} y
 * {@link DataInput}, y en la implementacion hecha de estas interfaces por
 * {@link RandomAccessFile}.
 * Contiene metodos estaticos que permiten pasar a byte[] desde tipos primitivos y
 * viceversa, asi como arrays de estos tipos primitivos (esto ultimo es solo
 * un atajo hecho por conveniencia).
 *
 * @author fvalido
 */
public class PrimitiveTypeSerializer {

	// booleans

	/**
	 * Permite convertir un boolean a una tira de byte[]. Esta tira tendra un largo
	 * fijo de un elemento.
	 * Luego puede ser reconvertido utilizando {@link #toBoolean(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link DataOutput#writeBoolean(boolean)}.
	 */
	public static byte[] toByte(boolean b) {
		byte[] bytes = new byte[1];
		bytes[0] = b ? (byte)0 : (byte)1;

		return bytes;
	}

	/**
	 * Permite reconvertir a un boolean una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(boolean)}.
	 * La tira de byte[] pasada solo debe tener un elemento.
	 * La conversion se realiza segun lo expresado por {@link DataInput#readBoolean()}.
	 */
	public static boolean toBoolean(byte[] bytes) {
		return bytes[0] == 0;
	}

	/**
	 * Permite convertir un array de boolean a una tira de byte[]. Esta tira tendra un largo
	 * igual al largo de b.
	 * Luego puede ser reconvertido utilizando {@link #toBooleanArray(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link #toByte(boolean)}.
	 */
	public static byte[] toByte(boolean[] b) {
		byte[] bytes = new byte[b.length];
		for (int i = 0; i < b.length; i++) {
			bytes[i] = b[i] ? (byte)0 : (byte)1;
		}

		return bytes;
	}

	/**
	 * Permite reconvertir a un array de boolean una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(boolean[])}.
	 * La tira devuelta tendra tantos elementos como el parametro pasado.
	 * La conversion se realiza segun lo expresado por {@link #toBoolean(byte[])}.
	 */
	public static boolean[] toBooleanArray(byte[] bytes) {
		boolean[] booleans = new boolean[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			booleans[i] = bytes[i] == 0;
		}

		return booleans;
	}

	// -----------------------------------------------------------------------------------

	// bytes
	// No hace falta hacer nada.

	// -----------------------------------------------------------------------------------

	// chars

	/**
	 * Permite convertir un char a una tira de byte[]. Esta tira tendra un largo
	 * fijo de dos elementos.
	 * Luego puede ser reconvertido utilizando {@link #toChar(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link DataOutput#writeChar(int)}
	 * suponiendo que se castea el char recibido a un int.
	 */
	public static byte[] toByte(char c) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte)(((int)c >> 8) & 0xFF);
		bytes[1] = (byte)((int)c & 0xFF);

		return bytes;
	}

	/**
	 * Permite reconvertir a un char una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(char)}.
	 * La tira de byte[] pasada debe tener dos elementos.
	 * La conversion se realiza segun lo expresado por {@link DataInput#readChar()}.
	 */
	public static char toChar(byte[] bytes) {
		return (char)((bytes[0] << 8) | (bytes[1] & 0xFF));
	}

	/**
	 * Permite convertir un array de char a una tira de byte[]. Esta tira tendra un largo
	 * igual al largo de c * 2.
	 * Luego puede ser reconvertido utilizando {@link #toCharArray(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link #toByte(char)}.
	 */
	public static byte[] toByte(char[] c) {
		byte[] currentBytes;

		byte[] bytes = new byte[c.length * 2];
		for (int i = 0; i < c.length; i++) {
			currentBytes = toByte(c[i]);
			bytes[i * 2] = currentBytes[0];
			bytes[i * 2 + 1] = currentBytes[1];
		}

		return bytes;
	}

	/**
	 * Permite reconvertir a un array de char una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(char[])}.
	 * La tira devuelta tendra tantos elementos como el parametro pasado / 2.
	 * La conversion se realiza segun lo expresado por {@link #toChar(byte[])}.
	 */
	public static char[] toCharArray(byte[] bytes) {
		byte[] currentBytes = new byte[2];

		char[] chars = new char[bytes.length / 2];
		for (int i = 0; i < bytes.length / 2; i++) {
			currentBytes[0] = bytes[i * 2];
			currentBytes[1] = bytes[i * 2 + 1];
			chars[i] = toChar(currentBytes);
		}

		return chars;
	}

	// -----------------------------------------------------------------------------------

	// shorts

	/**
	 * Permite convertir un short a una tira de byte[]. Esta tira tendra un largo
	 * fijo de dos elementos.
	 * Luego puede ser reconvertido utilizando {@link #toShort(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link DataOutput#writeShort(int)}
	 * suponiendo que se castea el short recibido a un int.
	 */
	public static byte[] toByte(short s) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte)(((int)s >> 8) & 0xFF);
		bytes[1] = (byte)((int)s & 0xFF);

		return bytes;
	}

	/**
	 * Permite reconvertir a un short una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(short)}.
	 * La tira de byte[] pasada debe tener dos elementos.
	 * La conversion se realiza segun lo expresado por {@link DataInput#readShort()}.
	 */
	public static short toShort(byte[] bytes) {
		return (short)((bytes[0] << 8) + (bytes[1] & 0xFF));
	}

	/**
	 * Permite convertir un array de short a una tira de byte[]. Esta tira tendra un largo
	 * igual al largo de s * 2.
	 * Luego puede ser reconvertido utilizando {@link #toShortArray(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link #toByte(short)}.
	 */
	public static byte[] toByte(short[] s) {
		byte[] currentBytes;

		byte[] bytes = new byte[s.length * 2];
		for (int i = 0; i < s.length; i++) {
			currentBytes = toByte(s[i]);
			bytes[i * 2] = currentBytes[0];
			bytes[i * 2 + 1] = currentBytes[1];
		}

		return bytes;
	}

	/**
	 * Permite reconvertir a un array de short una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(short[])}.
	 * La tira devuelta tendra tantos elementos como el parametro pasado / 2.
	 * La conversion se realiza segun lo expresado por {@link #toShort(byte[])}.
	 */
	public static short[] toShortArray(byte[] bytes) {
		byte[] currentBytes = new byte[2];

		short[] shorts = new short[bytes.length / 2];
		for (int i = 0; i < bytes.length / 2; i++) {
			currentBytes[0] = bytes[i * 2];
			currentBytes[1] = bytes[i * 2 + 1];
			shorts[i] = toShort(currentBytes);
		}

		return shorts;
	}

	// -----------------------------------------------------------------------------------

	// ints

	/**
	 * Permite convertir un int a una tira de byte[]. Esta tira tendra un largo
	 * fijo de cuatro elementos.
	 * Luego puede ser reconvertido utilizando {@link #toInt(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link DataOutput#writeInt(int)}.
	 */
	public static byte[] toByte(int i) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)((i >> 24) & 0xFF);
		bytes[1] = (byte)((i >> 16) & 0xFF);
		bytes[2] = (byte)((i >> 8) & 0xFF);
		bytes[3] = (byte)(i & 0xFF);

		return bytes;
	}

	/**
	 * Permite reconvertir a un int una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(int)}.
	 * La tira de byte[] pasada debe tener cuatro elementos.
	 * La conversion se realiza segun lo expresado por {@link DataInput#readInt()}.
	 */
	public static int toInt(byte[] bytes) {
		return (int)(((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF));
	}

	/**
	 * Permite convertir un array de int a una tira de byte[]. Esta tira tendra un largo
	 * igual al largo de i * 4.
	 * Luego puede ser reconvertido utilizando {@link #toIntArray(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link #toByte(int)}.
	 */
	public static byte[] toByte(int[] i) {
		byte[] currentBytes;

		byte[] bytes = new byte[i.length * 4];
		for (int j = 0; j < i.length; j++) {
			currentBytes = toByte(i[j]);
			bytes[j * 4] = currentBytes[0];
			bytes[j * 4 + 1] = currentBytes[1];
			bytes[j * 4 + 2] = currentBytes[2];
			bytes[j * 4 + 3] = currentBytes[3];
		}

		return bytes;
	}

	/**
	 * Permite reconvertir a un array de int una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(int[])}.
	 * La tira devuelta tendra tantos elementos como el parametro pasado / 4.
	 * La conversion se realiza segun lo expresado por {@link #toInt(byte[])}.
	 */
	public static int[] toIntArray(byte[] bytes) {
		byte[] currentBytes = new byte[4];

		int[] ints = new int[bytes.length / 4];
		for (int i = 0; i < bytes.length / 4; i++) {
			currentBytes[0] = bytes[i * 4];
			currentBytes[1] = bytes[i * 4 + 1];
			currentBytes[2] = bytes[i * 4 + 2];
			currentBytes[3] = bytes[i * 4 + 3];
			ints[i] = toInt(currentBytes);
		}

		return ints;
	}

	// -----------------------------------------------------------------------------------

	// longs

	/**
	 * Permite convertir un long a una tira de byte[]. Esta tira tendra un largo
	 * fijo de ocho elementos.
	 * Luego puede ser reconvertido utilizando {@link #toLong(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link DataOutput#writeLong(long)}.
	 */
	public static byte[] toByte(long l) {
		byte[] bytes = new byte[8];
		int despl = 56;
		for (int i = 0; i < 7; i++) {
			bytes[i] = (byte)((l >> despl) & 0xFF);
			despl -= 8;
		}
		bytes[7] = (byte)(l & 0xFF);

		return bytes;
	}

	/**
	 * Permite reconvertir a un long una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(long)}.
	 * La tira de byte[] pasada debe tener ocho elementos.
	 * La conversion se realiza segun lo expresado por {@link DataInput#readLong()}.
	 */
	public static long toLong(byte[] bytes) {
		return (long)(((long)(bytes[0] & 0xFF) << 56) | ((long)(bytes[1] & 0xFF) << 48) | ((long)(bytes[2] & 0xFF) << 40) |
				((long)(bytes[3] & 0xFF) << 32) | ((long)(bytes[4] & 0xFF) << 24) | ((long)(bytes[5] & 0xFF) << 16) |
				((long)(bytes[6] & 0xFF) << 8) + (long)(bytes[7] & 0xFF));
	}

	/**
	 * Permite convertir un array de long a una tira de byte[]. Esta tira tendra un largo
	 * igual al largo de l * 8.
	 * Luego puede ser reconvertido utilizando {@link #toLongArray(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link #toByte(long)}.
	 */
	public static byte[] toByte(long[] l) {
		byte[] currentBytes;

		byte[] bytes = new byte[l.length * 8];
		for (int i = 0; i < l.length; i++) {
			currentBytes = toByte(l[i]);
			for (int j = 0; j < 8; j++) {
				bytes[i * 8 + j] = currentBytes[j];
			}
		}

		return bytes;
	}

	/**
	 * Permite reconvertir a un array de long una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(long[])}.
	 * La tira devuelta tendra tantos elementos como el parametro pasado / 8.
	 * La conversion se realiza segun lo expresado por {@link #toLong(byte[])}.
	 */
	public static long[] toLongArray(byte[] bytes) {
		byte[] currentBytes = new byte[8];

		long[] longs = new long[bytes.length / 8];
		for (int i = 0; i < bytes.length / 8; i++) {
			for (int j = 0; j < 8; j++) {
				currentBytes[j] = bytes[i * 8 + j];
			}
			longs[i] = toLong(currentBytes);
		}

		return longs;
	}

	// -----------------------------------------------------------------------------------

	// floats

	/**
	 * Permite convertir un float a una tira de byte[]. Esta tira tendra un largo
	 * fijo de cuatro elementos.
	 * Luego puede ser reconvertido utilizando {@link #toFloat(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link DataOutput#writeFloat(float)}.
	 */
	public static byte[] toByte(float f) {
		int i = Float.floatToIntBits(f);
		return toByte(i);
	}

	/**
	 * Permite reconvertir a un float una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(float)}.
	 * La tira de byte[] pasada debe tener cuatro elementos.
	 * La conversion se realiza segun lo expresado por {@link DataInput#readFloat()}.
	 */
	public static float toFloat(byte[] bytes) {
		int i = toInt(bytes);

		return Float.intBitsToFloat(i);
	}

	/**
	 * Permite convertir un array de float a una tira de byte[]. Esta tira tendra un largo
	 * igual al largo de i * 4.
	 * Luego puede ser reconvertido utilizando {@link #toFloatArray(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link #toByte(float)}.
	 */
	public static byte[] toByte(float[] f) {
		byte[] currentBytes;

		byte[] bytes = new byte[f.length * 4];
		for (int i = 0; i < f.length; i++) {
			currentBytes = toByte(f[i]);
			bytes[i * 4] = currentBytes[0];
			bytes[i * 4 + 1] = currentBytes[1];
			bytes[i * 4 + 2] = currentBytes[2];
			bytes[i * 4 + 3] = currentBytes[3];
		}

		return bytes;
	}

	/**
	 * Permite reconvertir a un array de float una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(float[])}.
	 * La tira devuelta tendra tantos elementos como el parametro pasado / 4.
	 * La conversion se realiza segun lo expresado por {@link #toFloat(byte[])}.
	 */
	public static float[] toFloatArray(byte[] bytes) {
		byte[] currentBytes = new byte[4];

		float[] floats = new float[bytes.length / 4];
		for (int i = 0; i < bytes.length / 4; i++) {
			currentBytes[0] = bytes[i * 4];
			currentBytes[1] = bytes[i * 4 + 1];
			currentBytes[2] = bytes[i * 4 + 2];
			currentBytes[3] = bytes[i * 4 + 3];
			floats[i] = toFloat(currentBytes);
		}

		return floats;
	}

	// -----------------------------------------------------------------------------------

	// doubles

	/**
	 * Permite convertir un double a una tira de byte[]. Esta tira tendra un largo
	 * fijo de ocho elementos.
	 * Luego puede ser reconvertido utilizando {@link #toDouble(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link DataOutput#writeDouble(double)}.
	 */
	public static byte[] toByte(double d) {
		long l = Double.doubleToLongBits(d);
		return toByte(l);
	}

	/**
	 * Permite reconvertir a un double una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(double)}.
	 * La tira de byte[] pasada debe tener ocho elementos.
	 * La conversion se realiza segun lo expresado por {@link DataInput#readDouble()}.
	 */
	public static double toDouble(byte[] bytes) {
		long l = toLong(bytes);

		return Double.longBitsToDouble(l);
	}

	/**
	 * Permite convertir un array de double a una tira de byte[]. Esta tira tendra un largo
	 * igual al largo de i * 8.
	 * Luego puede ser reconvertido utilizando {@link #toDoubleArray(byte[])}.
	 * La conversion se realiza segun lo expresado por {@link #toByte(double)}.
	 */
	public static byte[] toByte(double[] d) {
		byte[] currentBytes;

		byte[] bytes = new byte[d.length * 8];
		for (int i = 0; i < d.length; i++) {
			currentBytes = toByte(d[i]);
			for (int j = 0; j < 8; j++) {
				bytes[i * 8 + j] = currentBytes[j];
			}
		}

		return bytes;
	}

	/**
	 * Permite reconvertir a un array de double una tira de byte[] que fue previamente obtenida
	 * usando {@link #toByte(double[])}.
	 * La tira devuelta tendra tantos elementos como el parametro pasado / 8.
	 * La conversion se realiza segun lo expresado por {@link #toDouble(byte[])}.
	 */
	public static double[] toDoubleArray(byte[] bytes) {
		byte[] currentBytes = new byte[8];

		double[] doubles = new double[bytes.length / 8];
		for (int i = 0; i < bytes.length / 8; i++) {
			for (int j = 0; j < 8; j++) {
				currentBytes[j] = bytes[i * 8 + j];
			}
			doubles[i] = toDouble(currentBytes);
		}

		return doubles;
	}
}
