package ar.com.datos.test.compressor.arithmetic;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.jmock.integration.junit3.MockObjectTestCase;

import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.buffer.variableLength.ArrayInputBuffer;
import ar.com.datos.buffer.variableLength.ArrayOutputBuffer;
import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.SimpleProbabilityTable;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticEmissor;
import ar.com.datos.compressor.arithmetic.ArithmeticInterpreter;


public class ArithmeticInterpreterTest extends MockObjectTestCase {

	private ArithmeticEmissor emisor;
	private ArrayByte arrayByte;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		arrayByte = new SimpleArrayByte(new byte[1024]);
		emisor = new ArithmeticEmissor(new ArrayOutputBuffer(arrayByte));
	}
	/**
	 * Voy a pedirle que emita dos veces un mismo caracter.
	 * Luego, tomando el resultado emitido, voy a pedirle que lo interprete 
	 * @throws Exception
	 */
	public void testOneSimpleChar() throws Exception {
		SimpleSuperChar caracterAEmitir = new SimpleSuperChar('a');
		ProbabilityTable table = constructTable(new SuperChar[] {new SimpleSuperChar('b'), caracterAEmitir},
										  		new Integer[] 	{5,					 		5});
		emisor.compress(caracterAEmitir, table);
		emisor.compress(caracterAEmitir, table);
		emisor.close();
		ArithmeticInterpreter interprete = new ArithmeticInterpreter(new ArrayInputBuffer(arrayByte));
		assertEquals(caracterAEmitir, interprete.decompress(table));
		assertEquals(caracterAEmitir, interprete.decompress(table));
	}
	/**
	 * Voy a emitir una cadena aleatoria con probabilidades equivalentes. 
	 * Luego verificar que se interprete la misma cadena
	 * @throws Exception
	 */
	public void testHigherOverflow() throws Exception {
		SimpleSuperChar[] caracterAEmitir = new SimpleSuperChar[] { new SimpleSuperChar('a'), new SimpleSuperChar('b'),
																	new SimpleSuperChar('c'), new SimpleSuperChar('d'), 
																	new SimpleSuperChar('e'), new SimpleSuperChar('f'), 
																	new SimpleSuperChar('g'), new SimpleSuperChar('h')};
		ProbabilityTable table = constructTable(caracterAEmitir, new Integer[] 	{5,5,5,5,5,5,5,5});
		Collection<SimpleSuperChar> emision = new ArrayList<SimpleSuperChar>(64);
		Random randomness = new Random();
		for (Integer i = 0 ; i < 64; i++) {
			emision.add(caracterAEmitir[ Math.abs(randomness.nextInt()) % caracterAEmitir.length ]);
		}
		for (SimpleSuperChar ssc: emision) {
			emisor.compress(ssc, table);
		}
		emisor.close();
		ArithmeticInterpreter interprete = new ArithmeticInterpreter(new ArrayInputBuffer(arrayByte));
		for (SimpleSuperChar ssc: emision) {
			assertEquals(ssc, interprete.decompress(table));
		}
		
	}
	/**
	 * Voy a pedirle que emita con una tabla que tiene 3 caracteres
	 * las probabilidades son 2 |P('b') = |P('q') = |P('a') = |P ('c') = 2 |P ('d')  
	 * Lo cual genera underflows, el interprete debería actualizar su estado para poder 
	 * recuperar correctamente los caracteres
	 * Para generar el overflow emito, con la misma tabla, 'b' que debería generar 2 bits de overflow (00)
	 * Dando como resultado de la emisión (en el Overflow => 010)
	 * @throws Exception
	 */
	public void testUnderflow() throws Exception {
		SimpleSuperChar caracterUnderflow = new SimpleSuperChar('a');
		SimpleSuperChar caracterOverflow = new SimpleSuperChar('b');
		ProbabilityTable table = constructTable(new SuperChar[] {caracterOverflow, 
																 new SimpleSuperChar('q'),
																 caracterUnderflow, 
																 new SimpleSuperChar('c'),
																 new SimpleSuperChar('d')},
										  		new Integer[] 	{5,	10, 10, 10, 5});
		// Esto solo genera Underflow por lo que no tengo expectativas de emisión
		emisor.compress(caracterUnderflow, table);
		// Esto solo genera Underflow por lo que no tengo expectativas de emisión
		emisor.compress(caracterOverflow, table);
		emisor.close();
		
		ArithmeticInterpreter interprete = new ArithmeticInterpreter(new ArrayInputBuffer(arrayByte));
		assertEquals(caracterUnderflow, interprete.decompress(table));
		assertEquals(caracterOverflow, interprete.decompress(table));
	}
	/**
	 * Voy a emitir una cadena aleatoria con probabilidades aleatorias. 
	 * Luego verificar que se interprete la misma cadena
	 * @throws Exception
	 */
	public void testIntensive() throws Exception {
		Random randomness = new Random();
		SimpleSuperChar[] caracterAEmitir = new SimpleSuperChar[] { new SimpleSuperChar('a'), new SimpleSuperChar('b'),
																	new SimpleSuperChar('c'), new SimpleSuperChar('d'),
																	new SimpleSuperChar('e'), new SimpleSuperChar('f'), 
																	new SimpleSuperChar('g'), new SimpleSuperChar('h')};
		ProbabilityTable table = constructTable(caracterAEmitir, new Integer[] 	{Math.abs(randomness.nextInt()) % 500, Math.abs(randomness.nextInt()) % 500,
																				 Math.abs(randomness.nextInt()) % 500, Math.abs(randomness.nextInt()) % 500,
																				 Math.abs(randomness.nextInt()) % 500, Math.abs(randomness.nextInt()) % 500,
																				 Math.abs(randomness.nextInt()) % 500, Math.abs(randomness.nextInt()) % 500});
		Collection<SimpleSuperChar> emision = new ArrayList<SimpleSuperChar>(64);
		for (Integer i = 0 ; i < 128; i++) {
			emision.add(caracterAEmitir[ Math.abs(randomness.nextInt()) % caracterAEmitir.length ]);
		}
		for (SimpleSuperChar ssc: emision) {
			emisor.compress(ssc, table);
		}
		emisor.close();
		ArithmeticInterpreter interprete = new ArithmeticInterpreter(new ArrayInputBuffer(arrayByte));
		String probabilidades = table.toString() + " " + emision.toString();
		for (SimpleSuperChar ssc: emision) {
			emisor.compress(ssc, table);
			assertEquals(probabilidades, ssc, interprete.decompress(table));
		}
		
	}
	public ProbabilityTable constructTable(SuperChar[] superChars,
			Integer[] integers) {
		SimpleProbabilityTable table = new SimpleProbabilityTable();
		for (Integer i = 0; i < superChars.length; i++)
			table.addChar(superChars[i], integers[i]);
		return table;
	}
}
