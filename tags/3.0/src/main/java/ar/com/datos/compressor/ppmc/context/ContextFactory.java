package ar.com.datos.compressor.ppmc.context;

import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SuperChar;

public class ContextFactory {
	private int maxOrder;
	
	public ContextFactory(int maxOrder){
		this.maxOrder = maxOrder;
	}

	public Context createContextForOrder(int order){
		ProbabilityTableByFrequencies table = new ProbabilityTableByFrequencies();
		table.addOccurrence(SuperChar.ESC);
		if (order < this.maxOrder){
			return new SimpleContext(order, table, this);
		} else {
			return new BaseContext(order, table);
			
		}
	}
}
