package ar.com.datos.util;

/**
 * 
 * @author fvalido
 *
 */
public class ArraysUtils {
    /**
     * Incrementa si es necesario la cantidad de Bytes que pueden
     * ser guardados en el array.
     *
     * Macheteado de ArrayList :P  (Pero tambien adaptado)
     */
    public static byte[] ensureCapacity(byte[] currentBytes, int size) {
    	int oldSize = currentBytes.length;
    	if (size > oldSize) {
			int newSize = (oldSize * 3) / 2 + 1;
		    if (newSize < size) {
		    	newSize = size;
		    }
		    byte[] newBytes = new byte[newSize];
		    System.arraycopy(currentBytes, 0, newBytes, 0, currentBytes.length);
		    currentBytes = newBytes;
		}
    	
    	return currentBytes;
    }
}
