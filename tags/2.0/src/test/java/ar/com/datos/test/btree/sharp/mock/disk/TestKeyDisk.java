package ar.com.datos.test.btree.sharp.mock.disk;

import ar.com.datos.btree.elements.Key;

/**
 * {@link Key} para tests de Arbol B# en disco.
 *
 * @author fvalido
 */
public class TestKeyDisk implements Key {
	private String value;

	public TestKeyDisk(String value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Key o) {
		TestKeyDisk key = (TestKeyDisk)o;
		return this.value.compareTo(key.value);
	}

	public String getValue() {
		return this.value;
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
		TestKeyDisk o = (TestKeyDisk)obj;
		return this.value.equals(o.value);
	}
}
