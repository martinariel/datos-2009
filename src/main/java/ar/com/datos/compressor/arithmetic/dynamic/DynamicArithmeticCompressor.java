package ar.com.datos.compressor.arithmetic.dynamic;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.FileCompressor;
import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticEmissor;
import ar.com.datos.compressor.arithmetic.traceable.ArithmeticEmissorWithTrace;
import ar.com.datos.documentlibrary.Document;

/**
 * Implementación de un compresor dinámico de orden 1
 * @author jbarreneche
 *
 */
public class DynamicArithmeticCompressor implements FileCompressor {
	/** Contextos del modelo */
	private Map<Character, ProbabilityTableByFrequencies> contexts;
	private PrintStream tracer;
	public DynamicArithmeticCompressor() {
		cleanContexts();
	}
	public DynamicArithmeticCompressor(PrintStream out) {
		this();
		this.tracer = out;
	}
	public void compress(Document documento, OutputBuffer output) {
		Iterator<Character> iterator = documento.getCharacterIterator();
		ArithmeticEmissor emissor = constructEmissor(output);
		Character context = null;
		Character aChar = null;
		
		while (iterator.hasNext()) {
			aChar = iterator.next();
			this.compress(context, aChar, emissor);
			context = aChar;
		}
		this.compress(context, SimpleSuperChar.EOF, emissor);
		emissor.close();
	}
	private ArithmeticEmissor constructEmissor(OutputBuffer output) {
		return this.tracer == null? new ArithmeticEmissor(output) : new ArithmeticEmissorWithTrace(output, tracer);
	}
	
	public void compress(Character context, SuperChar aSuperChar, ArithmeticEmissor emissor) {
		ProbabilityTableByFrequencies table = getProbabilityTable(context);
		emissor.compress(aSuperChar, table);
		table.addOccurrence(aSuperChar);
	}
	
	public void compress(Character context, Character aChar, ArithmeticEmissor emissor) {
		this.compress(context, new SimpleSuperChar(aChar), emissor);
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
