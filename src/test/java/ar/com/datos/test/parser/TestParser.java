package ar.com.datos.test.parser;

import junit.framework.TestCase;
import ar.com.datos.parser.Parser;
import ar.com.datos.documentlibrary.MemoryDocument;
import java.util.Collection;

@SuppressWarnings("unused")
public class TestParser extends TestCase {

    public void testParser(){

        MemoryDocument documento = new MemoryDocument();

        for (int i = 0; i < 10; i++){
            documento.addLine("                                 				");
            documento.addLine("## Pruñba **+++linea; Otra Linea  %%$$$  		");
            documento.addLine(" sigue la  $$ &&& linea.							");
            documento.addLine("                                 				");
            documento.addLine("			Fin Linea? 		Resto,					");
            documento.addLine("            55555559999887788    				");
        }

        Parser parser = new Parser(documento);

        int cantidadPalabras = 0;
        int cantidadFrases = 0;

        int times = 10;

        for (int i = 0; i < times; i++){
            for (Collection<String> oracion : parser){
                for (String palabra : oracion){
                    cantidadPalabras++;
                }
                cantidadFrases++;
            }
        }

        assertTrue(cantidadPalabras == (100*times));
        assertTrue(cantidadFrases == (40*times));

    }

    public void testParserSinglePhraseDocument(){

        MemoryDocument document = new MemoryDocument();

        for (int i = 0; i < 1000; i++){
            document.addLine("test <> '''' document double phrase.............()()():::::'''''");
        }

        Parser parser = new Parser(document);
        int phrases = 0;
        int words = 0;

        for (Collection<String> phrase : parser){
            for (String word : phrase){
                words++;
            }
            phrases++;
        }

        assertTrue (words == 4000);
        assertTrue (phrases == 2000);
    }

    public void testParserGigantLine(){
        MemoryDocument document = new MemoryDocument();
        StringBuilder string = new StringBuilder();

        for (int i = 0; i < 1000; i++){
            string.append(" test # document ++++ gigant line ");
        }

        document.addLine(string.toString());

        Parser parser = new Parser(document);
        int phrases = 0;
        int words = 0;

        for (Collection<String> phrase : parser){
            for (String word : phrase){
                words++;
            }
            phrases++;
        }

        assertTrue (words == 4000);
        assertTrue (phrases == 1);

    }
    
    public void testSeparators() {
    	
    	MemoryDocument document = new MemoryDocument();
    	document.addLine("....hola,\"como te va\"\"hola\".");
    	document.addLine(".probandp.probando.!!!?prueba?..seguro que si,\"como te va\"\"hola\".");
    	document.addLine(".probandp.probando.!!!?prueba?..seguro que si,\"como te va\"\"hola\".");
    	
    	Parser parser = new Parser(document);
    	
    	int words = 0;
    	
    	for (Collection<String> oracion : parser){
    		words += oracion.size();
    	}
    	
    	assertTrue(25 == words);
    	
    }

}
