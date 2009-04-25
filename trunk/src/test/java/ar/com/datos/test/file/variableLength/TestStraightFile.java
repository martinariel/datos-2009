package ar.com.datos.test.file.variableLength;

import java.util.Iterator;

import junit.framework.TestCase;
import ar.com.datos.file.variableLength.OffsetAddress;
import ar.com.datos.file.variableLength.StraightVariableLengthFile;
import ar.com.datos.serializer.common.StringSerializerDelimiter;

public class TestStraightFile extends TestCase {
	/**
	 * Creo un archivo limpio y lo itero
	 * @throws Exception
	 */
	public void testCreacionEIterado() throws Exception {
		StraightVariableLengthFile<String> archivo = new StraightVariableLengthFile<String>(new StringSerializerDelimiter());
		String[] datos = new String[] {"bueno","bonito","y barato"}; 
		archivo.addEntity(datos[0]);
		archivo.addEntity(datos[1]);
		archivo.addEntity(datos[2]);
		Iterator<String> iterador = archivo.iterator();
		for (Integer i = 0; i < datos.length; i++) {
			assertTrue(iterador.hasNext());
			assertEquals(datos[i], iterador.next());
		}
		
		assertFalse(iterador.hasNext());
		
	}

	/**
	 * Creo un archivo limpio y lo itero
	 * @throws Exception
	 */
	public void testRecuperoLoGrabado() throws Exception {
		StraightVariableLengthFile<String> archivo = new StraightVariableLengthFile<String>(new StringSerializerDelimiter());
		String[] datos = new String[] {"bueno","bonito","y barato"};
		OffsetAddress[] direcciones = new OffsetAddress[datos.length];
		for (Integer i = 0; i < datos.length; i++)
			direcciones[i] = archivo.addEntity(datos[i]);

		for (Integer i = datos.length - 1; i >= 0; i--) {
			assertEquals(datos[i], archivo.get(direcciones[i]));
			
		}
		
	}
}
