package ar.com.datos.audio;

import java.util.Collection;

import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.exception.WordIsAlreadyRegisteredException;

public class DummieWordsRecorder  implements WordsRecorder {

	private SoundPersistenceService persistenceService;
	private IWordsRecorderConector view;
	
	public DummieWordsRecorder ( IWordsRecorderConector view,SoundPersistenceService persistenceService) {
		this.persistenceService = persistenceService;
		this.view = view;
	}
	
	@Override
	public void recordWords(Collection<String> palabras) {
		//TODO: Definir audio vacio.
		byte[] audio = { 0 , 0 } ;
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
