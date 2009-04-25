package ar.com.datos.test.indexer;

import java.util.Collection;

import ar.com.datos.file.variableLength.OffsetAddress;
import ar.com.datos.indexer.Indexer;
import ar.com.datos.indexer.keywordIndexer.KeyCount;
import ar.com.datos.serializer.Serializer;
import junit.framework.TestCase;

public class TestIndexer extends TestCase {

	public void testIndexacionInicial() throws Exception {
		String fileName = "blah";
		OffsetAddress idDocumento = new OffsetAddress(23L);
		Serializer<OffsetAddress> serializadorDocumento = null;
		Indexer<OffsetAddress> unIndexador = new Indexer<OffsetAddress>(fileName, serializadorDocumento);
		unIndexador.startSession();
		unIndexador.addTerms(idDocumento, "hola", "hola", "mano");
		unIndexador.addTerms(idDocumento, "bueno", "chau");
		unIndexador.addTerms(idDocumento, "bueno", "hola");
		unIndexador.endSession();
		Collection<KeyCount<OffsetAddress>> documentosRecuperados = unIndexador.findTerm("hola");
		assertEquals(1, documentosRecuperados.size());
	}
}
