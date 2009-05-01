package ar.com.datos.parser;

import java.util.Iterator;
import ar.com.datos.documentlibrary.Document;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author mfernandez
 *
 */
public class Parser implements Iterable<Collection<String>> {

    private Document documento;
    private boolean iniciado;
    private String lineaAnterior = "";
    
    private static String[] separadores = null;
    private static String cadenaRegex;

    static {
    	separadores = new String[3];
    	separadores[0] = "?";
    	separadores[1] = ";";
    	separadores[2] = ".";
    	
    	//TODO: Todos los caracteres excepto
    	cadenaRegex = "[^a-zA-Z\\.\\;\\?]";
    	
    }
    
    public Parser(Document documento){
        this.documento = documento;
        iniciado = false;
    }
    

    /**
     *
     * @return estado de inicializacion
     */
    public boolean isStarted(){
        return iniciado;
    }

    /**
     * Inicia el parser
     *
     */
    public void initParser(){
        iniciado = true;
        documento.close();
        documento.open();
    }
    
    public void endParser(){
    	if (iniciado){
    		iniciado = false;
    		documento.close();
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
    private static String limpiarCaracteres(String linea){
    	
    	linea = linea.replaceAll(cadenaRegex, " ");
        linea = linea.replaceAll("  ", " ");
    	return linea;
    }
    
    private static String limpiarLinea(String linea){
    	return limpiarTildes(limpiarCaracteres(linea.toLowerCase()));
    	
    }
    
    /**
     * 
     * @param linea
     * @return Posicion en el string del primer separador encontrado
     */
    private static int findSeparator(String linea){
    	int indice = -1;
    	
    	int[] posiciones = new int[separadores.length];
    	
    	for (int i = 0; i < posiciones.length; i++){
    		posiciones[i] = linea.indexOf(separadores[i]);
    		//WTF??
    		if ((posiciones[i] > -1 && posiciones[i] < indice) || indice == -1)
    			indice = posiciones[i];
    	}
    	
    	return indice;
    	
    }
    
   
    private Collection<String> getCurrentWords(){
        Collection<String> words = null;

        if (isStarted()){
        	
        	String linea 			= lineaAnterior; //Inicializo en el pedazo anterior
        	String lineaArchivo		= null;
        	String lineaResultado 	= null;
        	
        	int posicionSeparator = -1;
        	
        	do {
        		//Busco el primer separador
        		posicionSeparator = findSeparator(linea);
        		
        		if (posicionSeparator < 0){
        			lineaArchivo = documento.readLine();
        			if (lineaArchivo != null)
        				linea += " " + limpiarLinea(lineaArchivo);
        		}	
	           
        	}while (posicionSeparator < 0 && lineaArchivo != null);

        	if (posicionSeparator > 0){
        		lineaResultado = linea.substring(0, posicionSeparator);
        		lineaAnterior  = linea.substring(posicionSeparator+1);
        	}
        	else {
        		lineaResultado = linea;
        		lineaAnterior = "";
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

    public Iterator<Collection<String>> iterator(){
        return new ParserLineIterator(this);
    }

    private class ParserLineIterator implements Iterator<Collection<String>> {

        private Parser parser;
        private Collection<String> words;


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

        public Collection<String> next() {
            return words;
        }

        public void remove() {
             throw new UnsupportedOperationException();
        }
    }
    
}
