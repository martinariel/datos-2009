package ar.com.datos.test.btree.sharp.mock.disk;

import ar.com.datos.btree.elements.Element;

/**
 * Element para tests de Arbol B# en disco.
 *
 * @author fvalido
 */
public class TestElementDisk implements Element<TestKeyDisk> {
	private TestKeyDisk key;
	private int position;

	public TestElementDisk(String key, int position) {
		this.key = new TestKeyDisk(key);
		this.position = position;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.elements.Element#getKey()
	 */
	public TestKeyDisk getKey() {
		return this.key;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.elements.Element#updateElement(ar.com.datos.btree.elements.Element)
	 */
	public boolean updateElement(Element element) {
		TestElementDisk testElement = (TestElementDisk)element;
		boolean returnValue = (this.position == testElement.position);

		this.position = testElement.position;

		return returnValue;
	}

	public int getPosition() {
		return this.position;
	}

	@Override
	public String toString() {
		String returnValue = this.key.toString();
		return returnValue;
	}

	@Override
	public boolean equals(Object obj) {
		TestElementDisk o = (TestElementDisk)obj;
		return this.key.equals(o.key);
	}

}
