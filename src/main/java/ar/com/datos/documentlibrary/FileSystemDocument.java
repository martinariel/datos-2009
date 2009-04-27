package ar.com.datos.documentlibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Documento del tipo FileSystem
 * @author mfernandez
 *
 */
public class FileSystemDocument implements Document {

    private String filePath;
    private File archivo;
    private boolean opened;
    private BufferedReader reader;

    public FileSystemDocument(String path){
        filePath = path;
        opened = false;
        reader = null;
        archivo = null;
    }

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

    public void open(){
        if (!opened) {
            File archivo = new File(filePath);

            try {
                reader = new BufferedReader(new FileReader(archivo));
                opened = true;
            }
            catch (IOException e){ }

        }

    }

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


}
