package ar.com.datos.bits.impl;

import ar.com.datos.bits.BitReceiver;
import ar.com.datos.bits.BitsException;
import ar.com.datos.buffer.OutputBuffer;

/**
 * {@link BitReceiver} que guarda sus bits en un {@link OutputBuffer}.
 * 
 * @author fvalido
 */
public class OutputBufferBitReceiver implements BitReceiver {
	/** Estructura usada para completar cada byte. */
	private byte[] bitSequenceBuffer;
	/** Estructura usada para saber la posición actual dentro de bitSequenceBuffer */
	private byte bitSequencePosition;
	/** OutputBuffer usado como destino de cada byte completado. */
	private OutputBuffer outputBuffer;
	
	/**
	 * Construye una instancia.
	 * 
	 * @param outputBuffer
	 * Buffer de destino para los bits agregados.
	 */
	public OutputBufferBitReceiver(OutputBuffer outputBuffer) {
		this.outputBuffer = outputBuffer;
		this.bitSequenceBuffer = new byte[8];
		this.bitSequencePosition = 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.bits.BitReceiver#add0()
	 */
	@Override
	public void add0() {
		addBit((byte)0);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.bits.BitReceiver#add1()
	 */
	@Override
	public void add1() {
		addBit((byte)1);
	}

	/**
	 * Finaliza el agregado de un byte completo.
	 */
	private void closeByte() {
		byte bits = 0;

		int pow = 1;
		for (int i = 7; i >= 0; i--) {
			bits += this.bitSequenceBuffer[i] * pow;
			pow *= 2;
		}
		
		this.outputBuffer.write(bits);
		this.bitSequencePosition = 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.bits.BitReceiver#addBit(byte)
	 */
	@Override
	public void addBit(byte bit) throws BitsException {
		if (bit != 1 && bit != 0) {
			throw new BitsException();
		}
		
		this.bitSequenceBuffer[this.bitSequencePosition] = bit;
		this.bitSequencePosition++;
		if (this.bitSequencePosition == 8) {
			closeByte();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.bits.BitReceiver#close()
	 */
	@Override
	public void close() {
		if (this.bitSequencePosition != 0) {
			// Pongo en 0 los bits de la derecha del bitSequenceBuffer que no fueron establecidos.
			for (int i = this.bitSequencePosition; i < 8; i++) {
				this.bitSequenceBuffer[i] = 0;
			}
			// Cierro el byte normalmente.
			closeByte();
		}
	}
}
