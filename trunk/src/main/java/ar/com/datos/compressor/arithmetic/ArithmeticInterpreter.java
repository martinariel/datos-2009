package ar.com.datos.compressor.arithmetic;

import java.util.Iterator;

import ar.com.datos.bits.BitEmisor;
import ar.com.datos.bits.impl.InputBufferBitEmisor;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.SuperChar;
/**
 * Implementación de un intérprete aritmético (descomprime una compresión aritmética de a un caracter a la vez)
 * Cada descompresión se realiza de a un caracter a la vez para que se le pueda proveer al intérprete diferentes
 * tablas por cada caracter descomprimido
 * @author jbarreneche
 *
 */
public class ArithmeticInterpreter extends ArithmeticProcessor {

	private long currentValue;
	private Iterator<Byte> bitStream;
	private ArithmeticMatcher matcher;
	private byte underflowBit = -1;

	/**
	 * Inicia el interprete para que tome datos del inputBuffer
	 * @param inputBuffer
	 */
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

	/**
	 * Toma un bit de la entrada y lo agrega al valor actual
	 */
	protected void fillCurrentValue() {
		this.currentValue = this.shiftOneLeft(this.currentValue) + this.bitStream.next();
	}

	/**
	 * Ejecuta la descompresión de un caracter desde la tabla de probabilidades.
	 * Puede arrojar ArithmeticInvalidDataException si la información no es consistente con los rangos
	 * @param table
	 * @return
	 * @throws ArithmeticInvalidDataException
	 */
	public SuperChar decompress(ProbabilityTable table) throws ArithmeticInvalidDataException {
		return super.processTable(table, matcher);
	}
	/**
	 * maneja el overflow ocurrido y verifica que se mantenga dentro del rango el nuevo valor actual
	 */
	@Override
	protected void flushOverflow(byte overflowBit) {
		currentValue = currentValue - overflowBit * ONE_IN_OVERFLOW_POSITION;
		zoomValue();
		checkRange();
	}
	/**
	 * verifica que los underflow ocurridos durante los notifyUnderflow tengan el bit correcto que se flushea
	 */
	@Override
	protected void flushUnderflow(byte underflowBit) {
		if (this.underflowBit != underflowBit) throw new ArithmeticInvalidDataException("Los Underflow liberados de la información leida no corresponden a los rangos reales");
		
	}
	/**
	 * Elimina el bit de underflow
	 */
	@Override
	protected void notifyUnderflow() {
		byte previousUnderflowBit = this.underflowBit;
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
		if (this.getUnderflowCount() > 1 && previousUnderflowBit != this.underflowBit) throw new ArithmeticInvalidDataException("Se leyeron dos underflow distintos sin que haya ocurrido una emisión de overflow en el medio"); 
		checkRange();
	}
	/**
	 * Verifica que el valor actual esté dentro del rango si no arroja una excepción
	 */
	protected void checkRange() {
		if (!this.isInRange()) throw new ArithmeticInvalidDataException();
	}

	/**
	 * Hace zoom del currentValue agregándole un bit mas desde la entrada
	 */
	protected void zoomValue() {
		currentValue = shiftOneLeft(currentValue) + this.bitStream.next();
	}

	/**
	 * verifica si el currentValue se encuentra dentro de rango 
	 * @return
	 */
	protected boolean isInRange() {
		return this.isInRange(this.currentValue);
	}

	/**
	 * Construye un emisor de a bits para obtener la información desde el inputBuffer de a un bit
	 * @param inputBuffer
	 * @return
	 */
	protected BitEmisor constructBitEmissor(InputBuffer inputBuffer) {
		return new InputBufferBitEmisor(inputBuffer);
	}

}
