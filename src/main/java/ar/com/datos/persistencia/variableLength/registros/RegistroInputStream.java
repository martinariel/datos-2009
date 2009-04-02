package ar.com.datos.persistencia.variableLength.registros;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.*;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.buffer.exception.BufferException;

import ar.com.datos.file.variableLength.VariableLengthAddress;

import ar.com.datos.serializer.Serializable;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.*;

/**
 * Registro que representa a un InputStream que puede ser serializado.
 * 
 * */

public class RegistroInputStream implements Serializable<RegistroInputStream>{

	InputStream stream;
	
	
	public RegistroInputStream( InputStream istream )
	{ stream = istream; }
	
	
	protected RegistroInputStream(){}
	
	
	
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

			@Override
			public void dehydrate( OutputBuffer output, 
								   RegistroInputStream object) 
			{
				//Recorro cada byte de InputStream y lo copio en el output buffer
				try
				{
					Integer byteactual = object.getStream().read();
					if ( byteactual.intValue() != -1 )
						output.write( byteactual.byteValue() );
					
				}catch ( IOException ex ){}
			}

			@Override
			public long getDehydrateSize(RegistroInputStream object) {
				return 5;
			}

			@Override
			public RegistroInputStream hydrate(InputBuffer input) 
			{
				ArrayList<Byte> array = new ArrayList<Byte>();
				
				//Cargo los Bytes del Input buffer en un ArrayList
				try
				{
					Integer byteactual = new Integer (input.read());
					if ( byteactual.intValue() != -1 )
						array.add( byteactual.byteValue() );
				}catch ( BufferException ex ){}
				
				//Ahora que tengo el tamaño del input buffer vuelvo a cargar los bytes en un byte[]
				byte[] arraydebytes = new byte[ array.size() ];
				
				for( int i =0;i<array.size();i++)
				{ arraydebytes[i] = array.get(i); }
				
				//con el byte[] armo el input Stream, y el registro hidratado.
				InputStream stream = new ByteArrayInputStream( arraydebytes );
				RegistroInputStream registro = new RegistroInputStream( stream );
				
				return registro;
				
			}	
			
			
		};
	}
	
	
	
	
	
}


