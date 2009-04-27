package ar.com.datos.test.btree.sharp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import ar.com.datos.btree.BTree;
import ar.com.datos.btree.BTreeIterator;
import ar.com.datos.btree.BTreeSharpFactory;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.test.ExtendedTestCase;
import ar.com.datos.test.btree.sharp.mock.disk.TestElementAndTestKeyListSerializerFactory;
import ar.com.datos.test.btree.sharp.mock.disk.TestElementDisk;
import ar.com.datos.test.btree.sharp.mock.disk.TestKeyDisk;

/**
 * Test para Arbol B# en Disco.
 *
 * @author fvalido
 */
public class TestBTreeSharpDisk extends ExtendedTestCase {
	private static final String LEAF_FILE = "resources/temp/archivoHojas.dat";
	private static final String INTERNAL_FILE = "resources/temp/archivoInternos.dat";
	private BTree<TestElementDisk, TestKeyDisk> bTreeSharp;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		this.bTreeSharp = new BTreeSharpFactory<TestElementDisk, TestKeyDisk>().createBTreeSharpDisk(INTERNAL_FILE, LEAF_FILE, 128, 128, TestElementAndTestKeyListSerializerFactory.class, true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.bTreeSharp.destroy();
	}

	/**
	 * Test completo. Agrega una serie de elementos al azar, se fija uno por uno
	 * que pueda encontrarlos nuevamente en el �rbol.
	 * Recorre todos los elementos de las hojas del �rbol fij�ndose que est�n en orden.
	 * Verifica que la cantidad de elementos final del �rbol sea la correcta.
	 */
	public void testRandomInsertion() {
		try {
			BTree<TestElementDisk, TestKeyDisk> bTreeSharp = this.bTreeSharp;

			int maxKeyValue = 1000000;
			
			// Inserto elementos al azar en el �rbol.
			int randomCount;
			Integer k = null;
			String keyValue;
			List<String> inserted = new ArrayList<String>();
			for(int i = 0; i < 10000; i++){
				// Formo la clave como la concatenaci�n de entre 1 y 3 n�meros (as� hago que
				// la clave mida diferentes tama�os).
				randomCount = (int) Math.round((Math.random()*2)) + 1;
				keyValue = "";
				for (int j = 0; j < randomCount; j++) {
					k = (int) (Math.random()*maxKeyValue);
					keyValue += k.toString();
				}
				
				bTreeSharp.addElement(new TestElementDisk(keyValue, k));
				inserted.add(keyValue);
			}
			// Me quedo con una colecci�n desordenada de esos elementos al azar.
			Collections.shuffle(inserted);

			// Reviso que cada uno de los elementos que agregu� se encuentre en el �rbol.
			Iterator<String> it = inserted.iterator();
			TestElementDisk element = null;
			TestKeyDisk key;
			while (it.hasNext()) {
				key = new TestKeyDisk(it.next());
				element = bTreeSharp.findElement(key);
				assertNotNull(element);
				assertEquals(key, element.getKey());
			}

			// Recorro las hojas hacia adelante viendo que los elementos est�n ordenados y que
			// haya tantos elementos como los que insert�.
			int cantElementos = 0;
			TestElementDisk minorElement = new TestElementDisk("", 0);
			element = null;
			TestElementDisk previousElement;
			BTreeIterator<TestElementDisk> bTreeIterator = bTreeSharp.iterator(minorElement.getKey());
			while (bTreeIterator.hasNext()) {
				previousElement = element;
				element = bTreeIterator.next();
				cantElementos++;
				if (previousElement != null) {
					assertLower(previousElement.getKey(), element.getKey());
				}
			}
			assertEquals(cantElementos, new HashSet<String>(inserted).size());
				
			// Recorro al vesre (NOTA: Es un test, por eso repito c�digo a lo pavote)
			cantElementos = 0;
			TestElementDisk biggestElement = new TestElementDisk("99999999999999999999999999999999999", 0);
			element = null;
			TestElementDisk nextElement;
			bTreeIterator = bTreeSharp.iterator(biggestElement.getKey());
			while (bTreeIterator.hasPrevious()) {
				nextElement = element;
				element = bTreeIterator.previous();
				cantElementos++;
				if (nextElement != null) {
					assertBigger(nextElement.getKey(), element.getKey());
				}
			}
			assertEquals(cantElementos, new HashSet<String>(inserted).size());
		} catch (BTreeException e) {
			// No deberia ocurrir.
			e.printStackTrace();
		}
	}
}
