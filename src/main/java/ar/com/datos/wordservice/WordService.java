package ar.com.datos.wordservice;
import java.io.IOException;
import java.util.Collection;

import ar.com.datos.audio.DocumentPlayer;
import ar.com.datos.audio.DocumentRecorder;
import ar.com.datos.audio.IWordsRecorderConector;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.indexer.Indexer;
import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.variableLength.SoundPersistenceServiceVariableLengthImpl;


/**
 * Backend de la aplicacion
 *
 */
public class WordService {

    private Crawler crawler;
    private SoundPersistenceService persistenciaAudio;
    private Indexer<Document> indexer;
    
    public WordService (String directorioArchivos){

        //TODO reemplazar por persistencia en Trie
        persistenciaAudio = new SoundPersistenceServiceVariableLengthImpl(
                directorioArchivos + "palabras",
                directorioArchivos + "sonidos"
                );
        //TODO instanciar Indexer y crawler
       
    }

    /**
     * TODO: Agrega un documento al indexador
     * @param document
     */
    public void addDocument(Document document , IWordsRecorderConector vista){
        DocumentRecorder recorder = new DocumentRecorder(vista, persistenciaAudio);
        try {
            recorder.record(document);
        }
        catch (Exception e){
            vista.sendMessage("Audio device busy");
        }
        crawler.addDocument(document);
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
