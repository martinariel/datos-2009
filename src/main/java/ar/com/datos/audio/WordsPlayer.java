package ar.com.datos.audio;

import java.io.InputStream;
import java.util.Collection;

import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.exception.UnregisteredWordException;
import ar.com.datos.persistencia.variableLength.SoundPersistenceServiceVariableLengthImpl;

/**
 * Reproductor de palabras
 *
 * @author mfernandez
 *
 */
public class WordsPlayer {

     private AudioServiceHandler servicioAudio;
     private SoundPersistenceService servicioArchivos;
     private boolean reproduciendo;

     public WordsPlayer(){
         servicioAudio = AudioServiceHandler.getInstance();
         servicioArchivos = new SoundPersistenceServiceVariableLengthImpl();
         reproduciendo = false;
     }

     /**
     * Reproduce cada una de las palabras de la coleccion
     * @param palabras
     */
    public void playWords(Collection<String> palabras){

        if (!reproduciendo){

            reproduciendo = true;

            for (String palabra : palabras){

                try{
                    InputStream audio = servicioArchivos.readWord(palabra);

                    /** No puedo reproducir directamente, deberia esperar que la reproducccion
                    * anterior termine. Para ello obtengo el Thread de reproduccion y llamo al metodo join()
                    *
                    * @see http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Thread.html#join()
                    *
                    */
                    Thread reproduccion = servicioAudio.play(audio);

                    try {
                        //Espero que termine la reproduccion
                        reproduccion.join();
                    }
                    catch(InterruptedException e){
                        System.out.println("Thread principal interrumpido");
                    }

                    //Desbloqueo el servicio de audio
                    servicioAudio.stopPlaying();

                }
                catch (UnregisteredWordException e){
                    // Audio no encontrado, beep??
                }

            }
        }
    }
}
