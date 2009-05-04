package ar.com.datos.test.indexer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.file.variableLength.address.OffsetAddressSerializer;
import ar.com.datos.indexer.SimpleSessionIndexer;
import ar.com.datos.utils.sort.external.KeyCount;

public class TestIndexer extends TestCase {

	private static String pathName = "./resources/temp/";
	private static String filePreffix = "TestIndexer";
	private static String fileName =  pathName + filePreffix ;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// Limpio todos los archivos que genera el test
		File dir = new File(pathName);
		File[] files = dir.listFiles(new FileFilter(){

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(filePreffix);
			}
			
		});
		for (File f: files) f.delete();
	}
	public void testIndexacionInicial() throws Exception {
		OffsetAddress idDocumento = new OffsetAddress(23L);
		SimpleSessionIndexer<OffsetAddress> unIndexador = new SimpleSessionIndexer<OffsetAddress>(fileName, new OffsetAddressSerializer());
		unIndexador.startSession();
		unIndexador.addTerms(idDocumento, "hola", "hola", "mano");
		unIndexador.addTerms(idDocumento, "bueno", "chau");
		unIndexador.addTerms(idDocumento, "bueno", "hola");
		unIndexador.endSession();
		Collection<KeyCount<OffsetAddress>> documentosRecuperados = unIndexador.findTerm("hola").getAssociatedData();
		assertEquals(1, documentosRecuperados.size());
		for (KeyCount<OffsetAddress> cuenta: documentosRecuperados) {
			assertEquals(3, cuenta.getCount().intValue());
		}
		documentosRecuperados = unIndexador.findTerm("bueno").getAssociatedData();
		assertEquals(1, documentosRecuperados.size());
		for (KeyCount<OffsetAddress> cuenta: documentosRecuperados) {
			assertEquals(2, cuenta.getCount().intValue());
		}
		
		documentosRecuperados = unIndexador.findTerm("mano").getAssociatedData();
		assertEquals(1, documentosRecuperados.size());
		for (KeyCount<OffsetAddress> cuenta: documentosRecuperados) {
			assertEquals(1, cuenta.getCount().intValue());
		}
		documentosRecuperados = unIndexador.findTerm("chau").getAssociatedData();
		assertEquals(1, documentosRecuperados.size());
		for (KeyCount<OffsetAddress> cuenta: documentosRecuperados) {
			assertEquals(1, cuenta.getCount().intValue());
		}
		unIndexador.close();
		
		unIndexador = new SimpleSessionIndexer<OffsetAddress>(fileName, new OffsetAddressSerializer());
		OffsetAddress idDocumento2 = new OffsetAddress(55L);
		unIndexador.startSession();
		unIndexador.addTerms(idDocumento2, "hola", "hola");
		unIndexador.addTerms(idDocumento2, "otra palabra");
		unIndexador.addTerms(idDocumento2, "otra palabra", "y otra mas");
		unIndexador.endSession();

		assertNull(unIndexador.findTerm("frankfurt"));
		documentosRecuperados = unIndexador.findTerm("hola").getAssociatedData();
		assertEquals(2, documentosRecuperados.size());
		ArrayList<KeyCount<OffsetAddress>> docs = new ArrayList<KeyCount<OffsetAddress>>(documentosRecuperados);
		assertEquals(idDocumento, docs.get(0).getKey());
		assertEquals(3, docs.get(0).getCount().intValue());
		assertEquals(idDocumento2, docs.get(1).getKey());
		assertEquals(2, docs.get(1).getCount().intValue());
		documentosRecuperados = unIndexador.findTerm("otra palabra").getAssociatedData();
		assertEquals(1, documentosRecuperados.size());
		for (KeyCount<OffsetAddress> cuenta: documentosRecuperados) {
			assertEquals(2, cuenta.getCount().intValue());
		}
		unIndexador.close();
	}
}
