package ar.com.datos.test.parser;

import junit.framework.TestCase;
import ar.com.datos.parser.Parser;
import ar.com.datos.documentlibrary.SimpleLinesDocument;
import java.util.Collection;

public class TestParser extends TestCase {

	public void testParser(){
		SimpleLinesDocument documento = new SimpleLinesDocument();
		documento.addLine("Prueba linea. Otra Linea ");
		documento.addLine(" sigue la linea. Fin Linea? Resto");
		
		Parser parser = new Parser(documento);
		
		for (Collection<String> oracion : parser){
			System.out.println(" _________________");
			for (String palabra : oracion){
				System.out.println(palabra);
			}
		}
	}
	
}
