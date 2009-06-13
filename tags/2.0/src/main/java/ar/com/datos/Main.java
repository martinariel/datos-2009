package ar.com.datos;

import ar.com.datos.audio.AudioStopper;
import ar.com.datos.audio.IWordsRecorderConector;
import ar.com.datos.util.Tuple;
import ar.com.datos.wordservice.WordService;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.FileSystemDocument;
import ar.com.datos.documentlibrary.MemoryDocument;

import java.io.*;
import java.util.HashSet;
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
    private List<Tuple<Double, Document>> searchResult;
    private boolean openMic;
    

    /**
     *
     * @param directorioArchivos
     * Directorio de trabajo.
     * @param openMic
     * Determina si el microfono se controla automaticamente.
     * @param boostAudio
     * Determina si el OutputStream de grabacion de audio es cortado inmediatamente
     * despues del stop.
     */
    public Main (String directorioArchivos, boolean openMic, boolean boostAudio){

        directorioArchivos = directorioArchivos.trim();

        directorioArchivos +=
            ((directorioArchivos.length() > 0) && !directorioArchivos.endsWith("/"))? "/":"";

        this.backend 			 = new WordService(directorioArchivos);
        this.bufferReaderTeclado = new BufferedReader(new InputStreamReader(System.in));
        this.openMic 			 = openMic;
        
        this.backend.setBoostMic(boostAudio);
        
        if (openMic) sendMessageLn("open-mic on");
        if (boostAudio) sendMessageLn("boost-audio on");
    }

    /**
     * Inicia la instancia 
     */
    public void init() {
        showMenu();
    }


    @Override
    public void notifyNextWord(String palabra){
        sendMessageLn("Se ha encontrado la palabra: " + palabra);
    }

    @Override
    public boolean canStartRecording(){
        sendMessageLn("Ingrese 'i' si quiere grabar la palabra.");
        
        return (openMic)? true : readKeyBoardChar() == 'i';
    }

    @Override
    public boolean recordingWordOK(){
        sendMessageLn("Opciones:");
        sendMessageLn("s: Guardar la palabra.");
        sendMessageLn("Grabar nuevamente (cualquier otra tecla).");

        return (openMic)? true : readKeyBoardChar() == 's';
    }

    @Override
    public void recordingWordError(){

    }

    @Override
    public void recordingWordStarted(){
        sendMessageLn("Grabando!!!!, ingrese 'f' para finalizar la grabacion.");
        
        if (openMic){
          try {
        	  
        	  Thread.sleep(100);
          
          } catch (InterruptedException e) {
			e.printStackTrace();
          }
          stopper.stop();
        } 
        else {
        	if (readKeyBoardChar() == 'f'){
                stopper.stop();
            }
            else {
                recordingWordStarted();
            }
        }
    }

    @Override
    public void recordingAllWordsEnd(){
        sendMessageLn("Grabacion de palabras finalizada!!!");

    }

    @Override
    public void sendMessageLn(String message){
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
        	sendMessageLn("ingrese SOLO n�meros");
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
        	sendMessageLn("Opciones:");
        	sendMessageLn("1 - Carga de documentos");
        	sendMessageLn("2 - Reproduccion documento de FileSystem");
	        sendMessageLn("3 - Busqueda de documentos.");
	        sendMessageLn("Cualquier otra tecla: Salir");
	        sendMessageLn("Seleccione una opcion:");
	
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

        sendMessageLn("Ingrese una ruta valida:");

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
        sendMessageLn("Ingrese una ruta valida:");

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

        sendMessageLn("Ingrese el criterio de busqueda:");
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

        sendMessageLn("Resultado de busqueda:");

        for (Tuple<Double, Document> result : searchResult){

            Double docValue = result.getFirst();
            Document doc = result.getSecond();
            doc.open();

            String line = doc.readLine();
            line = line.substring(0,  line.length() >= 40? 40 : line.length());

            if (line != null){
                sendMessageLn(new Integer(++i).toString() + " - " + new Double(docValue).toString() + " - "+ line);
            }

            doc.close();
        }

        if ( i == 0 ){
            sendMessageLn("No se han encontrado documentos. ");
        }
        else {
        	
            sendMessageLn("Desea reproducir un documento(s/n):");
            
            while (readKeyBoardChar() == 's') {

            	if (i > 1) {
                	
               		sendMessageLn("Ingrese el numero de documento: (1 - " + i + ")");

               		int searchOption = readKeyBoardInt();

               		if (searchOption > 0 && searchOption <= searchResult.size()) {
               			backend.playDocument(searchResult.get(searchOption - 1).getSecond(), this);
               		} else {
               			sendMessageLn("No es un n�mero de documento valido");
               		}
           			sendMessageLn("Desea reproducir otro documento(s/n):");
            	} else {
                	backend.playDocument(searchResult.get(0).getSecond(), this);
                	sendMessageLn("Desea reproducirlo nuevamente(s/n):");
                }
            }
        }
    }


    /**
     * Main
     * @param args
     */
    public static void main(String[] args) {
        
        HashSet<String> parametros = new HashSet<String>();
        
        for (String parametro : args) parametros.add(parametro);
        boolean openMic	   = parametros.remove("open-mic");
        boolean boostAudio = parametros.remove("boost-audio");
        String directory   = ".";
      
        if (parametros.size() == 1){
        	//WTF
        	for (String parametro : parametros) directory = parametro;
        }
       
        Main app = new Main(directory, openMic, boostAudio);
        
        app.init();
    }

    /*
     * (non-Javadoc)
     * @see ar.com.datos.audio.IWordsRecorderConector#sendMessage(java.lang.String)
     */
	public void sendMessage(String message) {
        System.out.print(message);
        System.out.flush();
	}

}