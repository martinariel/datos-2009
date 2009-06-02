package ar.com.datos.compressor.arithmetic;

import java.io.IOException;

import ar.com.datos.bits.BitReceiver;
import ar.com.datos.bits.impl.OutputBufferBitReceiver;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;

public class ArithmeticEmissor extends ArithmeticProcessor {
	
	private BitReceiver bitReceiver;

	public ArithmeticEmissor(OutputBuffer outputBuffer) {
		this.bitReceiver = constructBitReceiver(outputBuffer);
	}

	protected BitReceiver constructBitReceiver(OutputBuffer outputBuffer) {
		return new OutputBufferBitReceiver(outputBuffer);
	}

	public SuperChar compress(final SimpleSuperChar characterAEmitir, ProbabilityTable table) {
		// Defino el matcher para el processTable para que matchee el caracter recibido
		ArithmeticMatcher matcher = new ArithmeticMatcher() {

			@Override
			public boolean matches(SuperChar character, long ceiling) {
				return characterAEmitir.matches(character);
			}
			
		};
		return processTable(table, matcher);
	}

	@Override
	protected void flushUnderflow(byte underflowBit) {
		this.bitReceiver.addBit(underflowBit);
	}

	@Override
	protected void flushOverflow(byte overflowBit) {
		this.bitReceiver.addBit(overflowBit);
	}

	@Override
	public void close() throws IOException {
		super.close();
		this.bitReceiver.close();
	}

}
