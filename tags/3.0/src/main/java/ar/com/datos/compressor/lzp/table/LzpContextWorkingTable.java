package ar.com.datos.compressor.lzp.table;



/**
 * Tabla de trabajo de contextos para el Lzp.
 * 
 * @author fvalido
 *
 */
public interface LzpContextWorkingTable {
	/**
	 * Agrega o reemplaza la información de un contexto con la información pasada.
	 */
	public void addOrReplace(LzpContext lzpContext, long position);
	
	/**
	 * Averigua la posición almacenada para el {@link LzpContext} pasado.
	 * 
	 * @return
	 * La posición, o null si no se lo encuentra.
	 */
	public Long getPosition(LzpContext lzpContext);
	
	/**
	 * Deja sin efecto la tabla actual.
	 */
	public void close();
}
