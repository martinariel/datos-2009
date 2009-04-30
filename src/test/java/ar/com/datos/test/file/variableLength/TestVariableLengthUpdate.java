package ar.com.datos.test.file.variableLength;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.lib.action.CustomAction;

import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.HydratedBlock;
import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.file.variableLength.VariableLengthWithCache;
import ar.com.datos.file.variableLength.address.VariableLengthAddress;
import ar.com.datos.serializer.NullableSerializer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.StringSerializerDelimiter;
/**
 * Pruebas con archivo inicial vacÃ­o
 * @author Juan Manuel Barreneche
 *
 */
public class TestVariableLengthUpdate extends MockObjectTestCase {
	private Integer blockSize;
	// Mock de serialización de objetos
	private NullableSerializer<String> serializerMock;
	// Mock de archivo
	private BlockFileStub fileStub;
	// Verificador que el archivo sea creado una sola vez
	private Integer cantidadDeVecesCreado;
	//Mantyenimiento de los bloques hidratados que espero recuperar en las lecturas
	private Map<Integer, HydratedBlock<String>> bloquesHidratados;
	private VariableLengthWithCache<String> dynamicAccessor;
	@Override
	@SuppressWarnings("unchecked")
	protected void setUp() throws Exception {
		super.setUp();
		serializerMock = this.mock(NullableSerializer.class);
		cantidadDeVecesCreado = 0;
		blockSize = 512;
		bloquesHidratados = new HashMap<Integer, HydratedBlock<String>>();
	}
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		assertEquals(1, cantidadDeVecesCreado.intValue());
	}
	/**
	 * Voy a actualizar un objeto que se encuentra en el bloque 2 (tercer bloque), en dicho bloque hay
	 * otros objetos, pero al deshidratar no hay problema de tamaño
	 * Espero una única grabación del archivo 
	 * @throws Exception
	 */
	public void testActualizacionSimple() throws Exception {
		dynamicAccessor = crearArchivo(serializerMock); 
		final Integer cuartosDeBloque = blockSize / 4 - 50;
		configuracionVariosEnUnBloque();
		checking(new Expectations(){{
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto cero"));
			will(consume(cuartosDeBloque));
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto uno modificado"));
			will(consume(cuartosDeBloque));
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto dos"));
			will(consume(cuartosDeBloque));
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto tres"));
			will(consume(cuartosDeBloque));
		}});
		dynamicAccessor.updateEntity(new VariableLengthAddress(2L,(short)1), "objeto uno modificado");
		assertWrittens(2L);
	}
	/**
	 * Voy a actualizar un objeto que se encuentra en el bloque 2 (tercer bloque), en dicho bloque hay
	 * otros objetos, pero al deshidratar hay problema de tamaño
	 * Espero dos grabaciones del archivo (una por la actualización del bloque que se le retira el objeto)
	 * y otra del nuevo bloque donde se almacena el objeto que estaba actualizando 
	 * @throws Exception
	 */
	public void testActualizacionQueExcedeTamanio() throws Exception {
		dynamicAccessor = crearArchivo(serializerMock); 
		final Integer cuartosDeBloque = blockSize / 4;
		configuracionVariosEnUnBloque();
		checking(new Expectations(){{
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto cero"));
			will(consume(cuartosDeBloque));
			// La primera vez que lo deshidrata
			exactly(2).of(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto uno modificado"));
			will(consume(cuartosDeBloque));
			// La primera vez que lo deshidrata
			one(serializerMock).dehydrateNull(with(any(OutputBuffer.class)));
			will(consume(1));
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto dos"));
			will(consume(cuartosDeBloque));
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto tres"));
			will(consume(cuartosDeBloque));
		}});
		dynamicAccessor.updateEntity(new VariableLengthAddress(2L,(short)1), "objeto uno modificado");
		assertWrittens(2L,5L);
	}
	/**
	 * Voy a actualizar un objeto que se encuentra en el bloque 2 (tercer bloque), en dicho bloque hay
	 * otros objetos pero el que voy a actualizar es el último.
	 * Al deshidratar hay problema de tamaño
	 * Espero dos grabaciones del archivo (una por la actualización del bloque que se le retira el objeto)
	 * y otra del nuevo bloque donde se almacena el objeto que estaba actualizando 
	 * No quiero que deje el null ya que es el último
	 * @throws Exception
	 */
	public void testActualizacionElUltimoYExcede() throws Exception {
		dynamicAccessor = crearArchivo(serializerMock); 
		final Integer cuartosDeBloque = blockSize / 4;
		configuracionVariosEnUnBloque();
		bloquesHidratados.get(2).getData().remove(1);
		bloquesHidratados.get(2).getData().add(1,"objeto uno");
		checking(new Expectations(){{
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto cero"));
			will(consume(cuartosDeBloque));
			// La primera vez que lo deshidrata
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto uno"));
			will(consume(cuartosDeBloque));
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto dos"));
			will(consume(cuartosDeBloque));
			exactly(2).of(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto tres modificado"));
			will(consume(cuartosDeBloque));
		}});
		dynamicAccessor.updateEntity(new VariableLengthAddress(2L,(short)3), "objeto tres modificado");
		assertWrittens(2L,5L);
	}
	/**
	 * Voy a actualizar un objeto que se encuentra en el bloque 2 solito y ocupando un único bloque
	 * Al deshidratar excede el tamaño del bloque, por lo cual espero que grabe en dos bloques
	 * @throws Exception
	 */
	public void testActualizacionQueExtiendeBloque() throws Exception {
		dynamicAccessor = crearArchivo(serializerMock); 
		final Integer masDeUnBloque = blockSize;
		fileStub.extendTo(5);
		List<String> datos = new ArrayList<String>(1);
		datos.add("objeto a modificar");
		bloquesHidratados.put(2, new HydratedBlock<String>(datos, 2L, 3L));
		checking(new Expectations(){{
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto modificado"));
			will(consume(masDeUnBloque));
		}});
		dynamicAccessor.updateEntity(new VariableLengthAddress(2L,(short)0), "objeto modificado");
		assertWrittens(2L,5L);
	}
	/**
	 * Voy a actualizar un objeto que se encuentra en el bloque 2 solito y ocupando un único bloque
	 * Al deshidratar excede el tamaño del bloque, por lo cual espero que grabe en dos bloques
	 * @throws Exception
	 */
	public void testActualizacionDeVariosBloquesQueExtiendeMasBloques() throws Exception {
		dynamicAccessor = crearArchivo(serializerMock); 
		final Integer masDeUnBloque = blockSize * 2; // En total, por la Metadata son 3 bloques
		fileStub.extendTo(5);
		List<String> datos = new ArrayList<String>(1);
		datos.add("objeto a modificar");
		List<Long> bloquesOriginales = new ArrayList<Long>(2);
		bloquesOriginales.add(2L);
		bloquesOriginales.add(3L);
		bloquesHidratados.put(2, new HydratedBlock<String>(datos, bloquesOriginales , 3L));
		checking(new Expectations(){{
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with("objeto modificado"));
			will(consume(masDeUnBloque));
		}});
		dynamicAccessor.updateEntity(new VariableLengthAddress(2L,(short)0), "objeto modificado");
		assertWrittens(2L,3L,5L);
	}
	/**
	 * BUGFIX 10e456
	 * El problema se produce cuando un agrego un registro, luego lo actualizo y luego agrego otro
	 * El que se está grabando en el último caso es los dos que agregué en lugar del que
	 * modifiqué y el que agregué último
	 * @throws Exception
	 */
	public void testAgregarActualizarAgregar() throws Exception {
		VariableLengthFileManager<String> otroAccessor = new VariableLengthFileManager<String>("nombreArchivo", blockSize, new StringSerializerDelimiter()) {
			@Override
			public BlockFile constructFile(String nombreArchivo, Integer blockSize) {
				cantidadDeVecesCreado++;
				fileStub = new BlockFileStub(blockSize);
				return fileStub;
			}
		};
		BlockAddress<Long, Short> address = otroAccessor.addEntity("objeto a modificar");
		otroAccessor.updateEntity(address, "objeto uno modificado");
		otroAccessor.addEntity("objeto uno modificado");
		assertEquals("objeto uno modificado", otroAccessor.get(address));
		// XXX debería escribir menos veces
		assertWrittens(0L,0L,0L,0L);
	}
	private void configuracionVariosEnUnBloque() {
		fileStub.extendTo(5);
		List<String> datos = new ArrayList<String>(4);
		datos.add("objeto cero");
		datos.add("objeto uno a modificar");
		datos.add("objeto dos");
		datos.add("objeto tres");
		bloquesHidratados.put(2, new HydratedBlock<String>(datos, 2L, 3L));
	}
	private Action consume(final Integer tamanioAConsumir) {
		return new CustomAction("consumo:" + tamanioAConsumir.toString()) {
		
			@Override
			public Object invoke(Invocation invocation) throws Throwable {
				OutputBuffer buffer = (OutputBuffer)invocation.getParameter(0);
				
				buffer.write(new byte[tamanioAConsumir]);
				return null;
			}
		
		};
	}
	private void assertWrittens(Long...l) {
		List<Long> writtenBlocks = fileStub.getWrittenBlocks();
		if (writtenBlocks.size() > l.length) fail("Se escribieron mas bloques de los esperados" + writtenBlocks.toString());
		if (writtenBlocks.size() < l.length) fail("Se escribieron menos bloques de los esperados" + writtenBlocks.toString());
		
		for (Integer i = 0; i < l.length; i++) {
			assertEquals(l[i], writtenBlocks.get(i));
		}
	}
	private VariableLengthWithCache<String> crearArchivo(Serializer<String> serializer) {
		return new VariableLengthWithCache<String>("nombreArchivo",blockSize, serializer) {
			@Override
			public BlockFile constructFile(String nombreArchivo, Integer blockSize) {
				cantidadDeVecesCreado++;
				fileStub = new BlockFileStub(blockSize);
				return fileStub;
			}
			@Override
			protected HydratedBlock<String> getBlock(Long blockNumber) {
				return bloquesHidratados.get(blockNumber.intValue()); 
			}
		};
	}
}
