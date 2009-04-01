package ar.com.datos.wordandsoundservice.variableLength.registros;

import java.util.Queue;
import java.io.InputStream;
import ar.com.datos.file.variableLength.VariableLengthAddress;

public class RegistroInputStream {

	InputStream stream;
	
	
	public RegistroInputStream( Queue<Object> co )
	{
		this.loadFromQueue( co );
	}
	
	public RegistroInputStream( InputStream unstream )
	{
		this.stream = unstream;
	}
	
	public InputStream getStream() 
	{
		return stream;
	}
	
	
	public void setStream( InputStream unstream) 
	{
		this.stream = unstream;
	}
	
	
	
	public Queue<Object> toQueue()
	{
		Queue<Object> co = new Queue<Object>();
		co.add( stream );
		return co;
	}
	
	public void loadFromQueue( Queue<Object> co )
	{
		try 
		{
			InputStream unstream = (InputStream)co.poll();
			stream = unstream;
		}
		catch( ClassCastException ex ){}
	}
	
}


