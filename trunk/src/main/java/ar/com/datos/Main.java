package ar.com.datos;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;

import ar.com.datos.audio.AudioStopper;
import ar.com.datos.audio.IWordsRecorderConector;
import ar.com.datos.buffer.FileInputBuffer;
import ar.com.datos.buffer.FileOutputBuffer;
import ar.com.datos.compressor.CompressorException;
import ar.com.datos.compressor.FileCompressor;
import ar.com.datos.compressor.FileDeCompressor;
import ar.com.datos.compressor.arithmetic.dynamic.DynamicArithmeticCompressor;
import ar.com.datos.compressor.arithmetic.dynamic.DynamicArithmeticDecompressor;
import ar.com.datos.compressor.lzp.file.LzpFileCompressor;
import ar.com.datos.compressor.lzp.file.LzpFileDeCompressor;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.FileSystemDocument;
import ar.com.datos.documentlibrary.MemoryDocument;
import ar.com.datos.file.StandardFileWrapper;
import ar.com.datos.file.exception.OutOfBoundsException;
import ar.com.datos.util.Tuple;
import ar.com.datos.wordservice.WordService;

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
    

    /**
     *
     * @param directorioArchivos
     * Directorio de trabajo.
     * @param openMic
     * Determina si el audio se controla automaticamente, es decir, se agrega audio automaticamente sin
     * utilizar el servicio de audio.
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
        
        this.backend.setBoostMic(boostAudio);
        this.backend.setRecordingEnabled(!openMic);
       
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
        return readKeyBoardChar() == 'i';
    }

    @Override
    public boolean recordingWordOK(){
        sendMessageLn("Opciones:");
        sendMessageLn("s: Guardar la palabra.");
        sendMessageLn("Grabar nuevamente (cualquier otra tecla).");

        return readKeyBoardChar() == 's';
    }

    @Override
    public void recordingWordError(){

    }

    @Override
    public void recordingWordStarted(){
        sendMessageLn("Grabando!!!!, ingrese 'f' para finalizar la grabacion.");
        
    	if (readKeyBoardChar() == 'f'){
            stopper.stop();
        }
        else {
            recordingWordStarted();
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
        	sendMessageLn("ingrese SOLO numeros");
        	return readKeyBoardInt();
        }
    }

    /**
     * 
     * @return Byte leido de teclado.
     */
    private byte readKeyBoardByte()
    {
    	try{
    		return Byte.parseByte(readKeyboardString());
    	}
    	catch (NumberFormatException nfe) {
    		sendMessageLn("ingrese SOLO numeros");
    		return readKeyBoardByte();
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
	        sendMessageLn("4 - Prueba de Compresión.");
	        sendMessageLn("5 - Prueba de Descompresión.");
	        sendMessageLn("Cualquier otra tecla: Salir");
	        sendMessageLn("Seleccione una opcion:");
	
	        switch(readKeyBoardChar()){
	        case '1': loadDocument();break;
	        case '2': playDocument();break;
	        case '3': searchDocument();break;
	        case '4': testArithmeticCompression();break;
	        case '5': testArithmeticDecompression();break;
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
    
    private Object chooseCompressorForTest(boolean decompressor) {
    	Object returnValue = null;
    	Byte compressor = 0;
    	sendMessageLn("Seleccione el tipo de compresor:");
    	sendMessageLn("1 - ARITMETICO.");
    	sendMessageLn("2 - LZP.");
    	sendMessageLn("3 - PPMC.");
    	while (compressor < 1 || compressor > 3) {
    		sendMessage("1-3: ");
    		compressor = readKeyBoardByte();
    	}
    	
    	switch (compressor) {
			case 1: {
				returnValue = (decompressor) ? new DynamicArithmeticDecompressor(System.out) : new DynamicArithmeticCompressor(System.out);
				break;
			}
			case 2: {
				returnValue = (decompressor) ? new LzpFileDeCompressor(System.out) : new LzpFileCompressor(System.out);
				break;
			}
//			TODO
// NOTA: Aclarar con un mensaje que se usa el PPMC por defecto!!! (modelo 4 creo)
//			case 3: {
//				returnValue = (decompressor) ? new PpmcFileDeCompressor(System.out) : new PpmcFileCompressor(System.out);
//				break;
//			}
		}
    	
    	return returnValue;
    }
    
    /**
     * 
     */
	private void testArithmeticDecompression() {
		String rutaEntrada;
		String rutaSalida;
		do {
			sendMessageLn("Ingrese archivo a descomprimir");
	        rutaEntrada = readKeyboardString();
		} while (!isValidFileInput(rutaEntrada));
        
		do {
			sendMessageLn("Ingrese ruta para el archivo descomprimido");
	        rutaSalida = readKeyboardString();
		} while (!isValidFileOutput(rutaSalida));
		FileDeCompressor fileDeCompressor = (FileDeCompressor)chooseCompressorForTest(true);
		new File(rutaSalida).delete();
		FileSystemDocument document = new FileSystemDocument(rutaSalida);
		document.openWrite();
		try {
			fileDeCompressor.decompress(new FileInputBuffer(new StandardFileWrapper(rutaEntrada)), document);
		} catch (OutOfBoundsException e) {
			sendMessageLn("*** Error al descomprimir ***");
			sendMessageLn("El archivo provisto no estaba comprimido con un " + fileDeCompressor.getCompressorName());
		} catch (CompressorException e) {
			sendMessageLn("*** Error al descomprimir ***");
			sendMessageLn("El archivo provisto no estaba comprimido con un " + fileDeCompressor.getCompressorName());
		} finally {
			document.close();
			sendMessageLn("");
		}
	}

	private void testArithmeticCompression() {
		String rutaEntrada;
		String rutaSalida;
		do {
			sendMessageLn("Ingrese archivo a comprimir");
	        rutaEntrada = readKeyboardString();
		} while (!isValidFileInput(rutaEntrada));
        
		do {
			sendMessageLn("Ingrese ruta del archivo comprimido");
	        rutaSalida = readKeyboardString();
		} while (!isValidFileOutput(rutaSalida));
		FileCompressor fileCompressor = (FileCompressor)chooseCompressorForTest(false);
		StandardFileWrapper file = new StandardFileWrapper(rutaSalida);
		file.getFile().delete();
		fileCompressor.compress(new FileSystemDocument(rutaEntrada), new FileOutputBuffer(file));
		file.close();
	}

	protected boolean isValidFileOutput(String rutaSalida) {
		File f = new File(rutaSalida);
		return !rutaSalida.equals("") && !f.isDirectory() && ((f.exists() && f.canWrite()) || f.getAbsoluteFile().getParentFile().canWrite());
	}

	protected boolean isValidFileInput(String rutaEntrada) {
		File f = new File(rutaEntrada);
		return f.isFile() && f.canRead();
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
        	   
        	   sendMessageLn("Seleccione el tipo de compresor:");
        	   sendMessageLn("1 - LZP.");
        	   sendMessageLn("2 - PPMC.");
        	   sendMessageLn("Cualquier otro numero: Sin compresion.");
        	   
        	   Byte compresor = readKeyBoardByte();
               backend.addDocument(documento, this, compresor);
               
               
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
               			sendMessageLn("No es un nï¿½mero de documento valido");
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
