package ar.com.datos.wordservice;
import java.io.IOException;
import java.util.List;

import ar.com.datos.audio.DocumentPlayer;
import ar.com.datos.audio.IWordsRecorderConector;
import ar.com.datos.audio.WordsRecorder;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.DocumentLibrary;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.file.variableLength.address.OffsetAddressSerializer;
import ar.com.datos.indexer.SessionIndexer;
import ar.com.datos.indexer.SimpleSessionIndexer;
import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.variableLength.SoundPersistenceServiceVariableLengthImpl;
import ar.com.datos.util.Tuple;
import ar.com.datos.wordservice.search.SearchEngineImpl;
import ar.com.datos.wordservice.search.SearchEngine;
import ar.com.datos.wordservice.stopwords.StopWordsDiscriminator;
import ar.com.datos.wordservice.stopwords.StopWordsDiscriminatorBuilder;

/**
 * Backend de la aplicacion
 *
 * Basicamente delega comportamiento en otras clases.
 *
 *
 */
public class WordService {

    private Crawler crawler;
    private SoundPersistenceService soundPersistenceService;
    private SessionIndexer<OffsetAddress> indexer;
    private DocumentLibrary documentLibrary;
    private SearchEngine searchEngine;
    private StopWordsDiscriminator stopWords;

    private static final String soundsFileName 		= "sonidos";
    private static final String wordsFileName 		= "palabras";
    private static final String documentsFileName 	= "documentos";
    private static final String indexFileName			= "indice";
    private static final String stopWordsFileName 	= "stop_words.txt";
    private static final String stopPhrasesFileName 	= "stop_phrases.txt";

    /**
     * Crea la instancia del backend
     *
     * @param directorioArchivos
     * Directorio de trabajo, donde se localizaran todos los archivos del sistema.
     */
    public WordService (String directory){

        //TODO reemplazar por persistencia en Trie
        soundPersistenceService = new SoundPersistenceServiceVariableLengthImpl(
                directory + wordsFileName,
                directory + soundsFileName
        );

        documentLibrary = new DocumentLibrary (directory + documentsFileName);
        stopWords 		= StopWordsDiscriminatorBuilder.build(directory + stopWordsFileName, directory + stopPhrasesFileName);
        indexer 		= new SimpleSessionIndexer<OffsetAddress>(directory + indexFileName, new OffsetAddressSerializer());
        crawler 		= new SimpleCrawler(indexer, stopWords ,documentLibrary);
        searchEngine 	= new SearchEngineImpl(indexer, documentLibrary, stopWords);

    }


    /**
     * Agrega un documento al sistema
     *
     * @param document
     * Documento a agregar
     * @param vista
     * IWordsRecorderConector a notificar la grabacion de las palabras
     *
     */
    public void addDocument(Document document , IWordsRecorderConector view){
        WordsRecorder recorder = new WordsRecorder(view, soundPersistenceService);
        recorder.recordWords(crawler.addDocument(document));
    }


    /**
     * TODO: Obteniene una coleccion de documentos relacionados
     * con el documento parametro ordenados por relevancia
     *
     * @param query
     * Documento cuyo contenido es el query deseado
     * @return
     * Lista ordenada de documentos por orden de ranking (superior ranking primero).
     */
    public List<Tuple<Double, Document>> searchDocument(Document query){
        return searchEngine.lookUp(query, 5);
    }


    /**
     * Reproduce un documento
     *
     * @param document
     * Documento a reproductir
     */
    public void playDocument(Document document, IWordsRecorderConector view){

        DocumentPlayer player = new DocumentPlayer(soundPersistenceService);

        try {
            player.play(document);
        }
        catch (Exception e){
            view.sendMessage("Audio device busy");
        }

    }


    /**
     * Finaliza el servicio
     */
    public void end() {
        try{
            soundPersistenceService.close();
        }
        catch (IOException e){
             e.printStackTrace();
        }
    }


}
