package ar.com.datos.wordservice;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import ar.com.datos.audio.DocumentPlayer;
import ar.com.datos.audio.DummieWordsRecorder;
import ar.com.datos.audio.IWordsRecorderConector;
import ar.com.datos.audio.AudioWordsRecorder;
import ar.com.datos.audio.WordsRecorder;
import ar.com.datos.audio.exception.AudioServiceHandlerException;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.DocumentLibrary;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.file.variableLength.address.OffsetAddressSerializer;
import ar.com.datos.indexer.SessionIndexer;
import ar.com.datos.indexer.SimpleSessionIndexer;
import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.variableLength.SoundPersistenceServiceTrieImpl;
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
public class WordService implements Closeable {

    private Crawler crawler;
    private SoundPersistenceService soundPersistenceService;
    private SessionIndexer<OffsetAddress> indexer;
    private DocumentLibrary documentLibrary;
    private SearchEngine searchEngine;
    private StopWordsDiscriminator stopWords;
    private Boolean closed = false;
    private boolean boostMic = false;
    private boolean micOpened = true;
    private static final String soundsFileName 		= "sonidos";
    private static final String wordsFileName 		= "palabras";
    private static final String documentsFileName 	= "documentos";
    private static final String indexFileName		= "indice";
    private static final String stopWordsFileName 	= "resources/stopWords/stopWordsFile.txt";
    private static final String stopPhrasesFileName = "resources/stopWords/stopWordsPhrases.txt";
    private static final int MAX_SEARCH_RESULTS 	= 5;

    /**
     * Crea la instancia del backend
     *
     * @param directory
     * Directorio de trabajo, donde se localizaran todos los archivos del sistema.
     */
    public WordService (String directory){


        soundPersistenceService = new SoundPersistenceServiceTrieImpl(directory + wordsFileName, directory + soundsFileName );
        documentLibrary = new DocumentLibrary (directory + documentsFileName);
        stopWords 		= StopWordsDiscriminatorBuilder.build(directory + stopWordsFileName, directory + stopPhrasesFileName);
        indexer 		= new SimpleSessionIndexer<OffsetAddress>(directory + indexFileName, new OffsetAddressSerializer());
        crawler 		= new SimpleCrawler(indexer, stopWords ,documentLibrary);
        searchEngine 	= new SearchEngineImpl(indexer, documentLibrary, stopWords);

    }
    
    public void setBoostMic(boolean value) {
    	boostMic = value;	
    }
    
    public void setMicOpened( boolean value){
    	this.micOpened = value;
    }

    /**
     * Agrega un documento al sistema
     *
     * @param document
     * Documento a agregar
     * @param view
     * IWordsRecorderConector a notificar la grabacion de las palabras
     *
     */
    public void addDocument(Document document , IWordsRecorderConector view){
        WordsRecorder recorder = (micOpened)?
        	new AudioWordsRecorder(view, soundPersistenceService, boostMic) :
        	new DummieWordsRecorder(view ,soundPersistenceService);
        
        try {
        	recorder.recordWords(crawler.addDocument(document));
        }
        catch(AudioServiceHandlerException e){
        	view.sendMessageLn("Audio device busy");
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        
    }


    /**
     * Obteniene una coleccion de documentos relacionados
     * con el documento parametro ordenados por relevancia
     *
     * @param query
     * Documento cuyo contenido es el query deseado
     * @return
     * Lista ordenada de documentos por orden de ranking (superior ranking primero).
     */
    public List<Tuple<Double, Document>> searchDocument(Document query){
        return searchEngine.lookUp(query, MAX_SEARCH_RESULTS);
    }


    /**
     * Reproduce un documento
     *
     * @param document
     * Documento a reproductir
     */
    public void playDocument(Document document, IWordsRecorderConector view){

        DocumentPlayer player = new DocumentPlayer(soundPersistenceService);

        document.close();
        document.open();
        
        for (String line = document.readLine(); line != null; line = document.readLine()){
        	view.sendMessageLn(line);
        } 
        
        try {
            player.play(document);
        }
        catch (AudioServiceHandlerException e){
        	view.sendMessageLn("Audio device busy");
        }
        catch (Exception e){
        	e.printStackTrace();
        }

    }
    
    @Override
    protected void finalize() throws Throwable {
    	if (!closed) {
        	this.close();
    	}
    	super.finalize();
    }


	public void close() throws IOException {
		closed = true;
        soundPersistenceService.close();
        documentLibrary.close();
        indexer.close();
	}


}
