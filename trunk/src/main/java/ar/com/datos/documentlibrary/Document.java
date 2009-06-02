package ar.com.datos.documentlibrary;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase abstracta de documentos, de esta manera vemos de forma generica cualquier
 * tipo de documento: FileSystem, Memory, etc.
 * @author mfernandez
 *
 */
public abstract class Document {

    /**
     * @return String linea del documento.
     * null en el caso de fin de archivo
     */
    public abstract String readLine();

    /**
     * Abre el documento
     */
    public abstract void open();

    /**
     * Cierra el documento
     */
    public abstract void close();

    /**
     * @return
     * Boolean indicando si puede abrir el documento
     */
    public abstract boolean canOpen();

    /**
     * @return
     * Iterator de caracteres del documento.
     */
    public Iterator<Character> getCharacterIterator() {
        return new DocumentCharIterator(this , 0);
    }

    /**
     * @param initialPosition
     * Posicion inicial en numero de caracter del documento, inicio del documento = 0.
     * es decir, si quiere el iterator a partir del segundo caracter initialPosition debe ser 1.
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
        private String currentLine = null;
        private Character currentChar;
        private int initialPosition;
        private int relativePosition;
        private int currentPosition;

        /**
         * Constructor
         * @param document
         * Documento a iterar.
         * @param initialPosition
         * Posicion inicial de caracteres.
         */
        public DocumentCharIterator ( Document document , int initialPosition ){
            this.document 			= document;
            this.initialPosition 	= initialPosition;
            this.relativePosition	= 0;
            this.currentPosition	= -1;

            moveFoward();
            moveToInitialPosition();

        }

        private void moveToInitialPosition(){
             while (this.currentPosition < this.initialPosition)
                 this.next();
        }

        private void moveFoward(){

             if (this.currentLine == null || this.relativePosition + 1 > this.currentLine.length()){
                 this.currentLine = this.document.readLine();
                 this.relativePosition = 0;
             }

             this.currentPosition++;

             if (this.currentLine != null){
                 this.currentChar = this.currentLine.charAt(this.relativePosition++);
             }
             else {
                 this.currentChar = null;
             }
        }

        @Override
        public boolean hasNext() {
            return this.currentChar != null;
        }

        @Override
        public Character next() {
            Character returnValue;
            if (this.currentChar == null)
                throw new NoSuchElementException();

            returnValue = this.currentChar;
            this.moveFoward();

            return returnValue;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
