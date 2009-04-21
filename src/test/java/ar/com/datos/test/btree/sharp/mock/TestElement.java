package ar.com.datos.test.btree.sharp.mock;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;

/**
 * {@link Element} para tests de Árbol B# de memoria.
 *
 * @author fvalido
 */
public class TestElement implements Element<TestKey> {
	private TestKey key;
	private List<String> valuesElement;

	public TestElement(int primaryKey, String chain) {
		this.key = new TestKey(primaryKey);
		this.valuesElement = new LinkedList<String>();
		this.valuesElement.add(chain);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.elements.Element#getKey()
	 */
	@Override
	public TestKey getKey() {
		return this.key;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.elements.Element#updateElement(ar.com.datos.btree.elements.Element)
	 */
	@Override
	public boolean updateElement(Element element) {
		TestElement testElement = (TestElement)element;
		Iterator<String> it = testElement.valuesElement.iterator();
		while (it.hasNext()) {
			this.valuesElement.add(it.next());
		}

		return false;
	}

	@Override
	public String toString() {
		String returnValue = this.key.toString();
		return returnValue;
	}

	@Override
	public boolean equals(Object obj) {
		TestElement o = (TestElement)obj;
		return this.key.equals(o.key);
	}

}
