package ar.com.datos.test.file.variableLength;

import java.util.Iterator;
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
import ar.com.datos.file.variableLength.VariableLengthAddress;
import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.serializer.Serializer;
/**
 * Pruebas con archivo inicial vacío
 * @author Juan Manuel Barreneche
 *
 */
public class TestVariableLength extends MockObjectTestCase {
	private Integer blockSize = 512;
	private Serializer serializerMock;
	private BlockFile fileMock;
	private Integer cantidadDeVecesCreado;
	private OutputBuffer bufferMock;
	private Long cantidadDeBloquesInicial;
	private Short cantidadDeObjetosEnOutputBuffer;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		serializerMock = this.mock(Serializer.class);
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
	 * Verifico también que pueda recuperar dicho objeto 
	 * @throws Exception
	 */
	public void testCreacion() throws Exception {
		this.cantidadDeBloquesInicial = 0L;
		final Queue<Object> campos1 = new LinkedList<Object>();
		campos1.add(1);
		final Queue<Object> campos2 = new LinkedList<Object>();
		campos2.add(2);
		checking(new Expectations(){{
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos1));
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos2));
		}});
		DynamicAccesor unDynamicAccesor = crearArchivo();
		Address<Long, Short> direccion1 = unDynamicAccesor.addEntity(campos1);
		assertEquals(0, direccion1.getBlockNumber().intValue());
		assertEquals(0, direccion1.getObjectNumber().intValue());
		Address<Long, Short> direccion2 = unDynamicAccesor.addEntity(campos2);
		assertEquals(0, direccion2.getBlockNumber().intValue());
		assertEquals(1, direccion2.getObjectNumber().intValue());
		assertEquals(campos2, unDynamicAccesor.get(direccion2));
		assertEquals(campos1, unDynamicAccesor.get(direccion1));
	}
	/**
	 * Dado un archivo que tiene el último bloque con varios registros, espero que cargue el buffer
	 * con esos datos y que me permita agregar otro registro. La direccion del nuevo
	 * registro tiene que ser la dirección del último bloque (cantidad de bloques -1)
	 * y el número de objeto del objeto agregado tiene que ser la cantidad de objetos que había 
	 * inicialmente en dicho bloque 
	 * Verifico también que pueda recuperar dicho objeto 
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
		assertEquals(campos, unDynamicAccesor.get(direccion));
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
			// Deserialización del registro que agrego 
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos));
		}});
		DynamicAccesor unDynamicAccesor = crearArchivo();
		Address<Long, Short> direccion = unDynamicAccesor.addEntity(campos);
		assertEquals(cantidadDeBloquesInicial, direccion.getBlockNumber());
		assertEquals(cantidadDeObjetos.shortValue(), direccion.getObjectNumber().shortValue());
	}
	/**
	 * Voy a pedirle que hidrate dos objetos que no se encuentra en el último bloque
	 * y que su número de objeto son 0 y 1 (es decir son los dos primeros de dicho bloque)
	 * En el bloque existe únicamente dichos registro
	 * Espero que, primero le pida el último bloque (porque tiene que prepararse para recibir datos)
	 * Luego espero que lea el bloque correspondiente a ambos registros
	 * Que hidrate ambos y que me devuelva el segundo.
	 * Cuando, después, le pido el primero espero que no acceda al archivo pero si que me devuelva 
	 * el primero hidratado (que hidrató anteriormente)  
	 * @throws Exception
	 */
	public void testLecturaAntesDelUltimoBloque() throws Exception {
		this.cantidadDeBloquesInicial = 3L;
		final Queue<Object> campos1 = new LinkedList<Object>();
		campos1.add(1);
		final Queue<Object> campos2 = new LinkedList<Object>();
		campos2.add(2);
		final byte[] bloqueFinal = new byte[blockSize];
		final byte[] bloqueDatos = new byte[blockSize];
		Byte cantidadDeObjetos = 0;
		bloqueFinal[blockSize-1] = cantidadDeObjetos;
		final Long numeroDeBloqueBuscado = 0L;
		cantidadDeObjetos = 2;
		bloqueDatos[blockSize-1] = cantidadDeObjetos;
		checking(new Expectations(){{
			one(fileMock).readBlock(cantidadDeBloquesInicial - 1);
			will(returnValue(bloqueFinal));
			one(fileMock).readBlock(numeroDeBloqueBuscado);
			will(returnValue(bloqueDatos));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos1));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos2));
		}});
		DynamicAccesor unDynamicAccesor = crearArchivo();
		assertEquals(campos2, unDynamicAccesor.get(new VariableLengthAddress(numeroDeBloqueBuscado, (short)1)));
		assertEquals(campos1, unDynamicAccesor.get(new VariableLengthAddress(numeroDeBloqueBuscado, (short)0)));
	}
	/**
	 * Voy a pedirle que hidrate un objeto X que se encuentra en varios bloques
	 * por ende su número de objeto es 0
	 * En el bloque existe únicamente dicho registro
	 * Espero que, primero le pida el último bloque (porque  
	 * @throws Exception
	 */
	public void testLecturaRegistroEnVariosBloques() throws Exception {
		this.cantidadDeBloquesInicial = 3L;
		final Queue<Object> campos1 = new LinkedList<Object>();
		campos1.add(1);
		final byte[] bloqueFinal = new byte[blockSize];
		Byte cantidadDeObjetos = 0;
		bloqueFinal[blockSize-1] = cantidadDeObjetos;
		// Está serializado a mano, esperemos no haberle pifiado
		bloqueFinal[blockSize-2] = 2;
		bloqueFinal[blockSize-3] = 0;
		bloqueFinal[blockSize-4] = 0;
		bloqueFinal[blockSize-5] = 0;
		bloqueFinal[blockSize-6] = 0;
		bloqueFinal[blockSize-7] = 0;
		bloqueFinal[blockSize-8] = 0;
		bloqueFinal[blockSize-9] = 0;
		final byte[] bloqueDatos = new byte[blockSize];
		cantidadDeObjetos = 0;
		bloqueFinal[blockSize-1] = cantidadDeObjetos;
		final Long numeroDeBloqueBuscado = 1L;
		cantidadDeObjetos = 0;
		bloqueDatos[blockSize-1] = cantidadDeObjetos;
		// Está serializado a mano, esperemos no haberle pifiado
		bloqueDatos[blockSize-2] = 2;
		bloqueDatos[blockSize-3] = 0;
		bloqueDatos[blockSize-4] = 0;
		bloqueDatos[blockSize-5] = 0;
		bloqueDatos[blockSize-6] = 0;
		bloqueDatos[blockSize-7] = 0;
		bloqueDatos[blockSize-8] = 0;
		bloqueDatos[blockSize-9] = 0;
		final Long numeroDeSegundoBloque = 2L;
		checking(new Expectations(){{
			one(fileMock).readBlock(cantidadDeBloquesInicial - 1);
			will(returnValue(bloqueFinal));
			one(fileMock).readBlock(numeroDeBloqueBuscado);
			will(returnValue(bloqueDatos));
			one(fileMock).readBlock(numeroDeSegundoBloque);
			will(returnValue(bloqueFinal));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos1));
		}});
		DynamicAccesor unDynamicAccesor = crearArchivo();
		assertEquals(campos1, unDynamicAccesor.get(new VariableLengthAddress(numeroDeBloqueBuscado, (short)0)));
	}
	/**
	 * Voy a pedirle el iterador. El archivo va a estar cargado con 4 bloques
	 * El bloque 0 con 2 registros. El 1 y 2 con un único registro. El último con un registro.
	 * Espero que las lecturas sean a medida que solicito y que no haya varias lecturas del mismo bloque.
	 * @throws Exception
	 */
	public void testIteracion() throws Exception {
		this.cantidadDeBloquesInicial = 4L;
		final byte[] bloque0 = new byte[blockSize];
		final byte[] bloque1 = new byte[blockSize];
		final byte[] bloque2 = new byte[blockSize];
		final byte[] bloque3 = new byte[blockSize];
		bloque0[blockSize-1] = 2;
		bloque1[blockSize-1] = 0;
		bloque2[blockSize-1] = 0;
		bloque3[blockSize-1] = 1;
		// puntero al siguiente bloque
		bloque1[blockSize-2] = 2;
		bloque1[blockSize-3] = 0;
		bloque1[blockSize-4] = 0;
		bloque1[blockSize-5] = 0;
		bloque1[blockSize-6] = 0;
		bloque1[blockSize-7] = 0;
		bloque1[blockSize-8] = 0;
		bloque1[blockSize-9] = 0;
		// puntero al mismo bloque
		bloque2[blockSize-2] = 2;
		bloque2[blockSize-3] = 0;
		bloque2[blockSize-4] = 0;
		bloque2[blockSize-5] = 0;
		bloque2[blockSize-6] = 0;
		bloque2[blockSize-7] = 0;
		bloque2[blockSize-8] = 0;
		bloque2[blockSize-9] = 0;
		final Queue<Object> campos0 = new LinkedList<Object>();
		campos0.add(0);
		final Queue<Object> campos1 = new LinkedList<Object>();
		campos1.add(1);
		final Queue<Object> campos2 = new LinkedList<Object>();
		campos2.add(2);
		final Queue<Object> campos3 = new LinkedList<Object>();
		campos3.add(3);
		checking(new Expectations(){{
			// Carga del bloque final. Esta es la única lectura que no se hace en orden
			one(fileMock).readBlock(cantidadDeBloquesInicial - 1);
			will(returnValue(bloque3));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos3));
			allowing(serializerMock).dehydrate(with(any(OutputBuffer.class)),with(campos3));
		}});
		DynamicAccesor unDynamicAccesor = crearArchivo();
		Iterator<Queue<Object>> iterador = unDynamicAccesor.iterator();
		checking(new Expectations(){{
			one(fileMock).readBlock(0L);
			will(returnValue(bloque0));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos0));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos1));
		}});
		assertTrue(iterador.hasNext());
		assertEquals(campos0, iterador.next());
		assertTrue(iterador.hasNext());
		assertEquals(campos1, iterador.next());
		assertTrue(iterador.hasNext());
		checking(new Expectations(){{
			one(fileMock).readBlock(1L);
			will(returnValue(bloque1));
			one(fileMock).readBlock(2L);
			will(returnValue(bloque2));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos2));
		}});
		assertEquals(campos2, iterador.next());
		assertTrue(iterador.hasNext());
		assertEquals(campos3, iterador.next());
		assertFalse(iterador.hasNext());
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
