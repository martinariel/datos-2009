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
		arrayByte = new SimpleArrayByte(new byte[128]);
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
			emisor.compress(ssc, table);
			assertEquals(ssc, interprete.decompress(table));
		}
		
	}
//	/**
//	 * Voy a pedirle que emita con una tabla que tiene 4 caracteres
//	 * todos con la misma probabilidad. 
//	 * Le pido que emita el segundo de los 2 caracteres (que tomaría el rango 010...0 -> 011...1
//	 * Por lo cual esto debería generarle un OverFlow de cero y de uno.
//	 * @throws Exception
//	 */
//	public void testDoubleOverflow() throws Exception {
//		SimpleSuperChar caracterAEmitir = new SimpleSuperChar('a');
//		ProbabilityTable table = constructTable(new SuperChar[] {new SimpleSuperChar('b'), caracterAEmitir, new SimpleSuperChar('c'), new SimpleSuperChar('d')},
//										  		new Integer[] 	{5,					 		5,				5,							5});
//		final Sequence sequence = this.sequence("multiple-emision");
//		this.checking(new Expectations(){{
//			one(bitReceiverMock).addBit((byte)0);
//			inSequence(sequence);
//			one(bitReceiverMock).addBit((byte)1);
//			inSequence(sequence);
//		}});
//		SuperChar emitido = emisor.compress(caracterAEmitir, table);
//		assertEquals(caracterAEmitir, emitido);
//	}
//	/**
//	 * Voy a pedirle que emita con una tabla que tiene 3 caracteres
//	 * las probabilidades son 2 |P('b') = |P('q') = |P('a') = |P ('c') = 2 |P ('d')  
//	 * Al emitir a debería generar un underflow de una posición que se emitirá en el próximo overflow (el rango queda completo 0x00000000 -> 0xFFFFFFFF
//	 * Para generar el overflow emito, con la misma tabla, 'b' que debería generar 2 bits de overflow (00)
//	 * Dando como resultado de la emisión (en el Overflow => 010)
//	 * @throws Exception
//	 */
//	public void testUnderflow() throws Exception {
//		SimpleSuperChar caracterAEmitir = new SimpleSuperChar('a');
//		SimpleSuperChar caracterOverflow = new SimpleSuperChar('b');
//		ProbabilityTable table = constructTable(new SuperChar[] {caracterOverflow, 
//																 new SimpleSuperChar('q'),
//																 caracterAEmitir, 
//																 new SimpleSuperChar('c'),
//																 new SimpleSuperChar('d')},
//										  		new Integer[] 	{5,	10, 10, 10, 5});
//		// Esto solo genera Underflow por lo que no tengo expectativas de emisión
//		SuperChar emitido = emisor.compress(caracterAEmitir, table);
//		assertEquals(caracterAEmitir, emitido);
//
//		// Ahora voy a hacer Overflow que si espero emisión
//		final Sequence sequence = this.sequence("multiple-emision");
//		this.checking(new Expectations(){{
//			one(bitReceiverMock).addBit((byte)0);
//			inSequence(sequence);
//			exactly(2).of(bitReceiverMock).addBit((byte)1);
//			inSequence(sequence);
//			exactly(2).of(bitReceiverMock).addBit((byte)0);
//			inSequence(sequence);
//		}});
//
//		// Esto solo genera Underflow por lo que no tengo expectativas de emisión
//		emitido = emisor.compress(caracterOverflow, table);
//		assertEquals(caracterOverflow, emitido);
//	}
//	/**
//	 * Al cerrarse el compresor deben emitirse el piso y los underflow existentes
//	 * Se generará un underflow similar al testUnderflow() y luego se cerrará el aritmético
//	 * Se espera que emita 0, los dos underflow y luego 31 ceros mas.
//	 * @throws Exception
//	 */
//	public void testClose() throws Exception {
//		SimpleSuperChar caracterAEmitir = new SimpleSuperChar('a');
//		ProbabilityTable table = constructTable(new SuperChar[] {new SimpleSuperChar('b'), 
//																 new SimpleSuperChar('q'),
//																 caracterAEmitir, 
//																 new SimpleSuperChar('c'),
//																 new SimpleSuperChar('d')},
//										  		new Integer[] 	{5,	10, 10, 10, 5});
//		// Esto solo genera Underflow por lo que no tengo expectativas de emisión
//		SuperChar emitido = emisor.compress(caracterAEmitir, table);
//		assertEquals(caracterAEmitir, emitido);
//
//		// Ahora voy a hacer Overflow que si espero emisión
//		final Sequence sequence = this.sequence("multiple-emision");
//		this.checking(new Expectations(){{
//			one(bitReceiverMock).addBit((byte)0);
//			inSequence(sequence);
//			exactly(2).of(bitReceiverMock).addBit((byte)1);
//			inSequence(sequence);
//			exactly(31).of(bitReceiverMock).addBit((byte)0);
//			inSequence(sequence);
//			one(bitReceiverMock).close();
//		}});
//		
//		emisor.close();
//		
//	}
	public ProbabilityTable constructTable(SuperChar[] superChars,
			Integer[] integers) {
		SimpleProbabilityTable table = new SimpleProbabilityTable();
		for (Integer i = 0; i < superChars.length; i++)
			table.addChar(superChars[i], integers[i]);
		return table;
	}
}
