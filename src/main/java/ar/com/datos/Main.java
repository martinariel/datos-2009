package ar.com.datos;

import ar.com.datos.audio.IConectorUsuarioGrabadorPalabras;
import ar.com.datos.audio.ReproductorPalabras;
import ar.com.datos.audio.GrabadorPalabras;

import ar.com.datos.parser.IParser;
import ar.com.datos.parser.SimpleTextParser;
import java.io.*;
import java.util.Collection;

/**
 *
 * @author mfernandez
 *
 */

public class Main implements IConectorUsuarioGrabadorPalabras{

    private IParser parser;
    private BufferedReader bufferReaderTeclado;
    private ReproductorPalabras reproductor;
    private GrabadorPalabras grabador;

    public Main() {

        parser				= new SimpleTextParser();
        bufferReaderTeclado = new BufferedReader(new InputStreamReader(System.in));
        reproductor 		= new ReproductorPalabras();
        grabador			= new GrabadorPalabras(this);

    }

    public void init() {
        showMenu();
    }


    @Override
    public void notificarPalabra(String palabra){
        System.out.println("Se ha encontrado la palabra " + palabra);
    }

    @Override
    public boolean iniciarGrabacion(){
        String opcion = leerStringTeclado();
        return opcion == "i";
    }

    @Override
    public boolean palabraGrabadaCorrectamente(){
        System.out.println("Opciones:");
        System.out.println("s: Guardar la palabra.");
        System.out.println("Grabar nuevamente (cualquier otra tecla).");
        String opcion = leerStringTeclado();
        return opcion == "s";
    }

    @Override
    public void notificarErrorGrabacion(){

    }

    public void grabacionIniciada(){
        String opcion = leerStringTeclado();

        if (opcion == "f"){
            grabador.stopRecording();
        }
        else {
            grabacionIniciada();
        }
    }


    /**
     * @return String leido por teclado
     */
    private String leerStringTeclado(){
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

        String tecla = leerStringTeclado();

        switch(tecla.charAt(0)){
        case '1': cargarDocumento();break;
        case '2': reproducirDocumento();break;
        }
    }


    /**
     * Solicita al usuario la ruta del documento e intenta parsearlo,
     * y luego guarda las palabras no existentes
     */
    private void cargarDocumento(){

        System.out.println("Ingrese una ruta valida:");

        String ruta = leerStringTeclado();
        Collection<String> palabras = null;

        try {
            palabras = parser.parseTextFile(ruta);
            grabador.guardarPalabras(palabras);
        }
        catch(Exception e){
            cargarDocumento();
        }

    }

    /**
     * Solicita al usuairo la ruta del documento, intenta parsearlo y reproduce
     * cada una de las palabras.
     *
     */
    private void reproducirDocumento(){
        System.out.println("Ingrese una ruta valida:");

        String ruta = leerStringTeclado();
        Collection<String> palabras = null;

        try {
            palabras = parser.parseTextFile(ruta);
            reproductor.reproducirPalabras(palabras);
        }
        catch(Exception e){
            reproducirDocumento();
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
