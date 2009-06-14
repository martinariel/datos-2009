package ar.com.datos.documentlibrary;
import java.util.ArrayList;

/**
 * Implementacion en memoria de un Document
 *
 * @author mfernandez
 *
 */
public class MemoryDocument extends Document implements SizeKnowerDocumentReadable {

    private ArrayList<String> lineas;
    private int pos = -1;

    public MemoryDocument(){
        lineas = new ArrayList<String>();
    }

    @Override
    public void close() {
        pos = -1;
    }

    @Override
    public void addLine(String line){
        lineas.add(line);
    }

    @Override
    public void open() {
        pos = 0;
    }

    @Override
    public String readLine() {
        if (pos < lineas.size() && pos >= 0){
            return lineas.get(pos++);

        }
        else {
            return null;
        }
    }

    @Override
    public boolean canOpen(){
        return true;
    }

    @Override
    public String getLineAtPosition(int position) {

        if ( position >= this.getLinesCount()){
            throw new IllegalArgumentException();
        }

        return this.lineas.get(position);
    }

    @Override
    public int getLinesCount() {
        return this.lineas.size();
    }

    @Override
    protected SizeKnowerDocumentReadable getMultipleReadableDocument() {
        return this;
    }

}
