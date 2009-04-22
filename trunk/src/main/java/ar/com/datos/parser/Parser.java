package ar.com.datos.parser;

import java.lang.ref.PhantomReference;
import java.util.Iterator;
import ar.com.datos.documentlibrary.IDocument;
import java.util.Collection;

/**
 * @author mfernandez
 *
 */
public class Parser implements Iterable {

    private IDocument documento;
    private boolean iniciado;
    private String lineaAnterior = "";

    private static String PHRASE_SEPARATOR = "|||";

    public Parser(IDocument documento){
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
        documento.open();
    }

    private String stemming(String linea){

        //TODO mejorar esto
        linea = linea.replaceAll("�", "a");
        linea = linea.replaceAll("�", "e");
        linea = linea.replaceAll("�", "i");
        linea = linea.replaceAll("�", "o");
        linea = linea.replaceAll("�", "u");

        return linea;
    }

    public Collection<String> getCurrentWords(){
        Collection<String> words = null;

        if (isStarted()){

            String linea = documento.readLine();

            if (linea != null){
                linea = stemming(linea) + " " + lineaAnterior;
            }
            else {
                if (lineaAnterior != "") linea = lineaAnterior;
            }

            String[] vector = linea.split(PHRASE_SEPARATOR);



        }
        return words;
    }

    public Iterator iterator(){
        return new ParserLineIterator(this);
    }

}
