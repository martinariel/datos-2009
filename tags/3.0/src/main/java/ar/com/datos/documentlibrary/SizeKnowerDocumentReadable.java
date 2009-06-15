package ar.com.datos.documentlibrary;

/**
 * Interfase para conocer la cantidad de lineas de un documento y obtener una linea en particular.
 *
 * @author mfernandez
 *
 */
public interface SizeKnowerDocumentReadable {

    /**
     *
     * @return
     * Tamaño en cantidad de lineas
     */
    public int getLinesCount();

    /**
     *
     * @param position
     * Posicion de la linea
     * @return
     * Linea
     */
    public String getLineAtPosition (int position);
}
