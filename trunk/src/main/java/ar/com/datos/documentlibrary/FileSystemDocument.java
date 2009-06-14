package ar.com.datos.documentlibrary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

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
	private BufferedWriter writer;

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
    public void openWrite() {
        try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("ISO8859-1")));
		} catch (IOException e) {
			e.printStackTrace();
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
        if (writer != null) {
        	try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	@Override
	public void addLine(String linea) {
        if (writer != null){
        	try {
				writer.write(linea);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        	
	}

}
