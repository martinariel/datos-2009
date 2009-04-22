package ar.com.datos.parser;

import java.util.Iterator;
import java.util.Collection;

/**
 * Iterador del parser
 *
 * @author mfernandez
 *
 */
public class ParserLineIterator implements Iterator<Collection<String>> {

    private Parser parser;
    private Collection<String> words;


    public ParserLineIterator(Parser parser){
        this.parser = parser;
    }

    public boolean hasNext() {
        if (!parser.isStarted()) parser.initParser();
        words = parser.getCurrentWords();
        return words != null;
    }

    public Collection<String> next() {
        return words;
    }

    public void remove() {
         throw new UnsupportedOperationException();

    }

}
