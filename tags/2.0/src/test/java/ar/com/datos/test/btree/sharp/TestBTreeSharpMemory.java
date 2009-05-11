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
import ar.com.datos.test.btree.sharp.mock.memory.TestElementMemory;
import ar.com.datos.test.btree.sharp.mock.memory.TestKeyMemory;

/**
 * Test para �rbol en Memoria.
 *
 * @author fvalido
 */
public class TestBTreeSharpMemory extends ExtendedTestCase {
	private BTree<TestElementMemory, TestKeyMemory> bTreeSharp;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.bTreeSharp = new BTreeSharpFactory<TestElementMemory, TestKeyMemory>().createBTreeSharpMemory(9);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.bTreeSharp.close();
	}

//	/**
//	 * Test as� nom�s. Si alguna vez se rompe todo se puede usar como base para ver que pasa...
//	 */
//	public void testHiperPedorro() {
//		try {
//			BTree<TestElementMemory, TestKeyMemory> btree = this.bTreeSharp;
////			int[] lista = new int[] {864592, 544001, 977959, 82652, 576828, 482680, 556883, 76029, 124459, 531854, 825079, 90817, 786551, 803344, 485399, 180588, 844739, 347102, 353631, 171813, 831493, 688082, 568300, 711788, 677318, 74032, 380277, 525830, 586663, 697769, 963771, 638063, 859956};
////			int[] lista = new int[] {202741, 860454, 120951, 353667, 410077, 778268, 7637, 626014, 247353, 569318, 639908, 935376, 424305, 706023, 774299, 605621, 735045, 76573, 229628, 833598, 603470, 688032, 347415, 911619, 678929, 301082, 277124, 561038, 513910, 660261, 243452, 467049, 489315, 531392, 977366, 168299, 317907, 586997, 48204, 299635, 652832, 290872, 921102, 812284, 555622, 324548};
////			int[] lista = new int[] {123681, 836937, 346812, 928797, 440305, 509875, 684002, 952566, 137171, 765420, 827956, 806663, 578622, 176723};
//			int[] lista = new int[] {663611, 190977, 424018, 638201, 121201, 779672, 821367, 98942, 680362, 469613};
//			for (int i = 0; i < lista.length; i++) {
//				if (lista[i] == 469613) {
//					System.out.println();
//				}
//				btree.addElement(new TestElementMemory(lista[i], new Integer(lista[i]).toString()));
//			}
//			
//			TestElementMemory element = null;
//			TestKeyMemory key;
//			for (int i = 0; i < lista.length; i++) {
//				key = new TestKeyMemory(lista[i]);
//				element = btree.findElement(key);
//				assertNotNull(element);
//				assertEquals(key, element.getKey());
//			}
//		} catch (BTreeException e) {
//			// No deber�a ocurrir.	
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * Test intensivo. Puede tardar MUCHO (m�s de 40 minutos).
	 */
	public void testIntensivo() {
		BTreeSharpFactory<TestElementMemory, TestKeyMemory> bTreeSharpFactory = new BTreeSharpFactory<TestElementMemory, TestKeyMemory>();
		for (int i = 3; i < 12; i++) { // Tama�o nodo
			for (int j = 0; j < 3; j++) { // N�mero de pruebas por tama�o.
				this.bTreeSharp = bTreeSharpFactory.createBTreeSharpMemory(i); 
				testRandomInsertion();
			}
		}
	}
	
	/**
	 * Test completo. Agrega una serie de elementos al azar, se fija uno por uno
	 * que pueda encontrarlos nuevamente en el �rbol.
	 * Recorre todos los elementos de las hojas del �rbol fij�ndose que est�n en orden.
	 * Verifica que la cantidad de elementos final del �rbol sea la correcta.
	 */
	public void testRandomInsertion() {
		try {
			BTree<TestElementMemory, TestKeyMemory> bTreeSharp = this.bTreeSharp;

			int maxKeyValue = 1000000;
			
			// Inserto elementos al azar en el �rbol.
			int j;
			List<TestKeyMemory> inserted = new ArrayList<TestKeyMemory>();
			TestKeyMemory key;
			TestElementMemory element;
			for(int i = 0; i < 100000; i++){
				j = (int) (Math.random()*maxKeyValue);
				element = new TestElementMemory(j, new Integer(j).toString());
				bTreeSharp.addElement(element);
				inserted.add(element.getKey());
			}
			// Me quedo con una colecci�n desordenada de esos elementos al azar.
			Collections.shuffle(inserted);

			// Reviso que cada uno de los elementos que agregu� se encuentre en el �rbol.
			Iterator<TestKeyMemory> it = inserted.iterator();
			while (it.hasNext()) {
				key = it.next();
				element = bTreeSharp.findElement(key);
				assertNotNull(element);
				assertEquals(key, element.getKey());
			}

			// Recorro las hojas hacia adelante viendo que los elementos est�n ordenados y que
			// haya tantos elementos como los que insert�.
			int cantElementos = 0;
			TestKeyMemory lowestKey = new TestKeyMemory(-1);
			element = null;
			TestElementMemory previousElement;
			BTreeIterator<TestElementMemory> bTreeIterator = bTreeSharp.iterator(lowestKey);
			while (bTreeIterator.hasNext()) {
				previousElement = element;
				element = bTreeIterator.next();
				cantElementos++;
				if (previousElement != null) {
					assertLower(previousElement.getKey(), element.getKey());
				}
			}
			assertEquals(cantElementos, new HashSet<TestKeyMemory>(inserted).size());
				
			// Recorro al vesre (NOTA: Es un test, por eso repito c�digo a lo pavote)
			cantElementos = 0;
			TestKeyMemory biggestKey = new TestKeyMemory(maxKeyValue + 1);
			element = null;
			TestElementMemory nextElement;
			bTreeIterator = bTreeSharp.iterator(biggestKey);
			while (bTreeIterator.hasPrevious()) {
				nextElement = element;
				element = bTreeIterator.previous();
				cantElementos++;
				if (nextElement != null) {
					assertBigger(nextElement.getKey(), element.getKey());
				}
			}
			assertEquals(cantElementos, new HashSet<TestKeyMemory>(inserted).size());
		} catch (BTreeException e) {
			// No deberia ocurrir.
			e.printStackTrace();
		}
	}
}
