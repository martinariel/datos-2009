package ar.com.datos.wordservice.stopwords;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.FileSystemDocument;

/**
 * Builder de StopWordsDiscriminator
 *
 * @author martin
 *
 */
public class StopWordsDiscriminatorBuilder {

    /**
     * Devuelve un StopWordsDiscriminator cargado con stopwords y stopphrases
     * buscando en los archivos stop_words.txt y stop_phrases.txt del directorio
     * especificado.
     *
     * @param fileDirectory
     * @return
     */
    public static StopWordsDiscriminator build(String stopWordsFilePath, String stopPhrasesFilePath){

        Set<String> stopWords 			= new HashSet<String>();
        List<List<String>> stopPhrases 	= new LinkedList<List<String>>();
        Document stopWordsDocument		= new FileSystemDocument(stopWordsFilePath);
        Document stopPhrasesDocument	= new FileSystemDocument(stopPhrasesFilePath);

        stopWordsDocument.open();
        for (String line = stopWordsDocument.readLine(); line != null; line = stopWordsDocument.readLine()) {
            stopWords.add(line.trim());
        }
        stopWordsDocument.close();

        stopPhrasesDocument.open();
        for (String line = stopPhrasesDocument.readLine(); line != null; line = stopPhrasesDocument.readLine()) {
            String[] stopPhraseWords = line.split(" ");
            LinkedList<String> stopPhrase = new LinkedList<String>();
            for (int i = 0; i < stopPhraseWords.length; i++){
                stopPhrase.add(stopPhraseWords[i].trim());
            }
            stopPhrases.add(stopPhrase);
        }
        stopPhrasesDocument.close();

        return new SimpleStopWordsDiscriminator(stopWords, stopPhrases);
    }

}
