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

	public void assertBigger(Object bigger, Object lower, Comparator<Object> c) {
		assertTrue(bigger.getClass().isAssignableFrom(lower.getClass()) || lower.getClass().isAssignableFrom(bigger.getClass()));
		assertTrue(c.compare(bigger, lower) > 0);
	}
	
	@SuppressWarnings("unchecked")
	public void assertBiggerOrEquals(Comparable bigger, Comparable lower) {
		assertTrue(bigger.getClass().isAssignableFrom(lower.getClass()) || lower.getClass().isAssignableFrom(bigger.getClass()));
		assertTrue(bigger.compareTo(lower) >= 0);
	}
	
	public void assertBiggerOrEquals(Object bigger, Object lower, Comparator<Object> c) {
		assertTrue(bigger.getClass().isAssignableFrom(lower.getClass()) || lower.getClass().isAssignableFrom(bigger.getClass()));
		assertTrue(c.compare(bigger, lower) >= 0);
	}
	
	public void assertLower(Comparable lower, Comparable bigger) {
		assertBigger(bigger, lower);
	}

	public void assertLower(Object lower, Object bigger, Comparator<Object> c) {
		assertBigger(bigger, lower, c);
	}
	
	public void assertLowerOrEquals(Comparable lower, Comparable bigger) {
		assertBiggerOrEquals(bigger, lower);
	}
	
	public void assertLowerOrEquals(Object lower, Object bigger, Comparator<Object> c) {
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
