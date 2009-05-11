package ar.com.datos.persistencia.variableLength.registros;

import java.io.IOException;

import ar.com.datos.audio.AnotherInputStream;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializable;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.IntegerSerializer;
import ar.com.datos.serializer.common.SerializerCache;

/**
 * Registro que representa a un InputStream que puede ser serializado.
 * 
 * */

public class RegistroInputStream implements Serializable<RegistroInputStream>{

	private AnotherInputStream stream;
	
	
	public RegistroInputStream( AnotherInputStream istream )
	{ stream = istream; }
	
	
	protected RegistroInputStream(){}
	
	
	
	public AnotherInputStream getStream() 
	{
		return stream;
	}
	
	
	public void setStream( AnotherInputStream unstream) 
	{
		this.stream = unstream;
	}
	@Override
	public Serializer<RegistroInputStream> getSerializer() {
		return getSerializerStatic();
	}

	public static Serializer<RegistroInputStream> getSerializerStatic() 
	{
		
		return new Serializer<RegistroInputStream>(){
			private IntegerSerializer cardinalitySerializer = SerializerCache.getInstance().getSerializer(IntegerSerializer.class); 
			@Override
			public void dehydrate( OutputBuffer output, 
								   RegistroInputStream object) 
			{
				//Recorro cada byte de InputStream y lo copio en el output buffer
				Integer size = object.getStream().getSize();
				getCardinalitySerializer().dehydrate(output, size);
				try {
					byte[] datos = new byte[size];
					object.getStream().read(datos);
					output.write(datos);
				} catch (IOException e) {
					// XXX Esta excepcion no se deberia dar por tiempo de desarrollo se deja el catch
					e.printStackTrace();
				}
			}

			private IntegerSerializer getCardinalitySerializer() {
				return this.cardinalitySerializer;
			}

			@Override
			public long getDehydrateSize(RegistroInputStream object) {
				return object.getStream().getSize() + getCardinalitySerializer().getDehydrateSize(object.getStream().getSize());
			}

			@Override
			public RegistroInputStream hydrate(InputBuffer input) 
			{
				Integer size = this.getCardinalitySerializer().hydrate(input);
				byte[] data = new byte[size];
				return new RegistroInputStream(new AnotherInputStream(input.read(data)));
				
			}	
			
			
		};
	}
}


