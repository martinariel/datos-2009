package ar.com.datos.test;

import java.util.Comparator;

import junit.framework.TestCase;

/**
 * Extension de TestCase para que soporte Arrays y comparaciones de
 * mayor y menor.
 *
 * @author fvalido
 */
public class ExtendedTestCase extends TestCase {
	// Comparaciones de mayor y menor.
	
	@SuppressWarnings("unchecked")
	public void assertBigger(Comparable bigger, Comparable lower) {
		assertTrue(bigger.getClass().isAssignableFrom(lower.getClass()) || lower.getClass().isAssignableFrom(bigger.getClass()));
		assertTrue(bigger.compareTo(lower) > 0);
	}

	public <T, R extends T, S extends T> void assertBigger(R bigger, S lower, Comparator<T> c) {
		assertTrue(c.compare(bigger, lower) > 0);
	}
	
	@SuppressWarnings("unchecked")
	public void assertBiggerOrEquals(Comparable bigger, Comparable lower) {
		assertTrue(bigger.getClass().isAssignableFrom(lower.getClass()) || lower.getClass().isAssignableFrom(bigger.getClass()));
		assertTrue(bigger.compareTo(lower) >= 0);
	}
	
	public <T, R extends T, S extends T> void assertBiggerOrEquals(R bigger, S lower, Comparator<T> c) {
		assertTrue(c.compare(bigger, lower) >= 0);
	}
	
	@SuppressWarnings("unchecked")
	public void assertLower(Comparable lower, Comparable bigger) {
		assertBigger(bigger, lower);
	}

	public <T, R extends T, S extends T> void assertLower(R lower, S bigger, Comparator<T> c) {
		assertBigger(bigger, lower, c);
	}
	
	@SuppressWarnings("unchecked")
	public void assertLowerOrEquals(Comparable lower, Comparable bigger) {
		assertBiggerOrEquals(bigger, lower);
	}
	
	public <T, R extends T, S extends T> void assertLowerOrEquals(R lower, S bigger, Comparator<T> c) {
		assertBiggerOrEquals(bigger, lower, c);
	}
	
	// Comparaciones de Arrays
	
	public void assertEquals(boolean[] a, boolean[] b) {
		assertEquals(a.length, b.length);
		for (int i = 0; i < a.length; i++) {
			assertEquals(a[i], b[i]);
		}
	}

	public void assertEquals(byte[] a, byte[] b) {
		assertEquals(a.length, b.length);
		for (int i = 0; i < a.length; i++) {
			assertEquals(a[i], b[i]);
		}
	}

	public void assertEquals(char[] a, char[] b) {
		assertEquals(a.length, b.length);
		for (int i = 0; i < a.length; i++) {
			assertEquals(a[i], b[i]);
		}
	}

	public void assertEquals(short[] a, short[] b) {
		assertEquals(a.length, b.length);
		for (int i = 0; i < a.length; i++) {
			assertEquals(a[i], b[i]);
		}
	}

	public void assertEquals(int[] a, int[] b) {
		assertEquals(a.length, b.length);
		for (int i = 0; i < a.length; i++) {
			assertEquals(a[i], b[i]);
		}
	}

	public void assertEquals(long[] a, long[] b) {
		assertEquals(a.length, b.length);
		for (int i = 0; i < a.length; i++) {
			assertEquals(a[i], b[i]);
		}
	}

	public void assertEquals(float[] a, float[] b) {
		assertEquals(a.length, b.length);
		for (int i = 0; i < a.length; i++) {
			assertEquals(a[i], b[i]);
		}
	}

	public void assertEquals(double[] a, double[] b) {
		assertEquals(a.length, b.length);
		for (int i = 0; i < a.length; i++) {
			assertEquals(a[i], b[i]);
		}
	}
}
