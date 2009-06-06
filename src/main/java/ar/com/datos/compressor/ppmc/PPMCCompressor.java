package ar.com.datos.compressor.ppmc;

import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticEmissor;

public class PPMCCompressor {
	private static final int DEFAULT_COMPRESSOR_ORDER = 4;
	private ArithmeticEmissor arithmetic; // el compresor aritmetico
	private int compressorOrder;
	private SuperChar[] currentContext;
	private Context baseContext; // contexto de orden -1
	private Context firstContext; // contexto de orden 0

	public PPMCCompressor(int order, ArithmeticEmissor arithmetic) {
		if (order < 0 || order > 4){
			throw new RuntimeException("El rango de " +
				"orden permitido para el compresor PPMC es de 0 a 4");
		}
		this.compressorOrder = order;
		this.arithmetic = arithmetic;
		this.currentContext = new SuperChar[this.compressorOrder];
		this.constructContexts();
	}
	
	public PPMCCompressor(ArithmeticEmissor arithmetic){
		this(DEFAULT_COMPRESSOR_ORDER, arithmetic);
	}

	public void compress(SuperChar ch){
		
	}

	// XXX: METHOD UNDER CONSTRUCTION :P
	private boolean processContext(Context context, SuperChar charToCompress, int order){
		// recibo el contexto base (por ej. el contexto 0)
		//  {
		//   	entro recursivamente a los contextos siguientes (que emitiran 
		//      primero) y por ultimo se vuelve al contexto base para emitir.
		//  }
		//  emito este contexto
		//
		if (context == null){
			return false;
		}
		ProbabilityTable table;
		SuperChar currentChar = this.currentContext[order];
		if (currentChar != null){
			if (processContext(context.getNextContextFor(currentChar), charToCompress, order+1)){
				return true;
			} else {
				table = context.getProbabilityTable();
				if (table.contains(currentChar)){
					// WTF?? porque el aritmetico depende de SimpleSuperChar?
					//this.arithmetic.compress(charToCompress, table);
					return true;
				} else {
					//this.arithmetic.compress(SuperChar.ESC, table);
					return false;
				}
			}
		}
		return false;
	}
	private void emit(){
		
	}
	
	/**
	 * Encuentra el contexto adecuado para un determinado caracter
	 * @param ch
	 * @return
	 */
	private Context findContextFor(SuperChar ch) {
		/*
		Context context;
		SuperChar c = this.currentContext[0];
		context = this.firstContexts.get(ch);
		while (c != null){
		
		} else {

		}
		*/
		return baseContext;
	}
	
	/**
	 * Construye la estructura de contextos en base al orden del PPMC.
	 * Debe llamarse al crear un {@link PPMCSerializer} y dejar todo listo para que se 
	 * pueda comprimir/descomprimir. 
	 */
	private void constructContexts() {
		for (int i = 0; i < this.currentContext.length; i++) {
			this.currentContext[i] = null;
		}
	}
}
