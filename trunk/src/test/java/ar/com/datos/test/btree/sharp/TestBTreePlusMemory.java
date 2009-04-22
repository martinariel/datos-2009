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
import ar.com.datos.test.btree.sharp.mock.TestElement;
import ar.com.datos.test.btree.sharp.mock.TestKey;

/**
 * Test para �rbol en Memoria.
 *
 * @author fvalido
 */
public class TestBTreePlusMemory extends ExtendedTestCase {
	private BTree<TestElement, TestKey> bTreeSharp;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.bTreeSharp = new BTreeSharpFactory<TestElement, TestKey>().createBTreeSharpMemory((short)9, (short)9);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.bTreeSharp.destroy();
	}

//	/**
//	 * Test as� nom�s. Si alguna vez se rompe todo se puede usar como base para ver que pasa...
//	 */
//	public void testHiperPedorro() {
//		try {
//			BTree<TestElement, TestKey> btree = this.bTreeSharp;
////			int[] lista = new int[] {864592, 544001, 977959, 82652, 576828, 482680, 556883, 76029, 124459, 531854, 825079, 90817, 786551, 803344, 485399, 180588, 844739, 347102, 353631, 171813, 831493, 688082, 568300, 711788, 677318, 74032, 380277, 525830, 586663, 697769, 963771, 638063, 859956};
//			int[] lista = new int[] {202741, 860454, 120951, 353667, 410077, 778268, 7637, 626014, 247353, 569318, 639908, 935376, 424305, 706023, 774299, 605621, 735045, 76573, 229628, 833598, 603470, 688032, 347415, 911619, 678929, 301082, 277124, 561038, 513910, 660261, 243452, 467049, 489315, 531392, 977366, 168299, 317907, 586997, 48204, 299635, 652832, 290872, 921102, 812284, 555622, 324548};
//			for (int i = 0; i < lista.length; i++) {
//				btree.addElement(new TestElement(lista[i], new Integer(lista[i]).toString()));
//			}
//			
//			TestElement element = null;
//			TestKey key;
//			for (int i = 0; i < lista.length; i++) {
//				key = new TestKey(lista[i]);
//				element = btree.findElement(key);
//				assertNotNull(element);
//				assertEquals(key, element.getKey());
//			}
//		} catch (BTreeException e) {
//			// No deber�a ocurrir.	
//			e.printStackTrace();
//		}
//	}
	
//	/**
//	 * Test intensivo. Puede tardar MUCHO (m�s de 40 minutos).
//	 */
//	public void testIntensivo() {
//		BTreeSharpFactory<TestElement, TestKey> bTreeSharpFactory = new BTreeSharpFactory<TestElement, TestKey>();
//		for (short i = 3; i < 12; i++) { // Tama�o nodo interno
//			for (short j = 3; j < 12; j++) { // Tama�o nodo hoja
//				for (int k = 0; k < 3; k++) { // N�mero de pruebas por tama�o.
//					this.bTreeSharp = bTreeSharpFactory.createBTreeSharpMemory(i, j); 
//					testRandomInsertion();
//				}
//			}
//		}
//	}
	
	/**
	 * Test completo. Agrega una serie de elementos al azar, se fija uno por uno
	 * que pueda encontrarlos nuevamente en el �rbol.
	 * Recorre todos los elementos de las hojas del �rbol fij�ndose que est�n en orden.
	 * Verifica que la cantidad de elementos final del �rbol sea la correcta.
	 */
	public void testRandomInsertion() {
		try {
			BTree<TestElement, TestKey> bTreeSharp = this.bTreeSharp;

			int maxKeyValue = 1000000;
			
			// Inserto elementos al azar en el �rbol.
			int j;
			List<Integer> inserted = new ArrayList<Integer>();
			for(int i = 0; i < 100000; i++){
				j = (int) (Math.random()*maxKeyValue);
				bTreeSharp.addElement(new TestElement(j, new Integer(j).toString()));
				inserted.add(j);
			}
			// Me quedo con una colecci�n desordenada de esos elementos al azar.
			Collections.shuffle(inserted);

			// Reviso que cada uno de los elementos que agregu� se encuentre en el �rbol.
			Iterator<Integer> it = inserted.iterator();
			TestElement element = null;
			TestKey key;
			while (it.hasNext()) {
				key = new TestKey(it.next());
				element = bTreeSharp.findElement(key);
				assertNotNull(element);
				assertEquals(key, element.getKey());
			}

			// Recorro las hojas hacia adelante viendo que los elementos est�n ordenados y que
			// haya tantos elementos como los que insert�.
			int cantElementos = 0;
			TestElement minorElement = new TestElement(-1, "");
			element = null;
			TestElement previousElement;
			BTreeIterator<TestElement> bTreeIterator = bTreeSharp.iterator(minorElement.getKey());
			while (bTreeIterator.hasNext()) {
				previousElement = element;
				element = bTreeIterator.next();
				cantElementos++;
				if (previousElement != null) {
					assertLower(previousElement.getKey(), element.getKey());
				}
			}
			assertEquals(cantElementos, new HashSet<Integer>(inserted).size());
				
			// Recorro al vesre (NOTA: Es un test, por eso repito c�digo a lo pavote)
			cantElementos = 0;
			TestElement biggestElement = new TestElement(maxKeyValue + 1, "");
			element = null;
			TestElement nextElement;
			bTreeIterator = bTreeSharp.iterator(biggestElement.getKey());
			while (bTreeIterator.hasPrevious()) {
				nextElement = element;
				element = bTreeIterator.previous();
				cantElementos++;
				if (nextElement != null) {
					assertBigger(nextElement.getKey(), element.getKey());
				}
			}
			assertEquals(cantElementos, new HashSet<Integer>(inserted).size());
		} catch (BTreeException e) {
			// No deberia ocurrir.
			e.printStackTrace();
		}
	}


}
