package ar.com.datos.audio;
import java.util.Collection;

import ar.com.datos.audio.WordsPlayer;
import ar.com.datos.documentlibrary.IDocument;
import ar.com.datos.parser.Parser;

/**
 * Reproductor de documentos
 * 
 * @author martin
 *
 */
public class DocumentPlayer {
	private WordsPlayer player;
	
	public DocumentPlayer(){
		player = new WordsPlayer();
	}
	
	/**
	 * Inicia la reproduccion de un documento
	 * @param documento
	 */
	public void play(IDocument documento){
		Parser parser = new Parser(documento);
		
		for (Collection<String> oracion : parser){
			player.playWords(oracion);
		}
	}

}
