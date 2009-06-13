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
     * Agrega una linea al final del documento
     */
    public abstract void addLine(String linea);

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
     *
     * @return
     */
    protected abstract SizeKnowerDocumentReadable getMultipleReadableDocument();

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !Document.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        // Esta implementación está pensada para los tests... Es lenta! Aunque quizás no haya otra mejor...

        Document o = (Document) obj;
        Iterator<Character> itMe = getCharacterIterator();
        Iterator<Character> itOther = o.getCharacterIterator();

        Character me, other = null;
        boolean areEquals = true;
        while (itMe.hasNext() && areEquals) {
            me = itMe.next();
            if (itOther.hasNext()) {
                other = itOther.next();
            } else {
                areEquals = false;
            }

            if (areEquals) {
                areEquals = me.equals(other);
            }
        }

        if (areEquals) {
            areEquals = !itOther.hasNext();
        }

        return areEquals;
    }

    /**
     * Iterator de Characters del documento.
     * TODO porque no puedo usar Iterator<char>???
     * @author mfernandez
     */
    class DocumentCharIterator implements Iterator<Character>{

        private String currentLine = null;
        private Character currentChar;
        private int initialPosition;
        private int relativePosition;
        private int currentPosition;
        private SizeKnowerDocumentReadable multipleReadble;
        private int linePosition;

        /**
         * Constructor
         * @param document
         * Documento a iterar.
         * @param initialPosition
         * Posicion inicial de caracteres.
         */
        public DocumentCharIterator ( Document document , int initialPosition ){

            this.multipleReadble 	= document.getMultipleReadableDocument();
            this.initialPosition 	= initialPosition;
            this.relativePosition	= 0;
            this.currentPosition	= -1;
            this.linePosition		= 0;

            moveFoward();
            moveToInitialPosition();            	
        }

        private String readLine(){
            if (this.linePosition < this.multipleReadble.getLinesCount() && this.linePosition >= 0){
                return this.multipleReadble.getLineAtPosition(this.linePosition++);

            }
            else {
                return null;
            }

        }

        private void moveToInitialPosition(){
             while (this.currentPosition < this.initialPosition)
                 this.next();
        }

        private void moveFoward(){

             if (this.currentLine == null || this.relativePosition + 1 > this.currentLine.length()){
                 this.currentLine = this.readLine();
                 this.relativePosition = 0;
                 while (this.currentLine != null && this.relativePosition + 1 > this.currentLine.length()) {
                     this.currentLine = this.readLine();
                     this.relativePosition = 0;
                 }
                 if (this.currentLine != null && this.linePosition < this.multipleReadble.getLinesCount()) 
                	 this.currentLine += "\n";
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
