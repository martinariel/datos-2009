package ar.com.datos.test.indexer.keywordIndexer;

import java.util.Collection;

import junit.framework.TestCase;
import ar.com.datos.file.BlockFile;
import ar.com.datos.indexer.keywordIndexer.ExternalSorter;
import ar.com.datos.test.file.variableLength.BlockFileStub;

public class TestExternalSort extends TestCase {
	public void testSessionConPalabrasDeUnUnicoDocumento() throws Exception {
		byte[] bloqueConUno = new byte[]  {0,0,0,0,0,0,0,1};
		byte[] bloqueConUnos = new byte[] {1,1,1,1,1,1,1,1};
		byte[] bloqueConDos = new byte[]  {0,0,0,0,0,0,0,2};
		byte[] bloqueConTres = new byte[]  {0,0,0,0,0,0,0,3};
		byte[] bloqueConCuatro = new byte[]  {0,0,0,0,0,0,0,4};
		byte[] bloqueConCinco = new byte[]  {0,0,0,0,0,0,0,5};
		byte[] bloqueConCincos = new byte[]  {5,5,5,5,5,5,5,5};
		BlockFileStub file = new BlockFileStub(8);
		// Primer quinto
		file.appendBlock(bloqueConCinco);
		file.appendBlock(bloqueConCuatro);
		file.appendBlock(bloqueConTres);
		file.appendBlock(bloqueConDos);
		file.appendBlock(bloqueConUno);
		// Segundo quinto
		file.appendBlock(bloqueConCincos);
		file.appendBlock(bloqueConUnos);
		file.appendBlock(bloqueConCinco);
		file.appendBlock(bloqueConCincos);
		Integer ammountOfBlocks = 5;
		ExternalSorter es = new ExternalSorter(file, ammountOfBlocks) {
			@Override
			protected BlockFile constructTempFile() {
				return new BlockFileStub(8);
			}
		};
		Collection<BlockFile> semiPartes = es.getSortedChunks();
		assertEquals(2, semiPartes.size());
		
	}
}
