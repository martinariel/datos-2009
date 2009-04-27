package ar.com.datos.test.indexer.keywordIndexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;
import ar.com.datos.file.BlockFile;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.test.file.variableLength.BlockFileStub;
import ar.com.datos.utils.sort.external.FixedLengthKeyCounter;
import ar.com.datos.utils.sort.external.KeyCount;

public class TestFixedLengthKeyCounter extends TestCase {
	public void testSessionConPocasPalabras() throws Exception {
		final Map<Integer, List<BlockFileStub>> archivosCreados = new HashMap<Integer, List<BlockFileStub>>();
		Map<Long, Integer> resultadosEsperados = new HashMap<Long, Integer>();
		
		FixedLengthKeyCounter<Long> contador = new FixedLengthKeyCounter<Long>(new LongSerializer()) {
			@Override
			public BlockFile constructFile(Integer blockSize) {
				if (!archivosCreados.containsKey(blockSize)) archivosCreados.put(blockSize, new ArrayList<BlockFileStub>());
				archivosCreados.get(blockSize).add(new BlockFileStub(blockSize));
				return archivosCreados.get(blockSize).get(archivosCreados.get(blockSize).size() - 1);
			}
		};
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
		
		FixedLengthKeyCounter<Long> contador = new FixedLengthKeyCounter<Long>(new LongSerializer()) {
			@Override
			public BlockFile constructFile(Integer blockSize) {
				if (!archivosCreados.containsKey(blockSize)) archivosCreados.put(blockSize, new ArrayList<BlockFileStub>());
				archivosCreados.get(blockSize).add(new BlockFileStub(blockSize));
				return archivosCreados.get(blockSize).get(archivosCreados.get(blockSize).size() - 1);
			}
		};
		contador.startSession();
		Random r = new Random();
		for (Integer i = 0; i < 1934; i++) {
			Long random = Math.abs(r.nextLong() % 1024);
			contarEnMapa(resultadosEsperados, random);
			contador.countKey(random);
		}
		contador.endSession();
		Iterator<KeyCount<Long>> iterador = contador.iterator();
		Integer cantidadDeResultados = 0;
		Long previousKey = Long.MIN_VALUE;
		while (iterador.hasNext()) {
			cantidadDeResultados ++;
			KeyCount<Long> current = iterador.next();
			assertTrue(previousKey + " < " + current.getKey(), previousKey < current.getKey());
			assertTrue(resultadosEsperados.containsKey(current.getKey()));
			assertEquals(resultadosEsperados.get(current.getKey()), current.getCount());
			previousKey = current.getKey();
		}
		assertEquals(resultadosEsperados.keySet().size(), cantidadDeResultados.intValue());
	}
	private void contarEnMapa(Map<Long, Integer> resultadosEsperados, Long random) {
		Integer value = resultadosEsperados.containsKey(random)? resultadosEsperados.get(random) : 0;
		resultadosEsperados.put(random, value + 1);
	}
}
