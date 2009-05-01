package ar.com.datos.test.parser;

import junit.framework.TestCase;
import ar.com.datos.parser.Parser;
import ar.com.datos.documentlibrary.MemoryDocument;
import java.util.Collection;

public class TestParser extends TestCase {

	public void testParser(){
		
		MemoryDocument documento = new MemoryDocument();
		
		for (int i = 0; i < 10; i++){
			documento.addLine("Prueba linea; Otra Linea ");
			documento.addLine(" sigue la linea. Fin Linea? Resto?");
		}
		
		Parser parser = new Parser(documento);
		
		int cantidadPalabras = 0;
		int cantidadFrases = 0;
		
		for (Collection<String> oracion : parser){
			System.out.println("-------");
			for (String palabra : oracion){
				cantidadPalabras++;
				System.out.println(palabra);
			}
			cantidadFrases++;
		}
	
		for (Collection<String> oracion : parser){
			for (String palabra : oracion){
				cantidadPalabras++;
			}
			cantidadFrases++;
		}
		
		assertTrue(cantidadPalabras == 200);	
		assertTrue(cantidadFrases == 80);
		
	}
	
}
