package ar.com.datos.parser;

import java.io.IOException;
import java.util.Collection;

/**
 * Interfase de Parseadores de archivos de texto
 *
 * @author mfernandez
 *
 *@deprecated
 */
public interface IParser {

    /**
     * @param filePath
     * @return Collection<String> con todas las palabras del archivo.
     */
    public Collection<String> parseTextFile(String filePath) throws IOException;
}
