package ar.com.datos.compressor.arithmetic;

import java.util.Iterator;

import ar.com.datos.bits.BitEmisor;
import ar.com.datos.bits.impl.InputBufferBitEmisor;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.SuperChar;

public class ArithmeticInterpreter extends ArithmeticProcessor {

	private long currentValue;
	private Iterator<Byte> bitStream;
	private ArithmeticMatcher matcher;
	private byte underflowBit = -1;
	
	public ArithmeticInterpreter(InputBuffer inputBuffer) {
		this.bitStream = constructBitEmissor(inputBuffer).iterator();
		for (int i = 0; i < precission; i++) {
			fillCurrentValue();
		}
		matcher = new ArithmeticMatcher() {
		
			@Override
			public boolean matches(SuperChar character, long ceiling) {
				return currentValue <= ceiling;
			}
		};
	}

	protected void fillCurrentValue() {
		this.currentValue = this.shiftOneLeft(this.currentValue) + this.bitStream.next();
	}

	public SuperChar decompress(ProbabilityTable table) {
		return super.processTable(table, matcher);
	}
	@Override
	protected void flushOverflow(byte overflowBit) {
		currentValue = currentValue - overflowBit * ONE_IN_OVERFLOW_POSITION;
		zoomValue();
		checkRange();
	}

	@Override
	protected void flushUnderflow(byte underflowBit) {
		if (this.underflowBit != underflowBit) throw new ArithmeticInvalidDataException("Los Underflow liberados de la información leida no corresponden a los rangos reales");
		
	}
	@Override
	protected void notifyUnderflow() {
		super.notifyUnderflow();
		if (this.currentValue > OVERFLOW_SEPARATOR) {
			this.underflowBit = 0;
			this.currentValue = removeTheOneInOverflowPosition(currentValue);
			zoomValue();
			this.currentValue = addTheOneInOverflowPosition(currentValue);
		} else {
			this.underflowBit = 1;
			this.currentValue = removeTheOneInUnderflowPosition(currentValue);
			zoomValue();
		}
		checkRange();
	}
	protected void checkRange() {
		if (!this.isInRange()) throw new ArithmeticInvalidDataException();
	}

	protected void zoomValue() {
		currentValue = shiftOneLeft(currentValue) + this.bitStream.next();
	}

	protected boolean isInRange() {
		return this.isInRange(this.currentValue);
	}

	protected BitEmisor constructBitEmissor(InputBuffer inputBuffer) {
		return new InputBufferBitEmisor(inputBuffer);
	}

}
