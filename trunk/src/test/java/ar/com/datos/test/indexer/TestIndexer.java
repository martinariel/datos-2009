package ar.com.datos.test.indexer;

import java.util.Collection;

import junit.framework.TestCase;
import ar.com.datos.btree.BTree;
import ar.com.datos.btree.BTreeSharpFactory;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.file.variableLength.address.OffsetAddressSerializer;
import ar.com.datos.indexer.SimpleSessionIndexer;
import ar.com.datos.indexer.tree.IndexerTreeElement;
import ar.com.datos.indexer.tree.IndexerTreeKey;
import ar.com.datos.utils.sort.external.KeyCount;

public class TestIndexer extends TestCase {

	public void testIndexacionInicial() throws Exception {
		String fileName = "blah";
		OffsetAddress idDocumento = new OffsetAddress(23L);
		SimpleSessionIndexer<OffsetAddress> unIndexador = new SimpleSessionIndexer<OffsetAddress>(fileName, new OffsetAddressSerializer()) {
			@Override
			protected BTree<IndexerTreeElement<OffsetAddress>, IndexerTreeKey> constructIndexedElements(String fileName) {
				return new BTreeSharpFactory<IndexerTreeElement<OffsetAddress>, IndexerTreeKey>().createBTreeSharpMemory(16, 16);
			}
		};
		unIndexador.startSession();
		unIndexador.addTerms(idDocumento, "hola", "hola", "mano");
		unIndexador.addTerms(idDocumento, "bueno", "chau");
		unIndexador.addTerms(idDocumento, "bueno", "hola");
		unIndexador.endSession();
		Collection<KeyCount<OffsetAddress>> documentosRecuperados = unIndexador.findTerm("hola");
		assertEquals(1, documentosRecuperados.size());
		for (KeyCount<OffsetAddress> cuenta: documentosRecuperados) {
			assertEquals(3, cuenta.getCount().intValue());
		}
		documentosRecuperados = unIndexador.findTerm("bueno");
		assertEquals(1, documentosRecuperados.size());
		for (KeyCount<OffsetAddress> cuenta: documentosRecuperados) {
			assertEquals(2, cuenta.getCount().intValue());
		}
		
		documentosRecuperados = unIndexador.findTerm("mano");
		assertEquals(1, documentosRecuperados.size());
		for (KeyCount<OffsetAddress> cuenta: documentosRecuperados) {
			assertEquals(1, cuenta.getCount().intValue());
		}
		documentosRecuperados = unIndexador.findTerm("chau");
		assertEquals(1, documentosRecuperados.size());
		for (KeyCount<OffsetAddress> cuenta: documentosRecuperados) {
			assertEquals(1, cuenta.getCount().intValue());
		}
	}
}
