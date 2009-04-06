package ar.com.datos.audio;

import java.io.InputStream;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat;
import ar.com.datos.audio.exception.AudioServiceHandlerException;

import ar.com.datos.capturaaudio.core.SimpleAudioRecorder;
import ar.com.datos.reproduccionaudio.core.SimpleAudioPlayer;
import ar.com.datos.capturaaudio.exception.SimpleAudioRecorderException;
import ar.com.datos.reproduccionaudio.exception.SimpleAudioPlayerException;

/**
 * Componente encapuslador de acciones de audio
 *
 * Singleton
 * @author mfernandez
 *
 */

public class AudioServiceHandler {

    private boolean recording;
    private boolean playing;
    private SimpleAudioPlayer reproductor;
    private SimpleAudioRecorder grabador;

    private static AudioServiceHandler instance = null;

    private AudioServiceHandler(){
        recording = false;
        playing = false;
    }

    /**
     *
     * @return Instancia singleton
     */
    public static AudioServiceHandler getInstance(){
        if (instance == null) instance = new AudioServiceHandler();
        return instance;
    }

    public boolean isRecording() {
        return recording;
    }

    public boolean isPlaying(){
        return playing;
    }

    /**
     * Inicia un grabador de audio en el outputstream
     *
     * @param output
     * @throws AudioServiceHandlerException
     */
    public void record(OutputStream output) throws AudioServiceHandlerException{
        if (!recording && !playing){

            grabador = new SimpleAudioRecorder(AudioFileFormat.Type.AU,output);

            try{
                grabador.init();
                grabador.startRecording();
                recording = true;
            }
            catch (SimpleAudioRecorderException e){
                throw new AudioServiceHandlerException();
            }


        }
        else {
            throw new AudioServiceHandlerException();
        }
    }

    /**
     * Inicia la reproduccion de audio en el inputstream
     *
     * @param input
     * @throws AudioServiceHandlerException
     */
    public Thread play(InputStream input) throws AudioServiceHandlerException{
        if (!recording && !playing){

            reproductor = new SimpleAudioPlayer(input);

            try {
                reproductor.init();
                reproductor.startPlaying();
            }
            catch (SimpleAudioPlayerException e){
                throw new AudioServiceHandlerException();
            }

        }
        else {
            throw new AudioServiceHandlerException();
        }

        return reproductor;
    }


    /**
     * Detiene la grabacion de audio
     *
     */
    public void stopRecording(){
        if (recording){
            try {
                grabador.stopRecording();
                recording = false;
            }
            catch(Exception e){
                //Que hago aca?
            }
        }

    }

    /**
     * Detiene la reproduccion de audio
     *
     */
    public void stopPlaying(){
        if (playing){
            try {
                reproductor.stopPlaying();
                playing = false;
            }
            catch (Exception e){
                    //Que hago aca?
            }
        }

    }
}
