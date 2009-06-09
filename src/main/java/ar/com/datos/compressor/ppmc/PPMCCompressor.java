package ar.com.datos.compressor.ppmc;

import java.util.HashSet;
import java.util.Set;

import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticEmissor;
import ar.com.datos.compressor.ppmc.context.BaseContext;
import ar.com.datos.compressor.ppmc.context.Context;
import ar.com.datos.compressor.ppmc.context.ContextFactory;

public class PPMCCompressor {
	
	private static final int DEFAULT_COMPRESSOR_ORDER = 4;
	
	/** compresor aritmetico */
	private ArithmeticEmissor arithmetic;
	
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

	public PPMCCompressor(int order) {
		if (order < 0 || order > 4){
			throw new RuntimeException("El rango de " +
				"orden permitido para el compresor PPMC es de 0 a 4");
		}
		this.compressorOrder = order;
		this.currentContext = new SuperChar[this.compressorOrder];
		this.exclusionSet = new HashSet<SuperChar>();
		this.constructContexts();
	}
	
	public PPMCCompressor(){
		this(DEFAULT_COMPRESSOR_ORDER);
	}

	/**
	 * Comprime un caracter leido.
	 * Dispara el proceso de recorrer recursivamente los contextos a partir del 
	 * orden 0 y utilizando un set de exclusion para aplicar exclusion completa
	 * cada contexto va emitiendo lo que corresponda.
	 * Si ningun contexto partiendo del 0 pudo emitir el caracter buscado, se
	 * emite el caracter desde el contexto de orden -1. En este caso, estamos
	 * seguro que si no se encontro el caracter en un orden superior, debe
	 * necesariamente estar en el orden -1.
	 *  
	 * @param ch el caracter a comprimir
	 */
	public void compress(SuperChar charToCompress){
		// uso un set de exclusion vacio, se ira llenando en el caso que sea 
		// necesario aplicar exclusion completa.
		this.exclusionSet.clear();
		
		if (!this.processContext(this.zeroContext, charToCompress, exclusionSet)){
			// debo comprimir con el contexto de orden -1 usando el set de 
			// exclusion que vengo arrastrando de contextos superiores.
			
			// obtengo la tabla de probabilidades del contexto de orden -1
			ProbabilityTableByFrequencies table = this.negativeContext.getProbabilityTable();
			
			// intento comprimir el caracter
			this.arithmetic.compress(charToCompress, table);
			
			// agrego el caracter al set de exclusion del orden -1 porque
			// ya fue agregado al orden 0.
			table.addToExcludedSet(charToCompress);
		}
	}

	/**
	 * Procesa los contextos intentando comprimir un caracter, parte de un 
	 * contexto base (por ej. el contexto de orden 0) y va "entrando" en los
	 * contextos hijos de orden superior para tratar de encontrar el mejor
	 * contexto con el cual emitir el caracter a comprimir.
	 *  
	 * @param context el contexto a procesar
	 * @param charToCompress el caracter a emitir
	 * @param exclusionSet el set de exclusion con los caracteres que no se 
	 * 		  deben considerar al calcular las probabilidades.
	 * @return true si se pudo emitir el caracter en este contexto o en un contexto 
	 * 		   hijo; false si se emitio ESC en este y los contextos hijos.
	 */
	private boolean processContext(Context context, SuperChar charToCompress, 
			Set<SuperChar> exclusionSet){
		// si el contexto es null (esto sucede solo cuando se inicia la 
		// compresion, en los primeros pasos) no puedo emitir nada
		if (context == null){ 
			return false;
		}
		
		// Siempre debo ir hasta el ultimo contexto posible. 
		// Entro recursivamente, tratando de procesar los contextos de orden
		// superior. Si se pudo emitir el caracter en un contexto hijo, ya no 
		// debo hacer nada. 
		// Si no se pudo emtitir el caracter en un contexto hijo, debo intentar
		// emitirlo desde este contexto.
		if (this.processContext(context.getNextContextFor(
				this.currentContext[context.getOrder()]), charToCompress, exclusionSet)){
			
			// segun los ejemplos de la pagina cuando se emite en algun orden,
			// los contextos de orden inferior quedan intactos :S
			return true;
		} else {
			// obtengo la tabla de probabilidades del contexto actual
			ProbabilityTableByFrequencies table = context.getProbabilityTable();

			// intento comprimir con el exclusion set que recibo
			table.setExcludedSet(exclusionSet);
			SuperChar character = this.arithmetic.compress(charToCompress, table);
			
			// sumo una ocurrencia al caracter DESPUES de haber emitido
			context.addOcurrency(charToCompress);
			
			// si se emitio un ESC debo agregar al set de exclusion, los
			// caracteres de esta tabla (que no se usaran en otros contextos).
			boolean wasESCEmitted = character.equals(SuperChar.ESC); 
			if (wasESCEmitted){
				// this.exclusionSet.addAll(table.getCharacters());
			}
			// indico si pude comprimir el caracter leido.
			return wasESCEmitted;
		}
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
	
	public void setArithmeticCompressor(ArithmeticEmissor arithmetic){
		this.arithmetic = arithmetic;
	}
	
}
