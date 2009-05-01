package ar.com.datos.wordservice;
import java.io.IOException;
import java.util.Collection;

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
import ar.com.datos.wordservice.stopwords.StopWordsDiscriminatorBuilder;

/**
 * Backend de la aplicacion
 *
 */
public class WordService {

    private Crawler crawler;
    private SoundPersistenceService persistenciaAudio;
    private SessionIndexer<OffsetAddress> indexer;
    private DocumentLibrary documentLibrary;
    
    public WordService (String directorioArchivos){

        //TODO reemplazar por persistencia en Trie
        persistenciaAudio = new SoundPersistenceServiceVariableLengthImpl(
                directorioArchivos + "palabras",
                directorioArchivos + "sonidos"
        );
        
        documentLibrary = new DocumentLibrary ( directorioArchivos + "documentos");
        
        indexer = new SimpleSessionIndexer<OffsetAddress>(directorioArchivos + "indice", new OffsetAddressSerializer());
       
        crawler = new SimpleCrawler(indexer, StopWordsDiscriminatorBuilder.buildStopWords(directorioArchivos), documentLibrary);
    
    }
    

    /**
     * TODO: Agrega un documento al indexador
     * @param document
     */
    public void addDocument(Document document , IWordsRecorderConector vista){
    	WordsRecorder recorder = new WordsRecorder(vista, persistenciaAudio);
    	recorder.recordWords(crawler.addDocument(document));
    }


    /**
     * TODO: Obteniene una coleccion de documentos relacionados
     * con el documento parametro ordenados por relevancia
     *
     * @param document
     * @return
     */
    public Collection<Document> searchDocument(Document document){
        //TODO
        return null;
    }


    /**
     * Reproduce un documento
     * @param document
     */
    public void playDocument(Document document, IWordsRecorderConector vista){

        DocumentPlayer player = new DocumentPlayer(persistenciaAudio);

        try {
            player.play(document);
        }
        catch (Exception e){
            vista.sendMessage("Audio device busy");
        }

    }


    /**
     * Finaliza el servicio
     *
     */
    public void end() {
        try{
            persistenciaAudio.close();
        }
        catch (IOException e){
             e.printStackTrace();
        }
    }


}
