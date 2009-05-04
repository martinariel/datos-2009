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
public class FileSystemDocument implements Document {

    private String filePath;
    private File file;
    private boolean opened;
    private BufferedReader reader;

    public FileSystemDocument(String path){
        filePath = path;
        opened = false;
        reader = null;
        file = new File(filePath);
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


}
