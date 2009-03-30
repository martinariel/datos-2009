package ar.com.datos.wordandsoundservice.variableLength.registros;

import java.util.Queue;
import ar.com.datos.file.variableLength.VariableLengthAddress;

public class RegistroOffsetWord {

	VariableLengthAddress offset;
	String palabra;
	
	
	public RegistroOffsetWord( Queue<Object> co )
	{
		this.loadFromQueue( co );
	}
	
	public RegistroOffsetWord( VariableLengthAddress unoffset, String unapalabra )
	{
		this.offset = unoffset;
		this.palabra = unapalabra;
	}
	
	public VariableLengthAddress getOffset() 
	{
		return offset;
	}
	
	
	public String getPalabra() 
	{
		return palabra;
	}
	
	
	public void setOffset(VariableLengthAddress offset) 
	{
		this.offset = offset;
	}
	
	
	public void setPalabra(String palabra) 
	{
		this.palabra = palabra;
	}
	
	public Queue<Object> toQueue()
	{
		Queue<Object> co = new Queue<Object>();
		co.add( offset );
		co.add( palabra );
		return co;
	}
	
	public void loadFromQueue( Queue<Object> co )
	{
		try 
		{
			VariableLengthAddress address = (VariableLengthAddress)co.poll();
			String cadena = (String)co.poll();
			offset = address;
			palabra = cadena;
		}
		catch( ClassCastException ex ){}
	}
	
}
