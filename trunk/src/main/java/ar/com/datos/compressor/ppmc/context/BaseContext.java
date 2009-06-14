/**
 * 
 */
package ar.com.datos.compressor.ppmc.context;

import java.util.HashMap;

import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SuperChar;

/**
 * @author marcos
 *
 */
public class BaseContext implements Context {

	protected int order;
	protected ProbabilityTableByFrequencies table;
	protected HashMap<SuperChar, Context> nextContexts;
	
	public BaseContext(int order, ProbabilityTableByFrequencies table){
		this.order = order;
		this.table = table;
		this.nextContexts = new HashMap<SuperChar, Context>();
	}
	
	/* (non-Javadoc)
	 * @see ar.com.datos.compressor.ppmc.context.Context#addOcurrency(ar.com.datos.compressor.SuperChar)
	 */
	@Override
	public void addOccurrence(SuperChar ch){
		this.table.addOccurrence(ch);
	}

	/* (non-Javadoc)
	 * @see ar.com.datos.compressor.ppmc.context.Context#getNextContextFor(ar.com.datos.compressor.SuperChar)
	 */
	@Override
	public Context getNextContextFor(SuperChar ch) {
		return this.nextContexts.get(ch);
	}

	/* (non-Javadoc)
	 * @see ar.com.datos.compressor.ppmc.context.Context#getOrder()
	 */
	@Override
	public int getOrder() {
		return this.order;
	}

	/* (non-Javadoc)
	 * @see ar.com.datos.compressor.ppmc.context.Context#getProbabilityTable()
	 */
	@Override
	public ProbabilityTableByFrequencies getProbabilityTable() {
		return this.table;
	}

}
