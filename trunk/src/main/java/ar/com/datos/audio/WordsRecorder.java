package ar.com.datos.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.exception.UnregisteredWordException;
import ar.com.datos.persistencia.exception.WordIsAlreadyRegisteredException;

import ar.com.datos.persistencia.variableLength.SoundPersistenceServiceVariableLengthImpl;

/**
 * Grabador de palabras
 *
 * @author mfernandez
 *
 */
public class WordsRecorder {

    private AudioServiceHandler servicioAudio;
    private SoundPersistenceService servicioArchivos;
    private IWordsRecorderConector interfazUsuario;
    private ByteArrayOutputStream audio;
    private String palabraActual;

    public WordsRecorder(IWordsRecorderConector interfazUsuario) {
        servicioAudio = AudioServiceHandler.getInstance();
        servicioArchivos = new SoundPersistenceServiceVariableLengthImpl();
        this.interfazUsuario = interfazUsuario;
    }

    /**
     * Graba el audio de una palabra
     *
     * @param palabra
     */
    private void grabarPalabra(){

        if (interfazUsuario.canStartRecording()){
            //Grabo en memoria!!
            audio = new ByteArrayOutputStream();
            servicioAudio.record(audio);
            interfazUsuario.recordingStarted();
        }
    }

    /**
     * Detiene la grabacion
     *
     */
    public void stopRecording(){
        if (servicioAudio.isRecording()){
            servicioAudio.stopRecording();

            //Supongo que tengo memoria suficiente!
            InputStream inputAudio = new ByteArrayInputStream(audio.toByteArray());

            Thread reproduccion = servicioAudio.play(inputAudio);

            try {
                reproduccion.join();
            }
            catch(InterruptedException e){
                System.out.println("Thread principal interrumpido");
            }

            if (interfazUsuario.wordRecordedOk()) {
                try {
                    servicioArchivos.addWord(palabraActual, inputAudio);
                }
                catch (WordIsAlreadyRegisteredException e) {
                    interfazUsuario.notifyRecordingError();
                }
            }
        }
    }


    /**
     * Analiza la existencia de cada una de las palabras de la coleccion,
     * si una de ellas no existe se solicitara su grabacion.
     *
     * @param palabras
     */
    public void recordWords(Collection<String> palabras){

        for (String palabra : palabras){

            try {
                servicioArchivos.readWord(palabra);
            }
            catch (UnregisteredWordException e){
                palabraActual = palabra;
                interfazUsuario.notifyWord(palabraActual);
                grabarPalabra();
            }

        }
    }
}
