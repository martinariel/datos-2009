package ar.com.datos;

import ar.com.datos.audio.IWordsRecorderConector;
import ar.com.datos.audio.WordsPlayer;
import ar.com.datos.audio.WordsRecorder;

import ar.com.datos.parser.IParser;
import ar.com.datos.parser.SimpleTextParser;
import java.io.*;
import java.util.Collection;

/**
 *
 * @author mfernandez
 *
 */

public class Main implements IWordsRecorderConector{

    private IParser parser;
    private BufferedReader bufferReaderTeclado;
    private WordsPlayer reproductor;
    private WordsRecorder grabador;

    public Main() {

        parser				= new SimpleTextParser();
        bufferReaderTeclado = new BufferedReader(new InputStreamReader(System.in));
        reproductor 		= new WordsPlayer();
        grabador			= new WordsRecorder(this);

    }

    public void init() {
        showMenu();
    }


    @Override
    public void notifyWord(String palabra){
        System.out.println("Se ha encontrado la palabra " + palabra);
    }

    @Override
    public boolean canStartRecording(){
        String opcion = readKeyboardString();
        return opcion == "i";
    }

    @Override
    public boolean wordRecordedOk(){
        System.out.println("Opciones:");
        System.out.println("s: Guardar la palabra.");
        System.out.println("Grabar nuevamente (cualquier otra tecla).");
        String opcion = readKeyboardString();
        return opcion == "s";
    }

    @Override
    public void notifyRecordingError(){

    }

    public void recordingStarted(){
        String opcion = readKeyboardString();

        if (opcion == "f"){
            grabador.stopRecording();
        }
        else {
            recordingStarted();
        }
    }


    /**
     * @return String leido por teclado
     */
    private String readKeyboardString(){
        String linea = "";
        try{
            linea = bufferReaderTeclado.readLine();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return linea;
    }

    /**
     * Menu Inicial
     *
     */
    private void showMenu() {
        System.out.println("Opciones:");
        System.out.println("1 - Carga de documentos");
        System.out.println("2 - Reproducción de palabras");
        System.out.println("Seleccione una opcion:");

        String tecla = readKeyboardString();

        switch(tecla.charAt(0)){
        case '1': loadDocument();break;
        case '2': playDocument();break;
        }
    }


    /**
     * Solicita al usuario la ruta del documento e intenta parsearlo,
     * y luego guarda las palabras no existentes
     */
    private void loadDocument(){

        System.out.println("Ingrese una ruta valida:");

        String ruta = readKeyboardString();
        Collection<String> palabras = null;

        try {
            palabras = parser.parseTextFile(ruta);
            grabador.recordWords(palabras);
        }
        catch(Exception e){
            loadDocument();
        }

    }

    /**
     * Solicita al usuairo la ruta del documento, intenta parsearlo y reproduce
     * cada una de las palabras.
     *
     */
    private void playDocument(){
        System.out.println("Ingrese una ruta valida:");

        String ruta = readKeyboardString();
        Collection<String> palabras = null;

        try {
            palabras = parser.parseTextFile(ruta);
            reproductor.playWords(palabras);
        }
        catch(Exception e){
            playDocument();
        }
    }

    /**
     * Main
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        Main app = new Main();
        app.init();
    }

}
