package ar.com.datos.compressor.ppmc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticEmissor;
import ar.com.datos.compressor.ppmc.context.BaseContext;
import ar.com.datos.compressor.ppmc.context.Context;
import ar.com.datos.compressor.ppmc.context.ContextFactory;
import ar.com.datos.documentlibrary.Document;

public class PPMCCompressor {
	
	/** por defecto el orden del compresor es 4 */
	private static final int DEFAULT_COMPRESSOR_ORDER = 4;
	
	/** compresor aritmetico */
	private ArithmeticEmissor arithmetic;
	
	/** el orden del compresor PPMC (valor entre 0 y 4) */
	private int compressorOrder;
	
	/** Array de super chars que contiene lo que se va leyendo del documento. 
	 *  Por ej "papagayo" en O(4) contendra "papa" "apag" "paga" "agay" "gayo"*/
	private LinkedList<SuperChar> currentContext;
	
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
		this.currentContext = new LinkedList<SuperChar>();
		this.exclusionSet = new HashSet<SuperChar>();
		this.constructContexts();
	}
	
	public PPMCCompressor(){
		this(DEFAULT_COMPRESSOR_ORDER);
	}

	/**
	 * Comprime un caracter leido.
	 * Dispara el proceso de recorrer recursivamente los contextos a partir del 
	 * orden 0 y utilizando un set de exclusion para aplicar exclusion completa.
	 * Cada contexto trata de emitir el caracter y si no puede, emite ESC.
	 * 
	 * Si ningun contexto partiendo del 0 pudo emitir el caracter buscado, se
	 * emite el caracter desde el contexto de orden -1. Estamos seguro que si no
	 * se encontro el caracter en un orden superior, debe estar en el orden -1.
	 *  
	 * @param charToCompress el caracter a comprimir
	 */
	private void compress(SuperChar charToCompress){
		// uso un set de exclusion vacio que se ira llenando en el caso que sea 
		// necesario aplicar exclusion completa
		this.exclusionSet.clear();
		
		// creo una copia del contexto actual (porque en las llamadas recursivas 
		// se va modificando)
		LinkedList<SuperChar> charContext = new LinkedList<SuperChar>(this.currentContext);
		
		// intento comprimir desde el contexto de orden 0, si no se pudo, pruebo
		// con el contexto de orden -1
		if (!this.processContext(this.zeroContext, charContext, charToCompress, exclusionSet)){
			
			// obtengo la tabla de probabilidades del contexto de orden -1
			ProbabilityTableByFrequencies table = this.negativeContext.getProbabilityTable();
			
			// intento comprimir el caracter recibido
			SuperChar character = this.arithmetic.compress(charToCompress, table);
			
			// agrego el caracter al set de exclusion del orden -1 porque
			// (estoy seguro que ya fue agregado al orden 0)
			table.addToExcludedSet(charToCompress);
		}
		// estas ultimas lineas actualizan cual sera el contexto siguiente.
		// Por ejemplo para "COMPRESOR" (usando un PPMC de orden 4)
		// [C,O,M,P] ===pasara a ser===> [O,M,P,R]
		// para los primeros caso, no remueve el del principio, solo agrega:
		// [C,O,null,null] ===pasara a ser===> [C,O,M,null]
		if (this.currentContext.size() == this.compressorOrder){
			this.currentContext.poll();
		}
		this.currentContext.offer(charToCompress);
	}

	/**
	 * Comprime un documento utilizando el compresor PPMC del orden especificado.
	 * Itera sobre todos los caracteres del documento, comprimiendo uno por uno,
	 * y al final comprime un EOF y cierra el compresor aritmetico para que 
	 * pueda emitir los ultimos bits.
	 *  
	 * @param document el documento a comprimir
	 * @param output el OutputBuffer donde se emitira lo comprimido
	 */
	public void compress(Document document, OutputBuffer output){
		// creo un comresor aritmetico con el OuputBuffer recibido
		this.arithmetic = new ArithmeticEmissor(output);
		
		// itero sobre los caracteres del documento a comprimir
		Iterator<Character> it = document.getCharacterIterator();
		while (it.hasNext()){
			Character ch = it.next();
			// comprimo este caracter
			this.compress(new SimpleSuperChar(ch));
		}
		// finalmente comprimo el end-of-file
		this.compress(SuperChar.EOF);
		
		// cierro el aritmetico para que emita los ultimos bits
		this.arithmetic.close();
	}
	
	/**
	 * 
	 * @param context
	 * @param charContext
	 * @param charToCompress
	 * @param exclusionSet
	 * @return
	 */
	private boolean processContext(Context context, List<SuperChar> charContext, 
			SuperChar charToCompress, Set<SuperChar> exclusionSet){
		
		if (context == null){ 
			return false;
		}
		
		if (charContext.size() > context.getOrder()){
			if (!this.processContext(context.getNextContextFor(
					charContext.get(context.getOrder())), charContext, 
					charToCompress,exclusionSet)){
				
				if (charContext.size() == 1){
					return this.processContext(this.zeroContext, new LinkedList<SuperChar>(), charToCompress, exclusionSet);
				} else {
					charContext.remove(0);
					return this.processContext(this.findContextFor(new LinkedList<SuperChar>(charContext)),
						charContext, charToCompress, exclusionSet);
				}
			}
			return true;
		} else {
			ProbabilityTableByFrequencies table = context.getProbabilityTable();

			table.setExcludedSet(exclusionSet); 
			SuperChar character = this.arithmetic.compress(charToCompress, table);

			boolean wasESCEmitted = character.equals(SuperChar.ESC);
			if (wasESCEmitted){
				Set<SuperChar> characters = table.getCharacters();
				this.exclusionSet.addAll(characters);
				this.exclusionSet.remove(SuperChar.ESC);
			}
			context.addOcurrency(charToCompress);
			return !wasESCEmitted;
		}
	}

	/**
	 * Busca un contexto partiendo del contexto de orden 0, en base a una list
	 * de SuperChar que represente el contexto a buscar, por ej. que contenga:
	 * [T,A,T,A] busca el contexto de orden 4 para "TATA"
	 * 
	 * Llama a una funcion que busca el contexto recursivamente.
	 * 
	 * @param charContext el contexto a buscar como List<SuperChar>
	 * @return el contexto buscado o null si no se encuentra.
	 */
	private Context findContextFor(List<SuperChar> charContext){
		return this.findContext(charContext, this.zeroContext);
	}

	/**
	 * Funcion que busca un contexto recursivamente en base al contexto recibido
	 * y a la list de SuperChar que representa el contexto a buscar.
	 * @param charContext charContext el contexto a buscar como List<SuperChar>
	 * @param context el Context desde el cual buscar
	 * @return el contexto buscado o null si no se encuentra.
	 */
	private Context findContext(List<SuperChar> charContext, Context context){
		if (charContext.size() == 1){
			return context.getNextContextFor(charContext.get(0));
		} else if (charContext.size() == 0){
			return null;
		}
		SuperChar ch = charContext.remove(0);
		return findContext(charContext, context.getNextContextFor(ch));
	}
		
	/**
	 * Construye la estructura de contextos en base al orden del PPMC.
	 * Debe llamarse en el constructor y dejar todo listo para que se pueda  
	 * comprimir/descomprimir. 
	 */
	private void constructContexts() {
		ContextFactory factory = new ContextFactory(this.compressorOrder);
		
		// creo el contexto de orden -1 (con un rango definido)
		this.negativeContext = new BaseContext(-1, 
				new ProbabilityTableByFrequencies(new SimpleSuperChar(0), 
						SuperChar.EOF));
		
		// creo el contexto de orden 0 (sin un rango definido). Agrego ESC.
		this.zeroContext = factory.createContextForOrder(0);
	}	
}
