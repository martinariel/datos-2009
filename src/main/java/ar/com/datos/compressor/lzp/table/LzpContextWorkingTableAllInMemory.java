package ar.com.datos.compressor.lzp.table;

import java.util.HashMap;
import java.util.Map;

import ar.com.datos.util.UnsignedInt;


/**
 * Tabla de trabajo de contextos para el Lzp.
 * 
 * Notas de implemntación:
 * Toda en memoria. No requerida por el TP. (Y no se utiliza en él).
 * 
 * @author fvalido
 */
public class LzpContextWorkingTableAllInMemory implements LzpContextWorkingTable {
	/** Tabla de trabajo en memoria. */
	private Map<LzpContext, UnsignedInt> workingTable;

	/**
	 * Constructor.
	 */
	public LzpContextWorkingTableAllInMemory() {
		this.workingTable = new HashMap<LzpContext, UnsignedInt>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.lzp.table.LzpContextWorkingTable#addOrReplace(ar.com.datos.compressor.lzp.table.LzpContext, long)
	 */
	@Override
	public void addOrReplace(LzpContext lzpContext, long position) {
		this.workingTable.put(lzpContext, new UnsignedInt(position));
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.lzp.table.LzpContextWorkingTable#getPosition(ar.com.datos.compressor.lzp.table.LzpContext)
	 */
	@Override
	public Long getPosition(LzpContext lzpContext) {
		UnsignedInt position = this.workingTable.get(lzpContext);
		
		return (position == null) ? null : position.longValue();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.lzp.table.LzpContextWorkingTable#close()
	 */
	@Override
	public void close() {
	}
}
