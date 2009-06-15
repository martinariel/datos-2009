package ar.com.datos.compressor.ppmc;

import java.io.PrintStream;
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
import ar.com.datos.compressor.ppmc.context.Context;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.util.NullPrintStream;

public class PPMCCompressor extends AbstractPPMC{
	
	/** compresor aritmetico */
	private ArithmeticEmissor arithmetic;
	private PrintStream tracer;
		
	public PPMCCompressor(int order, PrintStream tracer) {
		if (order < 0 || order > 4){
			throw new RuntimeException("El rango de " +
				"orden permitido para el compresor PPMC es de 0 a 4");
		}
		this.tracer = tracer;
		this.compressorOrder = order;
		this.currentContext = new LinkedList<SuperChar>();
		this.exclusionSet = new HashSet<SuperChar>();
		this.constructContexts();
	}
	public PPMCCompressor(PrintStream tracer){
		this(DEFAULT_COMPRESSOR_ORDER, tracer);
	}
	public PPMCCompressor(int order){
		this(order, new NullPrintStream());
	}
	public PPMCCompressor(){
		this(DEFAULT_COMPRESSOR_ORDER);
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
		
		this.tracer.append("Compresion iniciada con PPMC de orden "+this.compressorOrder+"\n\n");
		
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
		
		this.tracer.append("Leo: "+((SimpleSuperChar)charToCompress).toAnotherString()+
				" en contexto:"+this.currentContext+"\n");
		
		// creo una copia del contexto actual (porque en las llamadas recursivas 
		// se va modificando)
		LinkedList<SuperChar> charContext = new LinkedList<SuperChar>(this.currentContext);
		
		// intento comprimir desde el contexto de orden 0, si no se pudo, pruebo
		// con el contexto de orden -1
		if (!this.processContext(this.zeroContext, charContext, charToCompress)){
			
			// obtengo la tabla de probabilidades del contexto de orden -1
			ProbabilityTableByFrequencies table = this.negativeContext.getProbabilityTable();
			
			// intento comprimir el caracter recibido
			SuperChar ch = this.arithmetic.compress(charToCompress, table);
			
			this.tracer.append("  Emitido: "+((SimpleSuperChar)ch).toAnotherString()+" (Orden:-1)\n"); 
			
			// agrego el caracter al set de exclusion del orden -1 porque
			// (estoy seguro que ya fue agregado al orden 0)
			table.addToExcludedSet(charToCompress);
		}
		// actualizo el contexto actual
		this.updateCurrentContext(charToCompress);
	}
	
	/**
	 * 
	 * @param context
	 * @param charContext
	 * @param charToCompress
	 * @return
	 */
	private boolean processContext(Context context, List<SuperChar> charContext, 
			SuperChar charToCompress){
		// si el contexto es null (esto sucede solo cuando se inicia la 
		// compresion, en los primeros pasos) no puedo emitir nada
		if (context == null){ 
			return false;
		}
		
		// Siempre debo ir hasta el ultimo contexto posible. 
		// Entro recursivamente, tratando de procesar los contextos de orden
		// superior. Si se pudo emitir el caracter en un contexto hijo, ya no 
		// debo hacer nada. Si no se pudo emtitir el caracter en un contexto 
		// hijo, debo intentar emitirlo desde el contexto de orden inferior.
		if (charContext.size() > context.getOrder()){
			if (!this.processContext(context.getNextContextFor(
					charContext.get(context.getOrder())), charContext, 
					charToCompress)){
				
				if (charContext.size() == 1){
					return this.processContext(this.zeroContext, 
							new LinkedList<SuperChar>(), charToCompress);
				} else {
					charContext.remove(0);
					return this.processContext(this.findContextFor(
							new LinkedList<SuperChar>(charContext)),
							charContext, charToCompress);
				}
			}
			// si pude comprimir el caracter, no actualizo nada mas
			return true;
		} else {
			// obtengo la tabla de probabilidades del contexto actual
			ProbabilityTableByFrequencies table = context.getProbabilityTable();

			// intento comprimir con el set de exclusion que recibo
			table.setExcludedSet(this.exclusionSet); 
			SuperChar ch = this.arithmetic.compress(charToCompress, table);

			this.tracer.append("  Emitido: "+((SimpleSuperChar)ch).toAnotherString()+
					" (Orden:"+context.getOrder()+")\n");
			
			// si se emitio un ESC debo agregar al set de exclusion, los
			// caracteres de esta tabla (que no se usaran en otros contextos).
			boolean wasESCEmitted = ch.equals(SuperChar.ESC);
			if (wasESCEmitted){
				Set<SuperChar> characters = table.getCharacters();
				this.exclusionSet.addAll(characters);
				this.exclusionSet.remove(SuperChar.ESC);
			}
			// sumo una ocurrencia al caracter DESPUES de haber emitido
			context.addOccurrence(charToCompress);
			// indico si pude comprimir el caracter leido.
			return !wasESCEmitted;
		}
	}	
}
