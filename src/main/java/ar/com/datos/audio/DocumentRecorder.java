package ar.com.datos.audio;

import java.util.Collection;

import ar.com.datos.documentlibrary.IDocument;
import ar.com.datos.parser.Parser;
import ar.com.datos.persistencia.SoundPersistenceService;


/**
 * Grabador de documentos
 * @author martin
 *
 */
public class DocumentRecorder implements IWordsRecordConnector{
	 
	private SoundPersistenceService servicioPersistencia;
	private WordsRecorder grabador;
	
	public DocumentRecorder(SoundPersistenceService servicioPersistencia) {
		this.servicioPersistencia = servicioPersistencia;
		
	}
	
	/**
	 * Graba un documento
	 * @param documento
	 */
	public void record (IDocument documento){
	
		Parser parser = new Parser(documento);
		
		for (Collection<String> oracion : parser){
			grabador.recordWords(oracion);
		}
		
	}
	
	
	
}