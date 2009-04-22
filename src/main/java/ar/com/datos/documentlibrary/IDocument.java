package ar.com.datos.documentlibrary;

/**
 * Interface de documentos, de esta manera vemos de forma generica cualquier
 * tipo de documento: FileSystem, VariableLength
 * @author mfernandez
 *
 */
public interface IDocument {
    /**
     *
     * @return String linea del documento
     */
    String readLine();


    /**
     * Abre el documento
     *
     */
    void open();

    /**
     * Cierra el documento
     *
     */
    void close();
}
