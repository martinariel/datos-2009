package ar.com.datos.audio;
import java.util.Collection;

import ar.com.datos.audio.WordsPlayer;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.parser.Parser;
import ar.com.datos.persistencia.SoundPersistenceService;

/**
 * Reproductor de documentos
 *
 * @author martin
 *
 */
public class DocumentPlayer {
    private WordsPlayer player;

    public DocumentPlayer(SoundPersistenceService servicioAudio){
        player = new WordsPlayer(servicioAudio);
    }

    /**
     * Inicia la reproduccion de un documento
     * @param documento
     */
    public void play(Document documento){
        Parser parser = new Parser(documento);
        for (Collection<String> oracion : parser){
            player.playWords(oracion);
        }
    }

}
