package ar.com.datos.parser;

import java.util.Collection;

/**
 *
 * @author mfernandez
 * @deprecated;
 */
@Deprecated
public class SimpleTextParser extends AbstractTextParser {

    @Override
    protected void procesarLinea(String linea, Collection<String> resultado) {

        String[] palabras = linea.split(" ");

        for (int i = 0; i < palabras.length; i++){
            resultado.add(palabras[i].trim());
        }

    }

}
