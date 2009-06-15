package ar.com.datos.compressor.lzp.table;



/**
 * Tabla de trabajo de contextos para el Lzp.
 * 
 * @author fvalido
 *
 */
public interface LzpContextWorkingTable {
	/**
	 * Agrega o reemplaza la informaci�n de un contexto con la informaci�n pasada.
	 */
	public void addOrReplace(LzpContext lzpContext, long position);
	
	/**
	 * Averigua la posici�n almacenada para el {@link LzpContext} pasado.
	 * 
	 * @return
	 * La posici�n, o null si no se lo encuentra.
	 */
	public Long getPosition(LzpContext lzpContext);
	
	/**
	 * Deja sin efecto la tabla actual.
	 */
	public void close();
}
