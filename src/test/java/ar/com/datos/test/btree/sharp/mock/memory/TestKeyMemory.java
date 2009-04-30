package ar.com.datos.test.btree.sharp.mock.memory;

import ar.com.datos.btree.elements.Key;

/**
 * {@link Key} para tests de Árbol B# en memoria.
 *
 * @author fvalido
 */
public class TestKeyMemory implements Key {
	private Integer value;

	public TestKeyMemory(int value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Key o) {
		TestKeyMemory key = (TestKeyMemory)o;
		return this.value.compareTo(key.value);
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		TestKeyMemory o = (TestKeyMemory)obj;
		return this.value.equals(o.value);
	}
}
