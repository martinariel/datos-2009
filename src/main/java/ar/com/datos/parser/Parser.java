package ar.com.datos.parser;

import java.util.Iterator;
import ar.com.datos.documentlibrary.Document;
import java.util.List;
import java.util.LinkedList;

/**
 * Parser
 *
 * Recibe un Document y devuelve a traves del Iterator las oraciones completas del mismo.
 *
 * @author mfernandez
 *
 */
public class Parser implements Iterable<List<String>> {

    private Document document;
    private boolean started;
    private String bufferedLine = "";
    private static String[] separators = null;
    private static String stringRegex;

    static {

        separators = new String[] {"?", ";", ".", "!", ":", "(", ")", "\"", "'", ",", "-"};

        //Reemplazar todos los caracteres excepto:
        stringRegex = "[^a-z\\�";

        //Completo el regex con los separadores, es decir, no seran removidos
        for (int i = 0; i < separators.length; i++){
            stringRegex += "\\" + separators[i];
        }

        //Cierro el Regex
        stringRegex += "]";

    }

    public Parser(Document documento){
        this.document = documento;
        started = false;
    }


    /**
     *
     * @return estado de inicializacion
     */
    public boolean isStarted(){
        return started;
    }

    /**
     * Inicia el parser
     *
     */
    public void initParser(){
        if (!started && document.canOpen()){
            started = true;
            document.close();
            document.open();
        }
    }

    /**
     * Finaliza el parser
     *
     */
    public void endParser(){
        if (started){
            started = false;
            document.close();
        }
    }

    /**
     *
     * @param linea
     * @return linea sin tildes
     */
    private static String limpiarTildes(String linea){

        linea = linea.replaceAll("�", "a");
        linea = linea.replaceAll("�", "e");
        linea = linea.replaceAll("�", "i");
        linea = linea.replaceAll("�", "o");
        linea = linea.replaceAll("�", "u");

        return linea;
    }

    /**
     *
     * @param linea
     * @return linea con solo caracteres del abecedario y los separadores
     */
    private static String cleanRegex(String linea){

        linea = linea.replaceAll(stringRegex, " ");
        linea = linea.replaceAll("  ", " ");
        return linea;
    }

    private static String cleanLine(String line){
        return cleanRegex(limpiarTildes(line.toLowerCase())).trim();

    }

    /**
     *
     * @param linea
     * @return Posicion en el string del primer separador encontrado
     */
    private static int findSeparator(String linea){
        int indice = -1;

        if (linea.length() > 0){
        	int[] posiciones = new int[separators.length];
        
        	for (int i = 0; i < posiciones.length; i++){
        		posiciones[i] = linea.indexOf(separators[i]);
        		//WTF??
        		if ((posiciones[i] > -1 && posiciones[i] < indice) || indice == -1)
        			indice = posiciones[i];
        	}
        }
        return indice;
    }


    private List<String> getCurrentWords(){
        List<String> words = null;

        if (isStarted()){

            String linea 			= bufferedLine.trim(); //Inicializo en el pedazo anterior
            String lineaArchivo		= null;
            String lineaResultado 	= null;

            int posicionSeparator = -1;

            do {
                
            	if (posicionSeparator == 0) linea = linea.substring(1).trim();
            	
            	//Busco el primer separador
                posicionSeparator = findSeparator(linea);

                if (posicionSeparator < 0){
                    lineaArchivo = document.readLine();
                    if (lineaArchivo != null){
                        linea += (linea.length() > 0)? " " + cleanLine(lineaArchivo) : cleanLine(lineaArchivo);
                        linea = linea.trim();
                    }
                }

            }
            while ( (posicionSeparator < 0 && lineaArchivo != null) || posicionSeparator == 0 );

            if (posicionSeparator > 0){
                lineaResultado = linea.substring(0, posicionSeparator);
                bufferedLine   = linea.substring(posicionSeparator+1);
            }
            else {
                lineaResultado = linea;
                bufferedLine = "";
            }

            if (lineaResultado.trim().length() > 0){
            	String[] vector = lineaResultado.trim().split(" ");
            	words = new LinkedList<String>();
            	for (int i = 0; i < vector.length ; i++){
            		if (vector[i].trim().length() > 0)
            			words.add(vector[i].trim());
            	}
            }
            
        }

        return words;
    }

    public Iterator<List<String>> iterator(){
        return new ParserLineIterator(this);
    }

    /**
     * Parser Iterator
     *
     * @author mfernandez
     *
     */
    private class ParserLineIterator implements Iterator<List<String>> {

        private Parser parser;
        private List<String> words;

        public ParserLineIterator(Parser parser){
            this.parser = parser;
        }

        public boolean hasNext() {
            if (!parser.isStarted()) parser.initParser();
            words = parser.getCurrentWords();

            boolean resultado = (words != null);

            if (!resultado){
                parser.endParser();
            }

            return resultado;
        }

        public List<String> next() {
            return words;
        }

        public void remove() {
             throw new UnsupportedOperationException();
        }
    }

}
