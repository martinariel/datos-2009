package ar.com.datos.compressor.ppmc;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.ppmc.context.BaseContext;
import ar.com.datos.compressor.ppmc.context.Context;
import ar.com.datos.compressor.ppmc.context.ContextFactory;

public class AbstractPPMC {
		
	/** por defecto el orden del compresor es 4 */
	protected static final int DEFAULT_COMPRESSOR_ORDER = 4;
	
	/** el orden del compresor PPMC (valor entre 0 y 4) */
	protected int compressorOrder;
	
	/** Array de super chars que contiene lo que se va leyendo del documento. 
	 *  Por ej "papagayo" en O(4) contendra "papa" "apag" "paga" "agay" "gayo"*/
	protected LinkedList<SuperChar> currentContext;
	
	/** contexto de orden 0 (los demas se consiguen a partir de este) */
	protected Context zeroContext; 
	
	/** contexto de orden -1 */
	protected Context negativeContext; 
	
	/** set con los caracteres a excluir al comprimir */
	protected Set<SuperChar> exclusionSet;
		
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
	protected Context findContextFor(List<SuperChar> charContext){
		return this.findContext(new LinkedList<SuperChar>(charContext), this.zeroContext);
	}

	/**
	 * Funcion que busca un contexto recursivamente en base al contexto recibido
	 * y a la list de SuperChar que representa el contexto a buscar.
	 * @param charContext charContext el contexto a buscar como List<SuperChar>
	 * @param context el Context desde el   cual buscar
	 * @return el contexto buscado o null si no se encuentra.
	 */
	protected Context findContext(List<SuperChar> charContext, Context context){
		if (charContext.size() == 1){
			return context.getNextContextFor(charContext.get(0));
		} else if (charContext.size() == 0){
			return context;
		}
		SuperChar ch = charContext.remove(0);
		return findContext(charContext, context.getNextContextFor(ch));
	}
	
	/**
	 * Cctualizan cual sera el contexto siguiente.
	 * Por ejemplo para "COMPRESOR" (usando un PPMC de orden 4)
	 * [C,O,M,P] ===pasara a ser===> [O,M,P,R]
	 * para los primeros caso, no remueve el del principio, solo agrega:
	 * [C,O,null,null] ===pasara a ser===> [C,O,M,null]
	 * 
	 * @param ch el nuevo character 
	 */
	protected void updateCurrentContext(SuperChar ch){
		if (this.currentContext.size() == this.compressorOrder){
			this.currentContext.poll();
		}
		this.currentContext.offer(ch);
	}
	
	/**
	 * Construye la estructura de contextos en base al orden del PPMC.
	 * Debe llamarse en el constructor y dejar todo listo para que se pueda  
	 * comprimir/descomprimir. 
	 */
	protected void constructContexts() {
		ContextFactory factory = new ContextFactory(this.compressorOrder);
		
		// creo el contexto de orden -1 (con un rango definido)
		this.negativeContext = new BaseContext(-1, 
				new ProbabilityTableByFrequencies(new SimpleSuperChar(0), 
						SuperChar.EOF));
		
		// creo el contexto de orden 0 (sin un rango definido). Agrego ESC.
		this.zeroContext = factory.createContextForOrder(0);
	}	

}
