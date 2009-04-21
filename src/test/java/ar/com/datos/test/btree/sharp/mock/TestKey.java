package ar.com.datos.test.btree.sharp.mock;

import ar.com.datos.btree.elements.Key;

/**
 * {@link Key} para tests de Árbol B# de memoria.
 *
 * @author fvalido
 */
public class TestKey implements Key {
	private Integer value;

	public TestKey(int value) {
		this.value = new Integer(value);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Key o) {
		TestKey key = (TestKey)o;
		return this.value.compareTo(key.value);
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		TestKey o = (TestKey)obj;
		return this.value.equals(o.value);
	}
}
