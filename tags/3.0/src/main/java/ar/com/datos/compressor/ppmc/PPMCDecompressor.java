package ar.com.datos.compressor.ppmc;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticInterpreter;
import ar.com.datos.compressor.ppmc.context.Context;
import ar.com.datos.util.NullPrintStream;

public class PPMCDecompressor extends AbstractPPMC{
	
	/** compresor aritmetico */
	private ArithmeticInterpreter arithmetic;
	private PrintStream tracer;
	
	public PPMCDecompressor(int order, PrintStream tracer) {
		if (order < 0 || order > 4){
			throw new RuntimeException("El rango de " +
				"orden permitido para el descompresor PPMC es de 0 a 4");
		}
		this.tracer = tracer;
		this.compressorOrder = order;
		this.currentContext = new LinkedList<SuperChar>();
		this.exclusionSet = new HashSet<SuperChar>();
		this.constructContexts();
	}
	public PPMCDecompressor(PrintStream tracer) {
		this(DEFAULT_COMPRESSOR_ORDER, tracer);
	}
	public PPMCDecompressor(int order) {
		this(order, new NullPrintStream());
	}
	
	public PPMCDecompressor() {
		this(DEFAULT_COMPRESSOR_ORDER, new NullPrintStream());
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public String decompress(InputBuffer input){
		// creo el aritmetico que se usara para descomprimir
		this.arithmetic = new ArithmeticInterpreter(input);

		StringBuffer buffer = new StringBuffer();

		this.tracer.append("\n\nDescompresion iniciada con PPMC de orden "+
				this.compressorOrder+"\n\n");
		
		SuperChar ch = this.decompress();
		
		// descomprimo  caracter a caracter hasta encontrar un EOF
		while(!ch.equals(SuperChar.EOF)){
			buffer.append(ch.charValue());
			ch = this.decompress();
		}
		this.tracer.append("\n\nEl resultado de la descompresion es:"+buffer.toString());
		return buffer.toString();
	}
	
	
	/**
	 * 
	 * @return
	 */
	private SuperChar decompress(){
		SuperChar ch;
		// uso un set de exclusion vacio, se ira llenando en el caso que sea 
		// necesario aplicar exclusion completa.
		this.exclusionSet.clear();
		
		// creo una copia del contexto actual (porque en las llamadas recursivas 
		// se va modificando)
		LinkedList<SuperChar> charContext = new LinkedList<SuperChar>(this.currentContext);
		
		ch = this.processContext(this.findContextFor(charContext), charContext);

		// actualizo el contexto actual
		this.updateCurrentContext(ch);
		
		return ch;
	}

	/**
	 * @param charContext 
	 * 
	 */
	private SuperChar processContext(Context context, LinkedList<SuperChar> charContext){
		SuperChar ch;
		
		// si el contexto es null (esto sucede solo cuando se inicia la 
		// descompresion, en los primeros pasos) no puedo emitir nada
		if (context == null){ 
			return null;
		}
		
		// obtengo la tabla de probabilidades del contexto actual
		ProbabilityTableByFrequencies table = context.getProbabilityTable();

		// intento descomprimir con el set de exclusion
		table.setExcludedSet(this.exclusionSet);
		ch = this.arithmetic.decompress(table);

		this.tracer.append("  Emitido: "+((SimpleSuperChar)ch).toAnotherString()+
				" (Orden:"+context.getOrder()+")\n");
		
		// si se emitio un ESC debo agregar al set de exclusion, los
		// caracteres de esta tabla (que no se usaran en otros contextos)
		if (ch.equals(SuperChar.ESC)){
			Set<SuperChar> characters = table.getCharacters();
			this.exclusionSet.addAll(characters);
			this.exclusionSet.remove(SuperChar.ESC);
			
			if (charContext.size() == 0){
				ch = this.processContext(this.negativeContext, charContext);
				this.negativeContext.getProbabilityTable().addToExcludedSet(ch);
			} else {
				LinkedList<SuperChar> x = new LinkedList<SuperChar>(charContext);
				x.remove(0);
				ch = this.processContext(this.findContextFor(x), x);
			}
		}
		context.addOccurrence(ch);
		
		return ch;
	}
	
}
