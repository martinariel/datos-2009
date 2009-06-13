package ar.com.datos.compressor.arithmetic.traceable;

import java.io.PrintStream;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticInterpreter;
import ar.com.datos.compressor.arithmetic.ArithmeticInvalidDataException;

public class ArithmeticInterpreterWithTrace extends ArithmeticInterpreter {

	private PrintStream tracer;
	public ArithmeticInterpreterWithTrace(InputBuffer inputBuffer, PrintStream out) {
		super(inputBuffer);
		this.tracer = out;
	}

	protected String binaryText32bits(long value) {
		StringBuilder sb = new StringBuilder();
		long mascara = (1L << 31);
		while (mascara > 0) {
			sb.append((value & mascara) > 0L? "1" : "0");
			mascara = mascara >> 1;
		}
		return sb.toString();
	}
	private void traceFloorAndCeiling() {
		tracer.append("piso:   0b" + binaryText32bits(this.getFloor())+ " - " + this.getFloor()  + "\n");
		tracer.append("techo:  0b" + binaryText32bits(this.getCeiling()) + " - " + this.getCeiling() + "\n");
	}

	@Override
	public SuperChar decompress(ProbabilityTable table)
			throws ArithmeticInvalidDataException {
		tracer.append("\nValor Actual 0b" + binaryText32bits(this.getCurrentValue())+ " " + this.getCurrentValue() + "\n");
		traceFloorAndCeiling();
		tracer.append("tabla de probabilidades: \n" + table.toString() + "\n");
		SuperChar caracterObtenido = super.decompress(table);
		tracer.append("caracter descomprimido : " + ((SimpleSuperChar)caracterObtenido).toAnotherString() + "\n");
		return caracterObtenido;
	}
}
