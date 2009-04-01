package ar.com.datos.wordandsoundservice.variableLength.serializer;

import java.util.Queue;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.file.variableLength.VariableLengthAddress;
import ar.com.datos.serializer.QueueSerializer;
import ar.com.datos.serializer.PrimitiveTypeSerializer;


/**
 * Serializa y desserializa una cola que contiene Address(offset)y palabra.
 * El address es la dirección del InputStream de la palabra , dentro del archivo 
 * de sonidos.
 * 
 * @author Raul Lopez
 *
 */


public class OffsetWordSerializer implements QueueSerializer{

	@Override
	public void dehydrate(OutputBuffer output, Queue<Object> object) {
		
		VariableLengthAddress addresssonido = (VariableLengthAddress)object.poll();
		String palabra = (String)object.poll();
		
		
		long blocknumber = addresssonido.getBlockNumber().longValue();
		short objectnumber = addresssonido.getObjectNumber().shortValue();
		
		int palabrasize = palabra.length();
		char[] palabrachars = palabra.toCharArray();
		
		output.write( PrimitiveTypeSerializer.toByte( blocknumber ) );
		output.write( PrimitiveTypeSerializer.toByte( objectnumber ) );
		output.write( PrimitiveTypeSerializer.toByte( palabrasize ) );
		output.write( PrimitiveTypeSerializer.toByte( palabrachars ) );
		
	}

	@Override
	public long getDehydrateSize(Queue<Object> object) {
		
		VariableLengthAddress address = (VariableLengthAddress)object.poll();
		String palabra = (String)object.poll();
		
		return ( palabra.length() + 14 );
	}

	@Override
	public Queue<Object> hydrate(InputBuffer input) {
		
		//Leo los bytes correspondientes a un long, un short y a un int 
		byte[] data = new byte[14];
		data = input.read( data );
		ArrayByte arraydata = new ArrayByte( data );  
		
		//El entero representa el tamaño de la cadena de caracteres.
		int palabrasize = PrimitiveTypeSerializer.toInt( arraydata.getRightSubArray(10).getArray() );
		
		data = new byte[14 + palabrasize];
		data = input.read( data );
		arraydata = new ArrayByte( data );  
		
		char[] palabraarray = PrimitiveTypeSerializer.toCharArray( arraydata.getRightSubArray(palabrasize +1).getArray() );
		long blocknumber = PrimitiveTypeSerializer.toLong( arraydata.getLeftSubArray(7).getArray() );
		short objectnumber = PrimitiveTypeSerializer.toShort( arraydata.getSubArray(8, 9).getArray() );
		
		Long Lblocknumber = new Long( blocknumber );
		Short Sobjectnumber = new Short( objectnumber );
		String palabra = new String( palabraarray );
		
		VariableLengthAddress address = new VariableLengthAddress( Lblocknumber, Sobjectnumber );
		
		Queue<Object> loscampos = new Queue<Object>();
		loscampos.add( address );
		loscampos.add( palabra );
		
		return loscampos;
	}

}
