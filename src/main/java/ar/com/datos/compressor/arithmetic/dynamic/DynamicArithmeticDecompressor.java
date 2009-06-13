package ar.com.datos.compressor.arithmetic.dynamic;

import java.util.HashMap;
import java.util.Map;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticInterpreter;

public class DynamicArithmeticDecompressor {
	/** Contextos del modelo */
	private Map<Character, ProbabilityTableByFrequencies> contexts;

	public DynamicArithmeticDecompressor() {
		cleanContexts();
	}
	
	public void decompress(InputBuffer input, OutputBuffer output) {
		ArithmeticInterpreter interpreter = new ArithmeticInterpreter(input);
		SuperChar current = this.decompressInner(null, interpreter);;
		Character context = current.charValue();
		while (!SimpleSuperChar.EOF.equals(current)) {
			this.write(output,current);
			current = this.decompressInner(context, interpreter);
			context = current.charValue();
		}
		interpreter.close();
	}

	protected void write(OutputBuffer output, SuperChar current) {
		char aChar = current.charValue();
		output.write((byte)(aChar));
		output.write((byte)(aChar >> 8));
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
}
