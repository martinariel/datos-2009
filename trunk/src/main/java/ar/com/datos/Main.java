package ar.com.datos;

import ar.com.datos.audio.IWordsRecorderConector;
import ar.com.datos.audio.WordsPlayer;
import ar.com.datos.audio.WordsRecorder;
import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.variableLength.SoundPersistenceServiceVariableLengthImpl;

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
    private SoundPersistenceService servicioArchivos;


    public Main (String directorioArchivos){

        directorioArchivos = directorioArchivos.trim();

        directorioArchivos +=
            ((directorioArchivos.length() > 0) && !directorioArchivos.endsWith("/"))? "/":"";

        servicioArchivos = new SoundPersistenceServiceVariableLengthImpl(
                directorioArchivos + "palabras",
                directorioArchivos + "sonidos"
                );

        parser				= new SimpleTextParser();
        bufferReaderTeclado = new BufferedReader(new InputStreamReader(System.in));
        reproductor 		= new WordsPlayer(servicioArchivos);
        grabador			= new WordsRecorder(this,servicioArchivos);

    }

    public void init() {
        showMenu();
    }


    @Override
    public void notifyNextWord(String palabra){
        System.out.println("Se ha encontrado la palabra: " + palabra);
    }

    @Override
    public boolean canStartRecording(){
        System.out.println("Ingrese 'i' si quiere grabar la palabra.");
        return readKeyBoardChar() == 'i';
    }

    @Override
    public boolean recordingWordOK(){
        System.out.println("Opciones:");
        System.out.println("s: Guardar la palabra.");
        System.out.println("Grabar nuevamente (cualquier otra tecla).");

        return readKeyBoardChar() == 's';
    }

    @Override
    public void recordingWordError(){

    }

    @Override
    public void recordingWordStarted(){
        System.out.println("Grabando!!!!, ingrese 'f' para finalizar la grabacion.");

        if (readKeyBoardChar() == 'f'){
            grabador.stopRecording();
        }
        else {
            recordingWordStarted();
        }
    }

    @Override
    public void recordingAllWordsEnd(){
        System.out.println("Grabacion de palabras finalizada!!!");
        showMenu();
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

    private char readKeyBoardChar(){
        String opcion;
        do
            opcion = readKeyboardString();
        while (opcion.length() == 0 || opcion.length() > 1);

        return opcion.trim().toLowerCase().charAt(0);
    }

    /**
     * Menu Inicial
     *
     */
    private void showMenu() {
        System.out.println("Opciones:");
        System.out.println("1 - Carga de documentos");
        System.out.println("2 - Reproduccion de palabras");
        System.out.println("Cualquier otra tecla: Salir");
        System.out.println("Seleccione una opcion:");

        switch(readKeyBoardChar()){
        case '1': loadDocument();break;
        case '2': playDocument();break;
        }
        try {
            servicioArchivos.close();
        } catch (IOException e) {
            e.printStackTrace();
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

            try {
                grabador.recordWords(palabras);
            }
            catch(Exception e){
                e.printStackTrace();
            }
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

            try {
                reproductor.playWords(palabras);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            showMenu();
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

        Main app;

        if (args.length == 1){
            app = new Main(args[0].trim());
        }
        else {
            //Directorio actual
            app = new Main(".");
        }

        app.init();
    }

}
