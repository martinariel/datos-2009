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
	private static final int BLOCK_SIZE = 512;
	private BTree<TestElementDisk, TestKeyDisk> bTreeSharp;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		this.bTreeSharp = new BTreeSharpFactory<TestElementDisk, TestKeyDisk>().createBTreeSharpDisk(INTERNAL_FILE, LEAF_FILE, BLOCK_SIZE, TestElementAndTestKeyListSerializerFactory.class, true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.bTreeSharp.close();
	}
	
//	/**
//	 * Test así nomás. Si alguna vez se rompe todo se puede usar como base para ver que pasa...
//	 */
//	public void testHiperPedorro() {
//		try {
//			BTree<TestElementDisk, TestKeyDisk> btree = this.bTreeSharp;
////			String[] lista = new String[] {"522630", "925861336572", "940027", "12275214072481695", "676159818757214", "635144937520", "343025", "885287", "855731162813196136"};
////			String[] lista = new String[] {"990707", "814561", "893024", "95592444739", "655170748521613866", "57971836335", "7775729897", "669833", "864004652398", "563959408814957898", "286823968160", "793501205650896534", "164502591619", "451579837255", "911722453988197619", "978636376735", "932372780949243559", "201413492933922261",  "316785921097439538", "119584708352261883", "365493879233", "869601452491", "755771699631354201", "536773693357", "545499"};
////			String[] lista = new String[] {"681522", "605085302661", "819896552595", "713905", "83106610053", "304329", "370921", "653743998796184703", "507495", "524550850161", "754145623790", "863771131624", "391649315367", "51417680752", "597275", "274884162412328600", "741142258335375664", "425729761484", "41812602287256919", "599626"};
////			String[] lista = new String[] {"843852175524142970", "320287", "89816", "233434533092", "251759213173", "606654", "802892", "69112", "647556794147", "880056307831", "916873820676", "643302883478861439", "641183542966", "858160174512", "388741162467", "256422877950", "902223432503", "679611", "802362538754", "745661464368", "794068688209", "757732912895734091", "944056156303", "379503"};
//			String[] lista = new String[] {"129877770381", "686273254708902074", "990573", "11170896058618071", "973493875925", "490880901829", "477433192507262817", "400295548264112008", "34104471499545894", "281045295071", "223060504466", "534475", "8901922493", "282299", "231180", "715346", "309158645052"};
//			for (int i = 0; i < lista.length; i++) {
//				btree.addElement(new TestElementDisk(lista[i], 1));
//			}
//			
//			TestElementDisk element = null;
//			TestKeyDisk key;
//			for (int i = 0; i < lista.length; i++) {
//				key = new TestKeyDisk(lista[i]);
//				element = btree.findElement(key);
//				assertNotNull(element);
//				assertEquals(key, element.getKey());
//			}
//
//		} catch (BTreeException e) {
//			// No debería ocurrir.	
//			e.printStackTrace();
//		}
//	}
	
//	/**
//	 * Test intensivo. Puede tardar MUCHO (muchas horas y horas y horas) 
//	 */
//	public void testIntensivo() {
//		BTreeSharpFactory<TestElementDisk, TestKeyDisk> bTreeSharpFactory = new BTreeSharpFactory<TestElementDisk, TestKeyDisk>();
//		int blockSize = 256;
//		for (int i = 0; i < 9; i++) {
//			for (int k = 0; k < 5; k++) { // Número de pruebas por tamaño.
//				this.bTreeSharp = bTreeSharpFactory.createBTreeSharpDisk(INTERNAL_FILE, LEAF_FILE, blockSize, TestElementAndTestKeyListSerializerFactory.class, true); 
//				testRandomInsertion();
//			}
//			blockSize *= 2;
//		}
//	}
	
	/**
	 * Test completo. Agrega una serie de elementos al azar, se fija uno por uno
	 * que pueda encontrarlos nuevamente en el árbol.
	 * Recorre todos los elementos de las hojas del árbol fijándose que estén en orden.
	 * Verifica que la cantidad de elementos final del árbol sea la correcta.
	 */
	public void testRandomInsertion() {
		try {
			BTree<TestElementDisk, TestKeyDisk> bTreeSharp = this.bTreeSharp;

			int maxKeyValue = 1000000;
			
			// Inserto elementos al azar en el árbol.
			int randomCount;
			Integer k = null;
			String keyValue;
			TestElementDisk element;
			TestKeyDisk key;
			List<TestKeyDisk> inserted = new ArrayList<TestKeyDisk>();
			for(int i = 0; i < 50000; i++){
				// Formo la clave como la concatenación de entre 1 y 3 números (así hago que
				// la clave mida diferentes tamaños).
				randomCount = (int) Math.round((Math.random()*2)) + 1;
				keyValue = "";
				for (int j = 0; j < randomCount; j++) {
					k = (int) (Math.random()*maxKeyValue);
					keyValue += k.toString();
				}
				element = new TestElementDisk(keyValue, k);
				bTreeSharp.addElement(element);
				inserted.add(element.getKey());
			}
			// Me quedo con una colección desordenada de esos elementos al azar.
			Collections.shuffle(inserted);

			// Reviso que cada uno de los elementos que agregué se encuentre en el árbol.
			Iterator<TestKeyDisk> it = inserted.iterator();
			element = null;
			while (it.hasNext()) {
				key = it.next();
				element = bTreeSharp.findElement(key);
				assertNotNull(element);
				assertEquals(key, element.getKey());
			}

			// Recorro las hojas hacia adelante viendo que los elementos estén ordenados y que
			// haya tantos elementos como los que inserté.
			int cantElementos = 0;
			TestKeyDisk lowestKey = new TestKeyDisk("");
			element = null;
			TestElementDisk previousElement;
			BTreeIterator<TestElementDisk> bTreeIterator = bTreeSharp.iterator(lowestKey);
			while (bTreeIterator.hasNext()) {
				previousElement = element;
				element = bTreeIterator.next();
				cantElementos++;
				if (previousElement != null) {
					assertLower(previousElement.getKey(), element.getKey());
				}
			}
			assertEquals(cantElementos, new HashSet<TestKeyDisk>(inserted).size());
				
			// Recorro al vesre (NOTA: Es un test, por eso repito código a lo pavote)
			cantElementos = 0;
			TestKeyDisk biggestKey = new TestKeyDisk("99999999999999999999999999999999999");
			element = null;
			TestElementDisk nextElement;
			bTreeIterator = bTreeSharp.iterator(biggestKey);
			while (bTreeIterator.hasPrevious()) {
				nextElement = element;
				element = bTreeIterator.previous();
				cantElementos++;
				if (nextElement != null) {
					assertBigger(nextElement.getKey(), element.getKey());
				}
			}
			assertEquals(cantElementos, new HashSet<TestKeyDisk>(inserted).size());
		} catch (BTreeException e) {
			// No deberia ocurrir.
			e.printStackTrace();
		}
	}
}
