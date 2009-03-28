package ar.com.datos.test.file.variableLength;

import java.util.LinkedList;
import java.util.Queue;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.lib.action.CustomAction;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.file.Address;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.serializer.QueueSerializer;
/**
 * Pruebas con archivo inicial vacío
 * @author Juan Manuel Barreneche
 *
 */
public class TestVariableLength extends MockObjectTestCase {
	private Integer blockSize = 512;
	private QueueSerializer serializerMock;
	private BlockFile fileMock;
	private Integer cantidadDeVecesCreado;
	private OutputBuffer bufferMock;
	private Long cantidadDeBloquesInicial;
	private Short cantidadDeObjetosEnOutputBuffer;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		serializerMock = this.mock(QueueSerializer.class);
		fileMock = this.mock(BlockFile.class);
		bufferMock = this.mock(OutputBuffer.class);
		cantidadDeVecesCreado = 0;
		cantidadDeObjetosEnOutputBuffer = 0;
	}
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		assertEquals(1, cantidadDeVecesCreado.intValue());
	}
	/**
	 * Voy a agregar un primer registro con el archivo vacío, espero que 
	 * deshidrate cada objeto que le doy, que cierre la entidad en el buffer
	 * y que utilice la cantidad de entidades para el creado de la dirección
	 * @throws Exception
	 */
	public void testCreacion() throws Exception {
		this.cantidadDeBloquesInicial = 0L;
		final Queue<Object> campos = new LinkedList<Object>();
		checking(new Expectations(){{
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos));
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos));
		}});
		DynamicAccesor unDynamicAccesor = crearArchivo();
		Address<Long, Short> direccion = unDynamicAccesor.addEntity(campos);
		assertEquals(0, direccion.getBlockNumber().intValue());
		assertEquals(0, direccion.getObjectNumber().intValue());
		Address<Long, Short> direccion2 = unDynamicAccesor.addEntity(campos);
		assertEquals(0, direccion2.getBlockNumber().intValue());
		assertEquals(1, direccion2.getObjectNumber().intValue());
	}
	/**
	 * Dado un archivo que tiene el último bloque con varios registros, espero que cargue el buffer
	 * con esos datos y que me permita agregar otro registro. La direccion del nuevo
	 * registro tiene que ser la dirección del último bloque (cantidad de bloques -1)
	 * y el número de objeto del objeto agregado tiene que ser la cantidad de objetos que había 
	 * inicialmente en dicho bloque
	 * @throws Exception
	 */
	public void testAgregadoAUnBloqueExistenteEnElArchivo() throws Exception {
		this.cantidadDeBloquesInicial = 2L;
		final Queue<Object> campos = new LinkedList<Object>();
		final byte[] bloque = new byte[blockSize];
		Byte cantidadDeObjetos = 5;
		bloque[blockSize-1] = cantidadDeObjetos;
		checking(new Expectations(){{
			one(fileMock).readBlock(cantidadDeBloquesInicial - 1);
			will(returnValue(bloque));
			allowing(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos));
			allowing(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos));
		}});
		DynamicAccesor unDynamicAccesor = crearArchivo();
		Address<Long, Short> direccion = unDynamicAccesor.addEntity(campos);
		assertEquals(this.cantidadDeBloquesInicial - 1L, direccion.getBlockNumber().longValue());
		assertEquals(cantidadDeObjetos.shortValue(), direccion.getObjectNumber().shortValue());
	}
	/**
	 * Dado un archivo que su último bloque es la cola de un registro que ocupa varios bloques
	 * espero que cargue el buffer vacío y que me permita agregar otro registro. 
	 * La direccion del nuevo registro tiene que ser la cantidad de bloques.
	 * El número de objeto del objeto agregado tiene que ser 0, ya que es el primero
	 * @throws Exception
	 */
	public void testAgregadoAUnBloqueNuevoConArchivoNoVacio() throws Exception {
		this.cantidadDeBloquesInicial = 2L;
		final Queue<Object> campos = new LinkedList<Object>();
		final byte[] bloque = new byte[blockSize];
		Byte cantidadDeObjetos = 0;
		bloque[blockSize-1] = cantidadDeObjetos;
		checking(new Expectations(){{
			one(fileMock).readBlock(cantidadDeBloquesInicial - 1);
			will(returnValue(bloque));
			allowing(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos));
			allowing(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos));
		}});
		DynamicAccesor unDynamicAccesor = crearArchivo();
		Address<Long, Short> direccion = unDynamicAccesor.addEntity(campos);
		assertEquals(cantidadDeBloquesInicial, direccion.getBlockNumber());
		assertEquals(cantidadDeObjetos.shortValue(), direccion.getObjectNumber().shortValue());
	}
	private DynamicAccesor crearArchivo() {
		checking(new Expectations(){{
			atLeast(1).of(fileMock).getTotalBlocks();
			will(new CustomAction("totalBlocks") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					return cantidadDeBloquesInicial;
				}
			});
			allowing(bufferMock).closeEntity();
			will(new CustomAction("closeEntity") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					cantidadDeObjetosEnOutputBuffer += 1;
					return null;
				}
				
			});
			allowing(bufferMock).getEntitiesCount();
			will(new CustomAction("entitiesCount") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					return cantidadDeObjetosEnOutputBuffer;
				}
				
			});
		}});
		return new VariableLengthFileManager("nombreArchivo",blockSize, serializerMock) {
			@Override
			public BlockFile constructFile(String nombreArchivo, Integer blockSize) {
				cantidadDeVecesCreado++;
				return fileMock;
			}
			@Override
			public OutputBuffer getLastBlockBuffer() {
				return bufferMock;
			}
		};
	}
}
