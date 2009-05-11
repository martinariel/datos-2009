package ar.com.datos.test.btree.sharp.mock.memory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;

/**
 * {@link Element} para tests de Árbol B# en memoria.
 *
 * @author fvalido
 */
public class TestElementMemory implements Element<TestKeyMemory> {
	private TestKeyMemory key;
	private List<String> valuesElement;

	public TestElementMemory(int key, String chain) {
		this.key = new TestKeyMemory(key);
		this.valuesElement = new LinkedList<String>();
		this.valuesElement.add(chain);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.elements.Element#getKey()
	 */
	@Override
	public TestKeyMemory getKey() {
		return this.key;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.elements.Element#updateElement(ar.com.datos.btree.elements.Element)
	 */
	@Override
	public boolean updateElement(Element element) {
		TestElementMemory testElement = (TestElementMemory)element;
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
		TestElementMemory o = (TestElementMemory)obj;
		return this.key.equals(o.key);
	}

}
