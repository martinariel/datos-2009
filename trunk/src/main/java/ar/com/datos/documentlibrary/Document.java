package ar.com.datos.documentlibrary;

import java.util.Iterator;

/**
 * Clase abstracta de documentos, de esta manera vemos de forma generica cualquier
 * tipo de documento: FileSystem, Memory, etc.
 * @author mfernandez
 *
 */
public abstract class Document {
    /**
     *
     * @return String linea del documento.
     * null en el caso de fin de archivo
     */
    public abstract String readLine();

    /**
     * Abre el documento
     *
     */
    public abstract void open();

    /**
     * Cierra el documento
     *
     */
    public abstract void close();

    /**
     *
     * @return
     * Boolean indicando si puede abrir el documento
     */
    public abstract boolean canOpen();

    /**
     * @return
     * Iterator de caracteres del documento.
     */
    public Iterator<Character> getCharacterIterator() {
        return new DocumentCharIterator(this);
    }

    /**
     * @param initialPosition
     * Posicion inicial en numero de caracter del iterator.
     * @return
     * Iterator de caracteres del documento desde initialPosition
     */
    public Iterator<Character> getCharacterIterator(int initialPosition){
        return new DocumentCharIterator(this, initialPosition);
    }

    /**
     * Iterator de Characters del documento.
     * TODO porque no puedo usar Iterator<char>???
     * @author mfernandez
     */
    class DocumentCharIterator implements Iterator<Character>{

        private Document document;
        private String currentLine;
        private int initialPosition;

        /**
         * Constructor
         * @param document
         * Documento a iterar.
         */
        public DocumentCharIterator ( Document document ) {
            this.document 			= document;
            this.initialPosition 	= 0;
        }

        /**
         * Constructor
         * @param document
         * Documento a iterar.
         * @param initialPosition
         * Posicion inicial de caracteres.
         */
        public DocumentCharIterator ( Document document, int initialPosition){
            this.document 			= document;
            this.initialPosition 	= initialPosition;
        }

        public boolean hasNext() {
            // TODO Implementar
            return false;
        }

        public Character next() {
            // TODO Implementar
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }


    }

}
