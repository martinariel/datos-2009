package ar.com.datos.audio;

import java.util.Collection;

import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.exception.WordIsAlreadyRegisteredException;

/**
 * 
 *	WordsRecorder dummie, agrega al servicio de persistencia audio de un byte por cada 
 *  palabra no encontrada, notificando unicamente a la vista cuando recorre todas las palabras.
 *  
 * @author mfernandez
 *
 */
public class DummieWordsRecorder  implements WordsRecorder {

	private SoundPersistenceService persistenceService;
	private IWordsRecorderConector view;
	
	public DummieWordsRecorder ( IWordsRecorderConector view,SoundPersistenceService persistenceService) {
		this.persistenceService = persistenceService;
		this.view = view;
	}
	
	@Override
	public void recordWords(Collection<String> palabras) {
		byte[] audio = { 0 } ;
		for (String palabra : palabras){
			AnotherInputStream stream = new AnotherInputStream(audio);
			
            if (!persistenceService.isRegistered(palabra)){
                try {
					persistenceService.addWord(palabra, stream);
				} catch (WordIsAlreadyRegisteredException e) {
					e.printStackTrace();
				}
            }

        }
		view.recordingAllWordsEnd();
		
	}

	@Override
	public void stopRecording() {
		// Do nothing
	}
	
}
