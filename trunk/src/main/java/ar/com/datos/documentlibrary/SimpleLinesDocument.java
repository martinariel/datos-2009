package ar.com.datos.documentlibrary;
import java.util.ArrayList;

public class SimpleLinesDocument implements Document {

	private ArrayList<String> lineas;
	private int pos = 0;
	
	public SimpleLinesDocument(){
		lineas = new ArrayList<String>();
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
	
	public void addLine(String line){
		lineas.add(line);
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub

	}

	@Override
	public String readLine() {
		if (pos < lineas.size()){
			return lineas.get(pos++);
			
		}
		else {
			return null;
		}
	}

}
