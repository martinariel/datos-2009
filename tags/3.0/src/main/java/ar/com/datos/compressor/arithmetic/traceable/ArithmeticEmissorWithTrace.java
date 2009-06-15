package ar.com.datos.compressor.arithmetic.traceable;

import java.io.PrintStream;

import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticEmissor;

public class ArithmeticEmissorWithTrace extends ArithmeticEmissor {

	private PrintStream tracer;
	public ArithmeticEmissorWithTrace(OutputBuffer outputBuffer, PrintStream tracer) {
		super(outputBuffer);
		this.tracer = tracer;
	}

	@Override
	public SuperChar compress(SuperChar characterInput, ProbabilityTable table) {
		tracer.append("\ncomprimiendo el caracter: " + ((SimpleSuperChar)characterInput).toAnotherString() + "\n");
		traceFloorAndCeiling();
		tracer.append("tabla de probabilidades: \n" + table.toString() + "\n");
		return super.compress(characterInput, table);
	}

	private void traceFloorAndCeiling() {
		tracer.append("piso:   0b" + binaryText32bits(this.getFloor())+ " - " + this.getFloor()  + "\n");
		tracer.append("techo:  0b" + binaryText32bits(this.getCeiling()) + " - " + this.getCeiling() + "\n");
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

	@Override
	protected void clearOverflow() {
		tracer.append("Rango seleccionado: \n");
		traceFloorAndCeiling();
		tracer.append("Overflow: ");
		if (!this.isInOverflow()) tracer.append("(sin overflow)");
		super.clearOverflow();
		tracer.append('\n');
	}
	@Override
	protected void flushOverflow(byte overflowBit) {
		tracer.append(overflowBit > 0? "1" : "0");
		super.flushOverflow(overflowBit);
	}
	@Override
	protected void flushUnderflow(byte underflowBit) {
		tracer.append(underflowBit > 0? "1" : "0");
		super.flushUnderflow(underflowBit);
	}
	@Override
	protected void clearUnderflow() {
		super.clearUnderflow();
		tracer.append("Undeflow: " + this.getUnderflowCount() + " (contados desde el último overflow)\n");
	}
	@Override
	public void close() {
		tracer.append("realizando la última emisión... \n");
		super.close();
	}
}
