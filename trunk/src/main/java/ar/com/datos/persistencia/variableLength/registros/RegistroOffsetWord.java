package ar.com.datos.persistencia.variableLength.registros;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.address.VariableLengthAddress;
import ar.com.datos.serializer.Serializable;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.ShortSerializer;
import ar.com.datos.serializer.common.StringSerializerDelimiter;



/**
 * Registro que representa a una palabra y su offset correpondiente.
 * Éste offset es la dirección del stream de la palabra en algún otro 
 * archivo.
 * Implementa la interfaz Serializable<T>, por lo tanto, puede obtenerse
 * un Serializer para serializar el registro.
 * */

public class RegistroOffsetWord implements Serializable<RegistroOffsetWord> {

	private Long blocknumber;
	private Short objectnumber;
	private String palabra;
	
	

	public RegistroOffsetWord( BlockAddress<Long, Short> offset, String unapalabra )
	{
		blocknumber = offset.getBlockNumber();
		objectnumber = offset.getObjectNumber();
		palabra = unapalabra;
	}
	
	public VariableLengthAddress getOffset() 
	{
		return new VariableLengthAddress( blocknumber, objectnumber );
	}
	
	
	public String getPalabra() 
	{
		return palabra;
	}
	
	
	public void setOffset(BlockAddress<Long,Short> unoffset) 
	{
		this.blocknumber = unoffset.getBlockNumber();
		this.objectnumber = unoffset.getObjectNumber();
	}
	
	
	public void setPalabra(String palabra) 
	{
		this.palabra = palabra;
	}
	
	@Override
	public Serializer<RegistroOffsetWord> getSerializer() {
		return getSerializerStatic();
	}
	
	public static Serializer<RegistroOffsetWord> getSerializerStatic() {
		
		return new Serializer<RegistroOffsetWord>()
		{

			private StringSerializerDelimiter stringSerializer = new StringSerializerDelimiter( new byte[] {(byte)0, (byte)(0)});
			private LongSerializer longserializer = SerializerCache.getInstance().getSerializer(LongSerializer.class);
			private ShortSerializer shortserializer = SerializerCache.getInstance().getSerializer(ShortSerializer.class);
			
			
			/**
			 * Deshidrata el registro en el siguiente órden:
			 * blocknumber(Long)-objectnumber(Short)-palabra(String)
			 * */
			
			@Override
			public void dehydrate(OutputBuffer output, RegistroOffsetWord object) {
				longserializer.dehydrate(output, object.getOffset().getBlockNumber() );
				shortserializer.dehydrate(output, object.getOffset().getObjectNumber() );
				stringSerializer.dehydrate( output, object.getPalabra() );
			}

			@Override
			public long getDehydrateSize(RegistroOffsetWord object) {
				
				return ( 10 + object.getPalabra().length() );
			}

			/**
			 * Hidrata el registro en el siguiente órden:
			 * blocknumber(Long)-objectnumber(Short)-palabra(String)
			 * */
			@Override
			public RegistroOffsetWord hydrate(InputBuffer input) {
				
				Long blockNumber = longserializer.hydrate( input );
				Short objectNumber = shortserializer.hydrate( input );
				String word = stringSerializer.hydrate( input );
				
				RegistroOffsetWord reg = new RegistroOffsetWord( new VariableLengthAddress(blockNumber,objectNumber),word );
				return reg;
			}
		};
		
	}
	
}
