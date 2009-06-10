package ar.com.datos.documentlibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Implementacion de un Document del FileSystem
 * @author mfernandez
 *
 */
public class FileSystemDocument extends Document {

    private String filePath;
    private File file;
    private boolean opened;
    private BufferedReader reader;
    private MemoryDocument memoryDocument;

    public FileSystemDocument(String path){
        filePath 		= path;
        opened 			= false;
        reader 			= null;
        file 			= new File(filePath);
        memoryDocument 	= null;
    }

    @Override
    public String readLine() {

        if (opened){
           try {
               return reader.readLine();
           }
           catch(IOException e){
               return null;
           }
        }
        else {
            return null;
        }
    }

    @Override
    public void open(){
        if (!opened) {
            try {
                reader = new BufferedReader(new FileReader(file));
                opened = true;
            }
            catch (IOException e){ }

        }

    }

    @Override
    public void close() {
        if (opened){
            if (reader != null){
                try {
                    reader.close();
                }
                catch(IOException e){}
            }
            opened = false;
        }
    }

    @Override
    public boolean canOpen(){
        return file.canRead();
    }

    /**
     *
     * @return
     * Un MemoryDocument con todas las lineas cargadas
     *
     * Obviamente implica leer todo el documento.
     */
    public MemoryDocument getMemoryDocument(){

        // La idea es leerlo una sola vez
        if (this.memoryDocument == null){

            MemoryDocument document = new MemoryDocument();
            this.open();

            for (String line = this.readLine(); line != null; line = this.readLine()) {
                document.addLine(line);
            }

            this.close();

            this.memoryDocument = document;
        }
        return this.memoryDocument;
    }

    @Override
    protected SizeKnowerDocumentReadable getMultipleReadableDocument() {
        return this.getMemoryDocument().getMultipleReadableDocument();
    }




}
