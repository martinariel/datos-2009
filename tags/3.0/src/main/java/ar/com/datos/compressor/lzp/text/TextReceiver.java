package ar.com.datos.compressor.lzp.text;

/**
 * Interface para un receptor de texto.
 * 
 * @author fvalido
 */
public interface TextReceiver {
	/**
	 * Agrega el caracter pasado a continuación. 
	 */
	public void addChar(char character);
	
	/**
	 * Devuelve el texto guardado.
	 */
	public String getText();
}
