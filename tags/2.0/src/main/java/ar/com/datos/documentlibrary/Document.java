package ar.com.datos.documentlibrary;

/**
 * Interface de documentos, de esta manera vemos de forma generica cualquier
 * tipo de documento: FileSystem, Memory, etc.
 * @author mfernandez
 *
 */
public interface Document {
    /**
     *
     * @return String linea del documento.
     * null en el caso de fin de archivo
     */
    public String readLine();

    /**
     * Abre el documento
     *
     */
    public void open();

    /**
     * Cierra el documento
     *
     */
    public void close();

    /**
     *
     * @return
     * Boolean indicando si puede abrir el documento
     */
    public boolean canOpen();
}
