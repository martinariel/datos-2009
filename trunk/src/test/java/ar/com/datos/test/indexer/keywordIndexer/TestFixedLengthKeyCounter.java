package ar.com.datos.test.indexer.keywordIndexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;
import ar.com.datos.file.BlockFile;
import ar.com.datos.indexer.keywordIndexer.FixedLengthKeyCounter;
import ar.com.datos.indexer.keywordIndexer.KeyCount;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.test.file.variableLength.BlockFileStub;

public class TestFixedLengthKeyCounter extends TestCase {
	public void testSessionConPocasPalabras() throws Exception {
		final Map<Integer, List<BlockFileStub>> archivosCreados = new HashMap<Integer, List<BlockFileStub>>();
		Map<Long, Integer> resultadosEsperados = new HashMap<Long, Integer>();
		
		FixedLengthKeyCounter<Long> contador = new FixedLengthKeyCounter<Long>() {
			@Override
			public BlockFile constructFile(Integer blockSize) {
				if (!archivosCreados.containsKey(blockSize)) archivosCreados.put(blockSize, new ArrayList<BlockFileStub>());
				archivosCreados.get(blockSize).add(new BlockFileStub(blockSize));
				return archivosCreados.get(blockSize).get(archivosCreados.get(blockSize).size() - 1);
			}
		};
		contador.setSerializer(new LongSerializer());
		contador.startSession();
		contador.countKey(2L);
		contador.countKey(1L);
		contador.countKey(2L);
		contador.countKey(23L);
		contador.countKey(2L);
		resultadosEsperados.put(1L, 1);
		resultadosEsperados.put(2L, 3);
		resultadosEsperados.put(23L, 1);
		contador.endSession();
		Iterator<KeyCount<Long>> iterador = contador.iterator();
		Integer cantidadDeResultados = 0;
		while (iterador.hasNext()) {
			cantidadDeResultados ++;
			KeyCount<Long> current = iterador.next();
			assertTrue(resultadosEsperados.containsKey(current.getKey()));
			assertEquals(resultadosEsperados.get(current.getKey()), current.getCount());
		}
		assertEquals(resultadosEsperados.keySet().size(), cantidadDeResultados.intValue());
	}
	public void testSessionConMuchasPalabras() throws Exception {
		final Map<Integer, List<BlockFileStub>> archivosCreados = new HashMap<Integer, List<BlockFileStub>>();
		Map<Long, Integer> resultadosEsperados = new HashMap<Long, Integer>();
		
		FixedLengthKeyCounter<Long> contador = new FixedLengthKeyCounter<Long>() {
			@Override
			public BlockFile constructFile(Integer blockSize) {
				if (!archivosCreados.containsKey(blockSize)) archivosCreados.put(blockSize, new ArrayList<BlockFileStub>());
				archivosCreados.get(blockSize).add(new BlockFileStub(blockSize));
				return archivosCreados.get(blockSize).get(archivosCreados.get(blockSize).size() - 1);
			}
		};
		contador.setSerializer(new LongSerializer());
		contador.startSession();
		Random r = new Random();
		for (Integer i = 0; i < 934; i++) {
			Long random = r.nextLong() % 512;
			contarEnMapa(resultadosEsperados, random);
			contador.countKey(random);
		}
		contador.endSession();
		Iterator<KeyCount<Long>> iterador = contador.iterator();
		Integer cantidadDeResultados = 0;
		while (iterador.hasNext()) {
			cantidadDeResultados ++;
			KeyCount<Long> current = iterador.next();
			assertTrue(resultadosEsperados.containsKey(current.getKey()));
			assertEquals(resultadosEsperados.get(current.getKey()), current.getCount());
		}
		assertEquals(resultadosEsperados.keySet().size(), cantidadDeResultados.intValue());
	}
	private void contarEnMapa(Map<Long, Integer> resultadosEsperados, Long random) {
		Integer value = resultadosEsperados.containsKey(random)? resultadosEsperados.get(random) : 0;
		resultadosEsperados.put(random, value + 1);
	}
}
