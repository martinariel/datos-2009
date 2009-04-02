package ar.com.datos.test.file.variableLength;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.lib.action.CustomAction;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.buffer.variableLength.ArrayByte;
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
	private Integer blockSize;
	@SuppressWarnings("unchecked")
	private Serializer serializerMock;
	private BlockFile fileMock;
	private Integer cantidadDeVecesCreado;
	private Long cantidadDeBloquesInicial;
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
	@SuppressWarnings("unchecked")
	public void testAgregadoAUnBloqueExistenteEnElArchivo() throws Exception {
		this.cantidadDeBloquesInicial = 2L;
		final Queue<Object> campos = new LinkedList<Object>();
		final byte[] bloque = new byte[blockSize];
		Byte cantidadDeObjetos = 5;
		setCantidadDeObjetos(bloque, cantidadDeObjetos);
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
	@SuppressWarnings("unchecked")
	public void testAgregadoAUnBloqueNuevoConArchivoNoVacio() throws Exception {
		this.cantidadDeBloquesInicial = 2L;
		final Queue<Object> campos = new LinkedList<Object>();
		final byte[] bloque = new byte[blockSize];
		Byte cantidadDeObjetos = 0;
		setCantidadDeObjetos(bloque, cantidadDeObjetos);
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
	@SuppressWarnings("unchecked")
	public void testLecturaAntesDelUltimoBloque() throws Exception {
		this.cantidadDeBloquesInicial = 3L;
		final Queue<Object> campos1 = new LinkedList<Object>();
		campos1.add(1);
		final Queue<Object> campos2 = new LinkedList<Object>();
		campos2.add(2);
		final byte[] bloqueFinal = new byte[blockSize];
		final byte[] bloqueDatos = new byte[blockSize];
		Byte cantidadDeObjetos = 0;
		setCantidadDeObjetos(bloqueFinal, cantidadDeObjetos);
		final Long numeroDeBloqueBuscado = 0L;
		cantidadDeObjetos = 2;
		setCantidadDeObjetos(bloqueDatos, cantidadDeObjetos);
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
	 * Espero que, primero le pida el último bloque (porque de esa manera es como se inicia el archivo)
	 * Luego espero que lea el numero de bloque 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testLecturaRegistroEnVariosBloques() throws Exception {
		this.cantidadDeBloquesInicial = 3L;
		final Queue<Object> campos1 = new LinkedList<Object>();
		campos1.add(1);
		final byte[] bloqueDatos = new byte[blockSize];
		final byte[] bloqueFinal = new byte[blockSize];
		Byte cantidadDeObjetos = 0;
		setCantidadDeObjetos(bloqueFinal, cantidadDeObjetos);
		setearSiguientePuntero(bloqueFinal,(byte)2);

		cantidadDeObjetos = 0;
		setCantidadDeObjetos(bloqueFinal, cantidadDeObjetos);
		final Long numeroDeBloqueBuscado = 1L;
		cantidadDeObjetos = 0;
		setCantidadDeObjetos(bloqueDatos, cantidadDeObjetos);
		setearSiguientePuntero(bloqueDatos,(byte)2);
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
	@SuppressWarnings("unchecked")
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
		setearSiguientePuntero(bloque1,(byte)2);
		setearSiguientePuntero(bloque2,(byte)2);
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
	/**
	 * 
	 * Voy a agregar 2 entidades al archivo estando vacio. La primera de tamaño menor al tamaño
	 * permitido por el buffer y la segunda de tamaño mayor.
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
		bloque1.add(new ArrayByte(serializacion1));
		bloque1.add(new ArrayByte(cantidadRegistrosB1));
		
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
		
		byte[] siguienteRegistroB2 = new byte[] { (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)2};
		byte[] cantidadRegistrosB2 = new byte[] { (byte)0, (byte)0 };
		final Collection<ArrayByte> bloque2 = new ArrayList<ArrayByte>();
		bloque2.add(new ArrayByte(datosBloque2));
		bloque2.add(new ArrayByte(siguienteRegistroB2));
		bloque2.add(new ArrayByte(cantidadRegistrosB2));

		byte[] siguienteRegistroB3 = new byte[] { (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)2};
		byte[] cantidadRegistrosB3 = new byte[] { (byte)0, (byte)0 };
		final Collection<ArrayByte> bloque3 = new ArrayList<ArrayByte>();
		bloque3.add(new ArrayByte(datosBloque3));
		bloque3.add(new ArrayByte(siguienteRegistroB3));
		bloque3.add(new ArrayByte(cantidadRegistrosB3));

		final Queue<Object> campos = new LinkedList<Object>();
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
					cantidadDeBloquesInicial += 1;
					return null;
				}
			});
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
					cantidadDeBloquesInicial += 1;
					return null;
				}
			});
			one(fileMock).writeBlock(with(2L), with(equal(bloque3)));
			will(new CustomAction("writeBlock2") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					cantidadDeBloquesInicial += 1;
					return null;
				}
			});
		}});
		this.cantidadDeBloquesInicial = 0L;
		DynamicAccesor unDynamicAccesor = crearArchivo();
		Address direccionr1 = unDynamicAccesor.addEntity(campos);
		Address direccionr2 = unDynamicAccesor.addEntity(campos);
		assertEquals(0L, direccionr1.getBlockNumber());
		assertEquals(0, direccionr1.getObjectNumber().intValue());
		assertEquals(1L, direccionr2.getBlockNumber());
		assertEquals(0, direccionr2.getObjectNumber().intValue());
		
	}
	private void setCantidadDeObjetos(final byte[] bloque, Byte cantidadDeObjetos) {
		bloque[blockSize-1] = cantidadDeObjetos;
		bloque[blockSize-2] = 0;
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
	private DynamicAccesor crearArchivo() {
		checking(new Expectations(){{
			atLeast(1).of(fileMock).getTotalBlocks();
			will(new CustomAction("totalBlocks") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					return cantidadDeBloquesInicial;
				}
			});
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
}
