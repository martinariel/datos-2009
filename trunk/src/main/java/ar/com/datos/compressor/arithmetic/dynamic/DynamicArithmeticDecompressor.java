package ar.com.datos.compressor.arithmetic.dynamic;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.CompressorException;
import ar.com.datos.compressor.FileDeCompressor;
import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticInterpreter;
import ar.com.datos.compressor.arithmetic.traceable.ArithmeticInterpreterWithTrace;
import ar.com.datos.documentlibrary.Document;

public class DynamicArithmeticDecompressor implements FileDeCompressor {
	/** Contextos del modelo */
	private Map<Character, ProbabilityTableByFrequencies> contexts;
	private PrintStream tracer;

	public DynamicArithmeticDecompressor() {
		cleanContexts();
	}
	public DynamicArithmeticDecompressor(PrintStream out) {
		this();
		this.tracer = out;
	}
	public void decompress(InputBuffer input, Document document) throws CompressorException {
		ArithmeticInterpreter interpreter = constructArithmeticInterpreter(input);
		SuperChar current = this.decompressInner(null, interpreter);;
		Character context = current.charValue();
		while (!SimpleSuperChar.EOF.equals(current)) {
			document.addLine(context.toString());
			current = this.decompressInner(context, interpreter);
			context = current.charValue();
		}
		interpreter.close();
	}
	private ArithmeticInterpreter constructArithmeticInterpreter(InputBuffer input) {
		return this.tracer == null? new ArithmeticInterpreter(input) : new ArithmeticInterpreterWithTrace(input, this.tracer);
	}

	protected void write(OutputBuffer output, SuperChar current) {
		char aChar = current.charValue();
		output.write((byte)(aChar >> 8));
		output.write((byte)(aChar));
	}

	protected SuperChar decompressInner(Character context, ArithmeticInterpreter interpreter) {
		ProbabilityTableByFrequencies table = getProbabilityTable(context);
		SuperChar aSuperChar = interpreter.decompress(table);
		table.addOccurrence(aSuperChar);
		return aSuperChar;
	}
	
	public Character decompress(Character context, ArithmeticInterpreter interpreter) {
		return this.decompressInner(context, interpreter).charValue();
	}
	
	protected ProbabilityTableByFrequencies getProbabilityTable(Character context) {
		if (context == null) {
			return new ProbabilityTableByFrequencies(new SimpleSuperChar(0), SuperChar.EOF);
		}
		
		ProbabilityTableByFrequencies returnValue = this.contexts.get(context);
		if (returnValue == null) {
			returnValue = new ProbabilityTableByFrequencies(new SimpleSuperChar(0), SuperChar.EOF);
			this.contexts.put(context, returnValue);
		}
		
		return returnValue;
	}

	public void cleanContexts() {
		this.contexts = new HashMap<Character, ProbabilityTableByFrequencies>();
	}
	
	public String getCompressorName() {
		return "ARITMÉTICO";
	}
}
