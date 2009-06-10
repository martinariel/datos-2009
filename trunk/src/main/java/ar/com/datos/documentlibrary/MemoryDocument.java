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

    /*
     * (non-Javadoc)
     * @see ar.com.datos.documentlibrary.Document#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (MemoryDocument.class.isAssignableFrom(obj.getClass())) {
            return super.equals(obj);
        }

        MemoryDocument o = (MemoryDocument)obj;

        return this.lineas.equals(o.lineas);
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
