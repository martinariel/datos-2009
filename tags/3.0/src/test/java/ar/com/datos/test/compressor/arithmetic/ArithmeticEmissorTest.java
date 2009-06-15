package ar.com.datos.test.compressor.arithmetic;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit3.MockObjectTestCase;

import ar.com.datos.bits.BitReceiver;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.buffer.variableLength.ArrayOutputBuffer;
import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.SimpleProbabilityTable;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticEmissor;


public class ArithmeticEmissorTest extends MockObjectTestCase {

	private ArithmeticEmissor emisor;
	private BitReceiver bitReceiverMock;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bitReceiverMock = this.mock(BitReceiver.class);
		emisor = new ArithmeticEmissor(new ArrayOutputBuffer(5)) {
			@Override
			protected BitReceiver constructBitReceiver(OutputBuffer buffer) {
				return bitReceiverMock;
			}
		};
	}
	/**
	 * Voy a pedirle que emita con una tabla que tiene sólo 2 caracteres
	 * ambos con la misma probabilidad. 
	 * Le pido que emita el primero de los 2 caracteres (que tomaría el rango 00...0 -> 01...1
	 * Por lo cual esto debería generarle un OverFlow de cero.
	 * @throws Exception
	 */
	public void testLowerOverflow() throws Exception {
		SimpleSuperChar caracterAEmitir = new SimpleSuperChar('a');
		ProbabilityTable table = constructTable(new SuperChar[] {caracterAEmitir, new SimpleSuperChar('b')},
										  		new Integer[] 	{5,					 		5});
		this.checking(new Expectations(){{
			one(bitReceiverMock).addBit((byte)0);
		}});
		SuperChar emitido = emisor.compress(caracterAEmitir, table);
		assertEquals(caracterAEmitir, emitido);
	}
	/**
	 * Voy a pedirle que emita con una tabla que tiene sólo 2 caracteres
	 * ambos con la misma probabilidad. 
	 * Le pido que emita el segundo de los 2 caracteres (que tomaría el rango 10...0 -> 11...1
	 * Por lo cual esto debería generarle un OverFlow de uno.
	 * @throws Exception
	 */
	public void testHigherOverflow() throws Exception {
		SimpleSuperChar caracterAEmitir = new SimpleSuperChar('a');
		ProbabilityTable table = constructTable(new SuperChar[] {new SimpleSuperChar('b'), caracterAEmitir},
										  		new Integer[] 	{5,					 		5});
		this.checking(new Expectations(){{
			one(bitReceiverMock).addBit((byte)1);
		}});
		SuperChar emitido = emisor.compress(caracterAEmitir, table);
		assertEquals(caracterAEmitir, emitido);
	}
	/**
	 * Voy a pedirle que emita con una tabla que tiene 4 caracteres
	 * todos con la misma probabilidad. 
	 * Le pido que emita el segundo de los 2 caracteres (que tomaría el rango 010...0 -> 011...1
	 * Por lo cual esto debería generarle un OverFlow de cero y de uno.
	 * @throws Exception
	 */
	public void testDoubleOverflow() throws Exception {
		SimpleSuperChar caracterAEmitir = new SimpleSuperChar('a');
		ProbabilityTable table = constructTable(new SuperChar[] {new SimpleSuperChar('b'), caracterAEmitir, new SimpleSuperChar('c'), new SimpleSuperChar('d')},
										  		new Integer[] 	{5,					 		5,				5,							5});
		final Sequence sequence = this.sequence("multiple-emision");
		this.checking(new Expectations(){{
			one(bitReceiverMock).addBit((byte)0);
			inSequence(sequence);
			one(bitReceiverMock).addBit((byte)1);
			inSequence(sequence);
		}});
		SuperChar emitido = emisor.compress(caracterAEmitir, table);
		assertEquals(caracterAEmitir, emitido);
	}
	/**
	 * Voy a pedirle que emita con una tabla que tiene 5 caracteres
	 * las probabilidades son 2 |P('b') = |P('q') = |P('a') = |P ('c') = 2 |P ('d')  
	 * Al emitir a debería generar un underflow de una posición que se emitirá en el próximo overflow (el rango queda completo 0x00000000 -> 0xFFFFFFFF
	 * Para generar el overflow emito, con la misma tabla, 'b' que debería generar 2 bits de overflow (00)
	 * Dando como resultado de la emisión (en el Overflow => 010)
	 * @throws Exception
	 */
	public void testUnderflow() throws Exception {
		SimpleSuperChar caracterAEmitir = new SimpleSuperChar('a');
		SimpleSuperChar caracterOverflow = new SimpleSuperChar('b');
		ProbabilityTable table = constructTable(new SuperChar[] {caracterOverflow, 
																 new SimpleSuperChar('q'),
																 caracterAEmitir, 
																 new SimpleSuperChar('c'),
																 new SimpleSuperChar('d')},
										  		new Integer[] 	{5,	10, 10, 10, 5});
		// Esto solo genera Underflow por lo que no tengo expectativas de emisión
		SuperChar emitido = emisor.compress(caracterAEmitir, table);
		assertEquals(caracterAEmitir, emitido);

		// Ahora voy a hacer Overflow que si espero emisión
		final Sequence sequence = this.sequence("multiple-emision");
		this.checking(new Expectations(){{
			one(bitReceiverMock).addBit((byte)0);
			inSequence(sequence);
			exactly(2).of(bitReceiverMock).addBit((byte)1);
			inSequence(sequence);
			exactly(2).of(bitReceiverMock).addBit((byte)0);
			inSequence(sequence);
		}});

		// Esto solo genera Underflow por lo que no tengo expectativas de emisión
		emitido = emisor.compress(caracterOverflow, table);
		assertEquals(caracterOverflow, emitido);
	}
	/**
	 * Al cerrarse el compresor deben emitirse el piso y los underflow existentes
	 * Se generará un underflow similar al testUnderflow() y luego se cerrará el aritmético
	 * Se espera que emita 0, los dos underflow y luego 31 ceros mas.
	 * @throws Exception
	 */
	public void testClose() throws Exception {
		SimpleSuperChar caracterAEmitir = new SimpleSuperChar('a');
		ProbabilityTable table = constructTable(new SuperChar[] {new SimpleSuperChar('b'), 
																 new SimpleSuperChar('q'),
																 caracterAEmitir, 
																 new SimpleSuperChar('c'),
																 new SimpleSuperChar('d')},
										  		new Integer[] 	{5,	10, 10, 10, 5});
		// Esto solo genera Underflow por lo que no tengo expectativas de emisión
		SuperChar emitido = emisor.compress(caracterAEmitir, table);
		assertEquals(caracterAEmitir, emitido);

		// Ahora voy a hacer Overflow que si espero emisión
		final Sequence sequence = this.sequence("multiple-emision");
		this.checking(new Expectations(){{
			one(bitReceiverMock).addBit((byte)0);
			inSequence(sequence);
			exactly(2).of(bitReceiverMock).addBit((byte)1);
			inSequence(sequence);
			exactly(31).of(bitReceiverMock).addBit((byte)0);
			inSequence(sequence);
			one(bitReceiverMock).close();
		}});
		
		emisor.close();
		
	}
	public ProbabilityTable constructTable(SuperChar[] superChars,
			Integer[] integers) {
		SimpleProbabilityTable table = new SimpleProbabilityTable();
		for (Integer i = 0; i < superChars.length; i++)
			table.addChar(superChars[i], integers[i]);
		return table;
	}
}
