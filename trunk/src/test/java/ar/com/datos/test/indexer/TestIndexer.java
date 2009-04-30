package ar.com.datos.test.indexer;

import java.io.File;
import java.io.FileFilter;
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
		unIndexador.close();
	}
}
