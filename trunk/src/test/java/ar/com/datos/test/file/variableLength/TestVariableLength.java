package ar.com.datos.test.file.variableLength;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.lib.action.CustomAction;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.VariableLengthAddress;
import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.serializer.Serializable;
import ar.com.datos.serializer.Serializer;
/**
 * Pruebas con archivo inicial vacÃ­o
 * @author Juan Manuel Barreneche
 *
 */
public class TestVariableLength extends MockObjectTestCase {
	private Integer blockSize;
	// Mock de serialización de objetos
	@SuppressWarnings("unchecked")
	private Serializer serializerMock;
	// Mock de archivo
	private BlockFile fileMock;
	// Verificador que el archivo sea creado una sola vez
	private Integer cantidadDeVecesCreado;
	// Propiedad que utiliza el mock de archivo para indicar cuantos bloques hay
	private Long cantidadDeBloquesEnFileMock
	;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		serializerMock = this.mock(Serializer.class);
		fileMock = this.mock(BlockFile.class);
		cantidadDeVecesCreado = 0;
		blockSize = 512;
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
	@SuppressWarnings("unchecked")
	public void testCreacion() throws Exception {
		this.cantidadDeBloquesEnFileMock = 0L;
		final MiLinkedList<Object> campos1 = new MiLinkedList<Object>();
		campos1.add(1);
		final MiLinkedList<Object> campos2 = new MiLinkedList<Object>();
		campos2.add(2);
		checking(new Expectations(){{
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos1));
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos2));
			allowing(fileMock).writeBlock(with(0L), with(any(Collection.class)));
		}});
		VariableLengthFileManager unDynamicAccesor = crearArchivo();
		assertFalse(unDynamicAccesor.iterator().hasNext());
		BlockAddress<Long, Short> direccion1 = unDynamicAccesor.addEntity(campos1);
		assertEquals(0, direccion1.getBlockNumber().intValue());
		assertEquals(0, direccion1.getObjectNumber().intValue());
		BlockAddress<Long, Short> direccion2 = unDynamicAccesor.addEntity(campos2);
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
	@SuppressWarnings("unchecked")
	public void testAgregadoAUnBloqueExistenteEnElArchivo() throws Exception {
		this.cantidadDeBloquesEnFileMock = 2L;
		final MiLinkedList<Object> campos = new MiLinkedList<Object>();
		final byte[] bloque = new byte[blockSize];
		Short cantidadDeObjetos = 5;
		setCantidadDeObjetos(bloque, cantidadDeObjetos);
		checking(new Expectations(){{
			one(fileMock).readBlock(cantidadDeBloquesEnFileMock - 1);
			will(returnValue(bloque));
			allowing(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos));
			allowing(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos));
		}});
		VariableLengthFileManager unDynamicAccesor = crearArchivo();
		BlockAddress<Long, Short> direccion = unDynamicAccesor.addEntity(campos);
		assertEquals(this.cantidadDeBloquesEnFileMock - 1L, direccion.getBlockNumber().longValue());
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
	@SuppressWarnings("unchecked")
	public void testAgregadoAUnBloqueNuevoConArchivoNoVacio() throws Exception {
		this.cantidadDeBloquesEnFileMock = 2L;
		final MiLinkedList<Object> campos = new MiLinkedList<Object>();
		final byte[] bloque = new byte[blockSize];
		Short cantidadDeObjetos = 0;
		setCantidadDeObjetos(bloque, cantidadDeObjetos);
		checking(new Expectations(){{
			one(fileMock).readBlock(cantidadDeBloquesEnFileMock - 1);
			will(returnValue(bloque));
			// Deserialización del registro que agrego 
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos));
			allowing(fileMock).writeBlock(with(cantidadDeBloquesEnFileMock), with(any(Collection.class)));
		}});
		VariableLengthFileManager unDynamicAccesor = crearArchivo();
		BlockAddress<Long, Short> direccion = unDynamicAccesor.addEntity(campos);
		assertEquals(cantidadDeBloquesEnFileMock, direccion.getBlockNumber());
		assertEquals(cantidadDeObjetos.shortValue(), direccion.getObjectNumber().shortValue());
	}
	/**
	 * Voy a pedirle que hidrate dos objetos que no se encuentra en el último bloque
	 * y que su número de objeto son 0 y 1 (es decir son los dos primeros de dicho bloque)
	 * En el bloque existe únicamente dichos registro
	 * Espero que lea el bloque correspondiente a ambos registros
	 * Que hidrate ambos y que me devuelva el segundo.
	 * Cuando, después, le pido el primero espero que no acceda al archivo pero si que me devuelva 
	 * el primero hidratado (que hidrató anteriormente)  
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testLecturaAntesDelUltimoBloque() throws Exception {
		this.cantidadDeBloquesEnFileMock = 3L;
		final MiLinkedList<Object> campos1 = new MiLinkedList<Object>();
		campos1.add(1);
		final MiLinkedList<Object> campos2 = new MiLinkedList<Object>();
		campos2.add(2);
		final byte[] bloqueFinal = new byte[blockSize];
		final byte[] bloqueDatos = new byte[blockSize];
		Short cantidadDeObjetos = 0;
		setCantidadDeObjetos(bloqueFinal, cantidadDeObjetos);
		final Long numeroDeBloqueBuscado = 1L;
		cantidadDeObjetos = 2;
		setCantidadDeObjetos(bloqueDatos, cantidadDeObjetos);
		checking(new Expectations(){{
			one(fileMock).readBlock(numeroDeBloqueBuscado);
			will(returnValue(bloqueDatos));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos1));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos2));
			allowing(fileMock).readBlock(2L);
			will(returnValue(bloqueFinal));
		}});
		DynamicAccesor unDynamicAccesor = crearArchivo();
		assertEquals(campos2, unDynamicAccesor.get(new VariableLengthAddress(numeroDeBloqueBuscado, (short)1)));
		assertEquals(campos1, unDynamicAccesor.get(new VariableLengthAddress(numeroDeBloqueBuscado, (short)0)));
	}
	/**
	 * Voy a pedirle que hidrate un objeto X que se encuentra en varios bloques
	 * por ende su número de objeto es -1 para la cabeza y 0 para el resto
	 * En el bloque existe únicamente dicho registro
	 * Luego espero que lea el numero de bloque 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testLecturaRegistroEnVariosBloques() throws Exception {
		this.cantidadDeBloquesEnFileMock = 3L;
		final MiLinkedList<Object> campos1 = new MiLinkedList<Object>();
		campos1.add(1);
		final byte[] bloqueDatos = new byte[blockSize];
		final byte[] bloqueFinal = new byte[blockSize];
		Short cantidadDeObjetos = 0;
		setCantidadDeObjetos(bloqueFinal, cantidadDeObjetos);
		setearSiguientePuntero(bloqueFinal,(byte)2);

		final Long numeroDeBloqueBuscado = 1L;
		cantidadDeObjetos = -1;
		setCantidadDeObjetos(bloqueDatos, cantidadDeObjetos);
		setearSiguientePuntero(bloqueDatos,(byte)2);
		final Long numeroDeSegundoBloque = 2L;
		checking(new Expectations(){{
			one(fileMock).readBlock(numeroDeBloqueBuscado);
			will(returnValue(bloqueDatos));
			one(fileMock).readBlock(numeroDeSegundoBloque);
			will(returnValue(bloqueFinal));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos1));
			allowing(fileMock).readBlock(2L);
			will(returnValue(bloqueFinal));
		}});
		VariableLengthFileManager unDynamicAccesor = crearArchivo();
		VariableLengthAddress address = new VariableLengthAddress(numeroDeBloqueBuscado, (short)0);
		assertEquals(campos1, unDynamicAccesor.get(address));
		assertEquals(2, unDynamicAccesor.getAmountOfBlocksFor(address).intValue());
	}
	/**
	 * Voy a pedirle el iterador. El archivo va a estar cargado con 4 bloques
	 * El bloque 0 con 2 registros. El 1 y 2 con un único registro. El último con un registro.
	 * Espero que las lecturas sean a medida que solicito y que no haya varias lecturas del mismo bloque.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testIteracion() throws Exception {
		this.cantidadDeBloquesEnFileMock = 4L;
		final byte[] bloque0 = new byte[blockSize];
		final byte[] bloque1 = new byte[blockSize];
		final byte[] bloque2 = new byte[blockSize];
		final byte[] bloque3 = new byte[blockSize];
		bloque0[blockSize-1] = 2;
		setCantidadDeObjetos(bloque1, (short)-1);
		bloque2[blockSize-1] = 0;
		bloque3[blockSize-1] = 1;
		// puntero al siguiente bloque
		setearSiguientePuntero(bloque1,(byte)2);
		setearSiguientePuntero(bloque2,(byte)2);
		final MiLinkedList<Object> campos0 = new MiLinkedList<Object>();
		campos0.add(0);
		final MiLinkedList<Object> campos1 = new MiLinkedList<Object>();
		campos1.add(1);
		final MiLinkedList<Object> campos2 = new MiLinkedList<Object>();
		campos2.add(2);
		final MiLinkedList<Object> campos3 = new MiLinkedList<Object>();
		campos3.add(3);
		DynamicAccesor unDynamicAccesor = crearArchivo();
		Iterator<MiLinkedList<Object>> iterador = unDynamicAccesor.iterator();
		checking(new Expectations(){{
			// Carga del bloque final. Esta es la única lectura que no se hace en orden
			one(fileMock).readBlock(cantidadDeBloquesEnFileMock - 1);
			will(returnValue(bloque3));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos3));
			one(fileMock).readBlock(0L);
			will(returnValue(bloque0));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos0));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos1));
			atLeast(1).of(fileMock).readBlock(1L);
			will(returnValue(bloque1));
			allowing(serializerMock).dehydrate(with(any(OutputBuffer.class)),with(campos3));
			one(fileMock).readBlock(2L);
			will(returnValue(bloque2));
			one(serializerMock).hydrate(with(any(InputBuffer.class)));
			will(returnValue(campos2));
			one(fileMock).readBlock(3L);
			will(returnValue(bloque3));
		}});
		assertTrue(iterador.hasNext());
		assertEquals(campos0, iterador.next());
		assertTrue(iterador.hasNext());
		assertEquals(campos1, iterador.next());
		assertTrue(iterador.hasNext());
		assertEquals(campos2, iterador.next());
		assertTrue(iterador.hasNext());
		assertEquals(campos3, iterador.next());
		assertFalse(iterador.hasNext());
	}
	/**
	 * 
	 * Voy a agregar 2 entidades al archivo estando vacio. La primera de tamaÃ±o menor al tamaÃ±o
	 * permitido por el buffer y la segunda de tamaÃ±o mayor.
	 * Esto debería generar 3 escrituras a disco. La primera escritura con los datos de deserializar
	 * el primer registro y con cantidad de registros igual a uno.
	 * La segunda con la primera parte de la serialización, el numero de bloque del siguiente bloque y 
	 * con la marca de que hay cero registros.
	 * La tercera y última, con lo restante de la serializacion, el número de bloque del mismo bloque (por ser el último)
	 * y la marca de que hay cero registros.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testVaciadoDeOutputBuffer() throws Exception {
		final byte[] serializacion1 = new byte[blockSize - 2];
		for (Integer i = 0; i < serializacion1.length; i++) 
			serializacion1[i] = 1; 
		byte[] cantidadRegistrosB1 = new byte[] { (byte)0, (byte)1 };
		final Collection<ArrayByte> bloque1 = new ArrayList<ArrayByte>();
		bloque1.add(new SimpleArrayByte(serializacion1));
		bloque1.add(new SimpleArrayByte(cantidadRegistrosB1));
		
		final byte[] serializacion2 = new byte[2 *(blockSize - 10)];
		byte[] datosBloque2 = new byte[blockSize - 10];
		byte[] datosBloque3 = new byte[blockSize - 10];
		for (Integer i = 0; i < serializacion2.length / 2; i++) {
			serializacion2[i] = 2; 
			datosBloque2[i] = 2;
		}
		for (Integer i = serializacion2.length / 2; i < serializacion2.length; i++) {
			serializacion2[i] = 3; 
			datosBloque3[i-datosBloque2.length] = 3;
		}
		
		byte[] siguienteRegistroB2 = new byte[] { 0, 0, 0, 0, 0, 0, 0, 2};
		byte[] cantidadRegistrosB2 = new byte[] { -1,-1};
		final Collection<ArrayByte> bloque2 = new ArrayList<ArrayByte>();
		bloque2.add(new SimpleArrayByte(datosBloque2));
		bloque2.add(new SimpleArrayByte(siguienteRegistroB2));
		bloque2.add(new SimpleArrayByte(cantidadRegistrosB2));

		byte[] siguienteRegistroB3 = new byte[] { (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)2};
		byte[] cantidadRegistrosB3 = new byte[] { (byte)0, (byte)0 };
		final Collection<ArrayByte> bloque3 = new ArrayList<ArrayByte>();
		bloque3.add(new SimpleArrayByte(datosBloque3));
		bloque3.add(new SimpleArrayByte(siguienteRegistroB3));
		bloque3.add(new SimpleArrayByte(cantidadRegistrosB3));

		final MiLinkedList<Object> campos = new MiLinkedList<Object>();
		campos.add(0);
		checking(new Expectations(){{
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos));
			will(new CustomAction("deshidratar") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					OutputBuffer outputBuffer = (OutputBuffer) invocation.getParameter(0);
					outputBuffer.write(serializacion1);
					return null;
				}
			});
			one(fileMock).writeBlock(with(0L), with(equal(bloque1)));
			will(new CustomAction("writeBlock0") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					cantidadDeBloquesEnFileMock += 1;
					return null;
				}
			});
			// Graba dos veces el mismo bloque porque hace un flush para asegurarse la posición del registro agregado
			one(fileMock).writeBlock(with(0L), with(equal(bloque1)));
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos));
			will(new CustomAction("deshidratar") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					OutputBuffer outputBuffer = (OutputBuffer) invocation.getParameter(0);
					outputBuffer.write(serializacion2);
					return null;
				}
			});
			one(fileMock).writeBlock(with(1L), with(equal(bloque2)));
			will(new CustomAction("writeBlock1") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					cantidadDeBloquesEnFileMock += 1;
					return null;
				}
			});
			one(fileMock).writeBlock(with(2L), with(equal(bloque3)));
			will(new CustomAction("writeBlock2") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					cantidadDeBloquesEnFileMock += 1;
					return null;
				}
			});
		}});
		this.cantidadDeBloquesEnFileMock = 0L;
		VariableLengthFileManager unDynamicAccesor = crearArchivo();
		BlockAddress direccionr1 = unDynamicAccesor.addEntity(campos);
		BlockAddress direccionr2 = unDynamicAccesor.addEntity(campos);
		assertEquals(0L, direccionr1.getBlockNumber());
		assertEquals(0, direccionr1.getObjectNumber().intValue());
		assertEquals(1L, direccionr2.getBlockNumber());
		assertEquals(0, direccionr2.getObjectNumber().intValue());
		
	}
	/**
	 * Voy a crear el VLFM con el archivo vacío. Luego voy a cerrarlo y verificar
	 * que no grabe (ya que no tiene registros en el último caché)
	 * Luego, voy a agregar uno y cerrarlo nuevamente. Esta vez si tiene que grabar 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testCerrado() throws Exception {
		this.cantidadDeBloquesEnFileMock = 0L;
		final byte[] serializacion = new byte[blockSize / 2];
		for (Integer i = 0; i < serializacion.length; i++) {
			serializacion[i] = 2;
		}
		final byte[] relleno = new byte[blockSize - serializacion.length - 2];
		final byte[] cantRegistros = new byte[] {(byte)0, (byte)1};
		final MiLinkedList<Object> campos = new MiLinkedList<Object>();
		campos.add(1);
		final Collection<ArrayByte> bloque = new ArrayList<ArrayByte>();
		bloque.add(new SimpleArrayByte(serializacion));
		bloque.add(new SimpleArrayByte(relleno));
		bloque.add(new SimpleArrayByte(cantRegistros));
		checking(new Expectations(){{
			one(serializerMock).dehydrate(with(any(OutputBuffer.class)), with(campos));
			will(new CustomAction("deshidratar") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					OutputBuffer outputBuffer = (OutputBuffer) invocation.getParameter(0);
					outputBuffer.write(serializacion);
					return null;
				}
			});
		}});
		VariableLengthFileManager unVLFM = crearArchivo();
		unVLFM.close();
		checking(new Expectations(){{
			atLeast(1).of(fileMock).writeBlock(with(0L), with(equal(bloque)));
			will(new CustomAction("writeBlock") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					cantidadDeBloquesEnFileMock += 1;
					return null;
				}
			});
		}});
		unVLFM.addEntity(campos);
		unVLFM.close();
	}
	private void setCantidadDeObjetos(final byte[] bloque, Short cantidadDeObjetos) {
		bloque[blockSize-1] = cantidadDeObjetos.byteValue();
		bloque[blockSize-2] = (byte)((cantidadDeObjetos.intValue() >> 8) & 0xFF);
	}
	private void setearSiguientePuntero(final byte[] bloque1, Byte siguiente) {
		bloque1[blockSize-3] = siguiente;
		bloque1[blockSize-4] = 0;
		bloque1[blockSize-5] = 0;
		bloque1[blockSize-6] = 0;
		bloque1[blockSize-7] = 0;
		bloque1[blockSize-8] = 0;
		bloque1[blockSize-9] = 0;
		bloque1[blockSize-10] = 0;
	}
	@SuppressWarnings("unchecked")
	private VariableLengthFileManager crearArchivo() {
		checking(new Expectations(){{
			atLeast(1).of(fileMock).getTotalBlocks();
			will(new CustomAction("totalBlocks") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					return cantidadDeBloquesEnFileMock;
				}
			});
			allowing(fileMock).close();
			allowing(fileMock).getBlockSize();
			will(new CustomAction("blockSize") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					return blockSize;
				}
			});
		}});
		return new VariableLengthFileManager("nombreArchivo",blockSize, serializerMock) {
			@Override
			public BlockFile constructFile(String nombreArchivo, Integer blockSize) {
				cantidadDeVecesCreado++;
				return fileMock;
			}
		};
	}
	public class MiLinkedList<T> extends LinkedList<T> implements Serializable<MiLinkedList<T>> {

		private static final long serialVersionUID = 1L;

		@Override
		public Serializer<MiLinkedList<T>> getSerializer() {
			return null;
		}
		
	}
}
