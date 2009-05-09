package ar.com.datos;

import ar.com.datos.audio.AudioStopper;
import ar.com.datos.audio.IWordsRecorderConector;
import ar.com.datos.util.Tuple;
import ar.com.datos.wordservice.WordService;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.FileSystemDocument;
import ar.com.datos.documentlibrary.MemoryDocument;

import java.io.*;
import java.util.List;

/**
 *
 * @author mfernandez
 *
 */

public class Main implements IWordsRecorderConector{

    private BufferedReader bufferReaderTeclado;
    private WordService backend;
    private AudioStopper stopper;
    List<Tuple<Double, Document>> searchResult;

    /**
     *
     * @param directorioArchivos
     * Directorio de trabajo
     */
    public Main (String directorioArchivos){

        directorioArchivos = directorioArchivos.trim();

        directorioArchivos +=
            ((directorioArchivos.length() > 0) && !directorioArchivos.endsWith("/"))? "/":"";

        backend = new WordService(directorioArchivos);

        bufferReaderTeclado = new BufferedReader(new InputStreamReader(System.in));

    }

    public void init() {
        showMenu();
    }


    @Override
    public void notifyNextWord(String palabra){
        sendMessage("Se ha encontrado la palabra: " + palabra);
    }

    @Override
    public boolean canStartRecording(){
        sendMessage("Ingrese 'i' si quiere grabar la palabra.");
        return readKeyBoardChar() == 'i';
//        return true;
    }

    @Override
    public boolean recordingWordOK(){
        sendMessage("Opciones:");
        sendMessage("s: Guardar la palabra.");
        sendMessage("Grabar nuevamente (cualquier otra tecla).");

        return readKeyBoardChar() == 's';
//        return true;
    }

    @Override
    public void recordingWordError(){

    }

    @Override
    public void recordingWordStarted(){
        sendMessage("Grabando!!!!, ingrese 'f' para finalizar la grabacion.");
//        try {
//			Thread.sleep(200);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//
        if (readKeyBoardChar() == 'f'){
            stopper.stop();
/**/
        }
        else {
            recordingWordStarted();
        }
/**/
    }

    @Override
    public void recordingAllWordsEnd(){
        sendMessage("Grabacion de palabras finalizada!!!");

    }

    @Override
    public void sendMessage(String message){
        System.out.println(message);
    }

    @Override
    public void sendStopper(AudioStopper stopper){
        this.stopper = stopper;
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
     * @return Char Leido del teclado
     */
    private char readKeyBoardChar(){
        String opcion;
        do
            opcion = readKeyboardString();
        while (opcion.length() == 0 || opcion.length() > 1);

        return opcion.trim().toLowerCase().charAt(0);
    }

    /**
     * @return Integer leido del teclado
     */
    private int readKeyBoardInt(){
    	try {
            return Integer.parseInt(readKeyboardString());
    	} catch (NumberFormatException nfe) {
        	sendMessage("ingrese SOLO números");
        	return readKeyBoardInt();
        }
    }

    /**
     * Menu Inicial
     *
     */
    private void showMenu() {
        boolean getOut = false;
        while (!getOut) {
        	sendMessage("Opciones:");
        	sendMessage("1 - Carga de documentos");
        	sendMessage("2 - Reproduccion documento de FileSystem");
	        sendMessage("3 - Busqueda de documentos.");
	        sendMessage("Cualquier otra tecla: Salir");
	        sendMessage("Seleccione una opcion:");
	
	        switch(readKeyBoardChar()){
	        case '1': loadDocument();break;
	        case '2': playDocument();break;
	        case '3': searchDocument();break;
	        default :  
	            //Finalizo el backend
	            try {
	            	getOut = true;
	            	backend.close();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	        }
        }
    }


    /**
     * Solicita al usuario la ruta del documento e intenta parsearlo,
     * y luego guarda las palabras no existentes
     */
    private void loadDocument(){

        sendMessage("Ingrese una ruta valida:");

        String ruta = readKeyboardString();

        try {
           Document documento = new FileSystemDocument(ruta);

           if (documento.canOpen()){
               backend.addDocument(documento, this);
           }
           else
           {
               loadDocument();
           }
        }
        catch(Exception e){
           e.printStackTrace();
        }

    }

    /**
     * Solicita al usuairo la ruta del documento, intenta parsearlo y reproduce
     * cada una de las palabras.
     *
     */
    private void playDocument(){
        sendMessage("Ingrese una ruta valida:");

        String ruta = readKeyboardString();

        try {
            Document documento = new FileSystemDocument(ruta);

            if (documento.canOpen()){
                backend.playDocument(documento, this);
            }
            else
            {
                playDocument();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Busca el documento en el backend
     *
     */
    private void searchDocument(){

        sendMessage("Ingrese el criterio de busqueda:");
        String busqueda = readKeyboardString();

        MemoryDocument document = new MemoryDocument();
        document.addLine(busqueda);

        searchResult = backend.searchDocument(document);
        showSearchResult();

    }

    /**
     * Muestra los resultados de la busqueda
     *
     */
    private void showSearchResult(){
        int i = 0;

        sendMessage("Resultado de busqueda:");

        for (Tuple<Double, Document> result : searchResult){

            Double docValue = result.getFirst();
            Document doc = result.getSecond();
            doc.open();

            String line = doc.readLine();
            line = line.substring(0,  line.length() >= 20? 20 : line.length());

            if (line != null){
                sendMessage(new Integer(++i).toString() + " - " + new Double(docValue).toString() + " - "+ line);
            }

            doc.close();
        }

        if ( i == 0 ){
            sendMessage("No se han encontrado documentos. ");
        }
        else {
        	
            sendMessage("Desea reproducir un documento(s/n):");
            
            while (readKeyBoardChar() == 's') {

            	if (i > 1) {
                	
               		sendMessage("Ingrese el numero de documento: (1 - " + i + ")");

               		int searchOption = readKeyBoardInt();

               		if (searchOption > 0 && searchOption <= searchResult.size()) {
               			backend.playDocument(searchResult.get(searchOption - 1).getSecond(), this);
               		} else {
               			sendMessage("No es un número de documento valido");
               		}
           			sendMessage("Desea reproducir otro documento(s/n):");
            	} else {
                	backend.playDocument(searchResult.get(0).getSecond(), this);
                	sendMessage("Desea reproducirlo nuevamente(s/n):");
                }
            }
        }
    }


    /**
     * Main
     * @param args
     */
    public static void main(String[] args) {

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
