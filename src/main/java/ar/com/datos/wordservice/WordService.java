package ar.com.datos.wordservice;
import java.util.Collection;

import ar.com.datos.audio.DocumentPlayer;
import ar.com.datos.audio.DocumentRecorder;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.persistencia.SoundPersistenceService;


/**
 * Backend de la aplicacion
 * 
 */
public class WordService {

	private Crawler crawler;
	private SoundPersistenceService persistenciaAudio;

	public WordService (){
		//TODO instanciar persistencia de audio
		//TODO instanciar crawler
	}
	
	/**
	 * TODO: Agrega un documento al indexador
	 * @param document
	 */
	public void addDocument(Document document){
		DocumentRecorder recorder = new DocumentRecorder(persistenciaAudio);
		try {
			recorder.record(document);
		}
		catch (Exception e){
			sendMessage("Audio device busy");
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
	public void playDocument(Document document){
		
		DocumentPlayer player = new DocumentPlayer();
		
		try {
			player.play(document);
		}
		catch (Exception e){
			sendMessage("Audio device busy");
		}		
		
	}
	
	/**
	 * Envia un mensaje a las vistas
	 * @param message
	 */
	private void sendMessage(String message){
		//TODO
	}
	
	
}
