package ar.com.datos.compressor.ppmc.context;

import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SuperChar;

/**
 * Implementacion simple de un contexto para el compresor PPMC. 
 * @author marcos
 */
public class SimpleContext extends BaseContext {

	private ContextFactory factory;

	public SimpleContext(int order, ProbabilityTableByFrequencies table, 
			ContextFactory factory){
		super(order, table);
		this.factory = factory;
	}
	
	@Override
	public void addOcurrency(SuperChar ch) {
		// si no tengo existe un contexto de orden superior, lo creo.
		if (!ch.equals(SuperChar.ESC) && !this.nextContexts.containsKey(ch)){
			this.nextContexts.put(ch, 
					this.factory.createContextForOrder(this.order+1));
		}
		// agrego una ocurrencia del caracter que se queria emitir (incluso
		// aunque se haya emitido ESC)
		super.addOcurrency(ch);
	}
	@Override
	public String toString(){
		return "Context("+this.order+"): "+this.table.getCharacters();
	}
}
