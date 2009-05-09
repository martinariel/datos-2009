package ar.com.datos.audio;

import java.io.ByteArrayOutputStream;
import java.util.Collection;

import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.exception.WordIsAlreadyRegisteredException;

/**
 * Grabador de palabras
 *
 * @author mfernandez
 *
 */
public class WordsRecorder implements AudioStopper{

    private AudioServiceHandler servicioAudio;
    private SoundPersistenceService servicioArchivos;
    private IWordsRecorderConector interfazUsuario;
    private ByteArrayOutputStream audio;
    private String palabraActual;

    public WordsRecorder(IWordsRecorderConector interfazUsuario, SoundPersistenceService servicioArchivos) {
        servicioAudio = AudioServiceHandler.getInstance();
        this.servicioArchivos = servicioArchivos;
        this.interfazUsuario = interfazUsuario;

        //Le envio el stopper a la interfaz de usuario
        this.interfazUsuario.sendStopper(this);
    }

    /**
     * Graba el audio de una palabra
     *
     * @param palabra
     */
    private void grabarPalabra(){

        if (interfazUsuario.canStartRecording()){
            //Grabo en memoria!!
            audio = new ByteArrayOutputStream(128 * 1024);
            servicioAudio.record(audio);
            interfazUsuario.recordingWordStarted();
        }
    }

    public void stop(){
        stopRecording();
    }

    /**
     * Detiene la grabacion
     *
     */
    public void stopRecording(){
        if (servicioAudio.isRecording()){
            servicioAudio.stopRecording();

            //Supongo que tengo memoria suficiente!
            AnotherInputStream inputAudio = new AnotherInputStream(audio.toByteArray());
            inputAudio.mark(Integer.MAX_VALUE);
            Thread reproduccion = servicioAudio.play(inputAudio);

            try {
                // FIXME tarda en devolver el control
                reproduccion.join();
            }
            catch(InterruptedException e){
                System.out.println("Thread principal interrumpido");
            }

            inputAudio.reset();

            if (interfazUsuario.recordingWordOK()) {
                try {
                    servicioArchivos.addWord(palabraActual, inputAudio);
                }
                catch (WordIsAlreadyRegisteredException e) {
                    interfazUsuario.recordingWordError();
                }
            }
            else {
                grabarPalabra();
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

            if (!servicioArchivos.isRegistered(palabra)){
                palabraActual = palabra;
                interfazUsuario.notifyNextWord(palabraActual);
                grabarPalabra();
            }

        }

        interfazUsuario.recordingAllWordsEnd();
    }
}
