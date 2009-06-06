package ar.com.datos.compressor.arithmetic;

import java.io.IOException;

import ar.com.datos.bits.BitReceiver;
import ar.com.datos.bits.impl.OutputBufferBitReceiver;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.SuperChar;
/**
 * Implementación de un compresor aritmético (comprime con representación aritmética de a un caracter a la vez)
 * Cada compresión se realiza de a un caracter a la vez para que se le pueda proveer al intérprete diferentes
 * tablas por cada caracter comprimido
 * @author jbarreneche
 *
 */
public class ArithmeticEmissor extends ArithmeticProcessor {
	
	private BitReceiver bitReceiver;

	/**
	 * Crea un nuevo ArithmethicEmissor que emitirá bytes en el outputbuffer.
	 * Tener en cuenta que para realizar la última emisión debe ejecutarse el close
	 * de esta entidad 
	 * @param outputBuffer
	 */
	public ArithmeticEmissor(OutputBuffer outputBuffer) {
		this.bitReceiver = constructBitReceiver(outputBuffer);
	}

	/**
	 * Comprime el caracter <code>characterInput</code> según la tabla de probabilidades.
	 * @param characterInput
	 * @param table
	 * @return representación del caracter <code>characterInput</code> en la tabla.
	 */
	public SuperChar compress(final SuperChar characterInput, ProbabilityTable table) {
		// Defino el matcher para el processTable para que matchee el caracter recibido
		ArithmeticMatcher matcher = new ArithmeticMatcher() {

			@Override
			public boolean matches(SuperChar character, long ceiling) {
				return character.matches(characterInput);
			}
			
		};
		return processTable(table, matcher);
	}

	/**
	 * Emite el bit de underflow
	 */
	@Override
	protected void flushUnderflow(byte underflowBit) {
		this.bitReceiver.addBit(underflowBit);
	}

	/**
	 * Emite el bit de overflow
	 */
	@Override
	protected void flushOverflow(byte overflowBit) {
		this.bitReceiver.addBit(overflowBit);
	}

	@Override
	protected void notifyUnderflow() {
		// No necesita hacer nada cuando ocurre underflow en el rango
	}

	/**
	 * Cierra el bitReceiver
	 */
	@Override
	public void close() throws IOException {
		super.close();
		this.bitReceiver.close();
	}

	/**
	 * Construye un receptor de bits para obtener la emitir la información al outputBuffer de a bytes
	 * @param outputBuffer
	 * @return
	 */
	protected BitReceiver constructBitReceiver(OutputBuffer outputBuffer) {
		return new OutputBufferBitReceiver(outputBuffer);
	}

}
