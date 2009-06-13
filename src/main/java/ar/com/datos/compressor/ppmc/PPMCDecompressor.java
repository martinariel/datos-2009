package ar.com.datos.compressor.ppmc;

import java.util.HashSet;
import java.util.Set;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticInterpreter;
import ar.com.datos.compressor.ppmc.context.BaseContext;
import ar.com.datos.compressor.ppmc.context.Context;
import ar.com.datos.compressor.ppmc.context.ContextFactory;
import ar.com.datos.documentlibrary.Document;

public class PPMCDecompressor {
	/** por defecto el orden del compresor es 4 */
	private static final int DEFAULT_COMPRESSOR_ORDER = 4;
	
	/** compresor aritmetico */
	private ArithmeticInterpreter arithmetic;
	
	/** el orden del compresor PPMC (valor entre 0 y 4) */
	private int compressorOrder;
	
	/** Array de super chars que contiene lo que se va leyendo del documento. 
	 *  Por ej "papagayo" en O(4) contendra "papa" "apag" "paga" "agay" "gayo"*/
	private SuperChar[] currentContext;
	
	/** contexto de orden 0 (los demas se consiguen a partir de este) */
	private Context zeroContext; 
	
	/** contexto de orden -1 */
	private Context negativeContext; 
	
	/** set con los caracteres a excluir al comprimir */
	private Set<SuperChar> exclusionSet;
	
	
	public PPMCDecompressor(int order) {
		if (order < 0 || order > 4){
			throw new RuntimeException("El rango de " +
				"orden permitido para el compresor PPMC es de 0 a 4");
		}
		this.compressorOrder = order;
		this.currentContext = new SuperChar[this.compressorOrder];
		this.exclusionSet = new HashSet<SuperChar>();
		this.constructContexts();
	}
	
	public PPMCDecompressor() {
		this(DEFAULT_COMPRESSOR_ORDER);
	}

	public Document decompress(InputBuffer input){
		this.arithmetic = new ArithmeticInterpreter(input);
		return null;
	}
	
	
	/**
	 *  
	 */
	private SuperChar decompress(){
		// uso un set de exclusion vacio, se ira llenando en el caso que sea 
		// necesario aplicar exclusion completa.
		this.exclusionSet.clear();
		
		if (!this.processContext(this.zeroContext, exclusionSet)){
			// debo comprimir con el contexto de orden -1 usando el set de 
			// exclusion que vengo arrastrando de contextos superiores.
			
			// obtengo la tabla de probabilidades del contexto de orden -1
			ProbabilityTableByFrequencies table = this.negativeContext.getProbabilityTable();
			
			// intento comprimir el caracter
			this.arithmetic.decompress(table);
			
			// agrego el caracter al set de exclusion del orden -1 porque
			// ya fue agregado al orden 0.
			//table.addToExcludedSet(charToCompress);
		}
		return null;
	}

	/**
	 * 
	 */
	private boolean processContext(Context context, Set<SuperChar> exclusionSet){
		// si el contexto es null (esto sucede solo cuando se inicia la 
		// compresion, en los primeros pasos) no puedo emitir nada
		if (context == null){ 
			return false;
		}
		
		if (this.processContext(context.getNextContextFor(
				this.currentContext[context.getOrder()]), exclusionSet)){
			return true;
		} else {
			// obtengo la tabla de probabilidades del contexto actual
			ProbabilityTableByFrequencies table = context.getProbabilityTable();

			// intento descomprimir con el exclusion set que recibo
			table.setExcludedSet(exclusionSet);
			SuperChar character = this.arithmetic.decompress(table);
			
			// sumo una ocurrencia al caracter DESPUES de haber leido
			context.addOcurrency(character);
			
			// si se leyo un ESC debo agregar al set de exclusion, los
			// caracteres de esta tabla (que no se usaran en otros contextos).
			if (character.equals(SuperChar.ESC)){
				Set<SuperChar> characters = table.getCharacters();
				characters.remove(SuperChar.ESC);
				this.exclusionSet.addAll(characters);
			}
			
			// indico si pude comprimir el caracter leido.
			//return wasESCEmitted;
		}
		return false;
	}
		
	/**
	 * Construye la estructura de contextos en base al orden del PPMC.
	 * Debe llamarse al inicio y dejar todo listo para 
	 * que se pueda comprimir/descomprimir. 
	 */
	private void constructContexts() {
		ContextFactory factory = new ContextFactory(this.compressorOrder);
		
		// creo el contexto de orden -1 (con un rango definido)
		this.negativeContext = new BaseContext(-1, 
				new ProbabilityTableByFrequencies(new SimpleSuperChar(0), SuperChar.EOF));
		
		// creo el contexto de orden 0 (sin un rango definido). Agrego ESC.
		this.zeroContext = factory.createContextForOrder(0);
	}
}
