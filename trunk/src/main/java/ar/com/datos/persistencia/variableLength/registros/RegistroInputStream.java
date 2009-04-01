package ar.com.datos.persistencia.variableLength.registros;

import java.io.InputStream;
import java.io.ByteArrayInputStream;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;

import ar.com.datos.serializer.Serializable;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.*;

/**
 * Registro que representa a un InputStream que puede ser serializado.
 * 
 * */

public class RegistroInputStream implements Serializable<RegistroInputStream>{

	InputStream stream;
	
	
	private RegistroInputStream(){}
	
	
	public InputStream getStream() 
	{
		return stream;
	}
	
	
	public void setStream( InputStream unstream) 
	{
		this.stream = unstream;
	}

	@Override
	public Serializer<RegistroInputStream> getSerializer() 
	{
		
		return new Serializer<RegistroInputStream>(){

			private StringSerializerDelimiter stringserializer = new StringSerializerDelimiter( new byte[] {(byte)0, (byte)(0)});

			@Override
			public void dehydrate( OutputBuffer output, 
								   RegistroInputStream object) 
			{
				stringserializer.dehydrate( output , object.getStream().toString() );
			}

			@Override
			public long getDehydrateSize(RegistroInputStream object) {
				
				return ( object.getStream().toString().length() );
			}

			@Override
			public RegistroInputStream hydrate(InputBuffer input) {
				String cadena = stringserializer.hydrate( input );
				InputStream istream = new ByteArrayInputStream( cadena.getBytes() );
				RegistroInputStream reg = new RegistroInputStream( istream );
				return reg;
			}
			
		};
	}
	
	
	
	
	
}


