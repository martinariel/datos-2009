package ar.com.datos.wordandsoundservice.variableLength.serializer;

import java.util.Queue;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.file.variableLength.VariableLengthAddress;
import ar.com.datos.serializer.QueueSerializer;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import java.io.InputStream;
import java.io.ByteArrayInputStream;


public class InputStreamSerializer implements QueueSerializer {

	@Override
	public void dehydrate(OutputBuffer output, Queue<Object> object) {
		
		InputStream stream = (InputStream)object.poll();
		
		String Sstream = stream.toString();
		int streamsize = Sstream.length();
		char[] streamchars = Sstream.toCharArray();
		
		output.write( PrimitiveTypeSerializer.toByte( streamsize ) );
		output.write( PrimitiveTypeSerializer.toByte( streamchars ) );
		
	}

	@Override
	public long getDehydrateSize(Queue<Object> object) {
		
		InputStream stream = (InputStream)object.poll();
		String Sstream = stream.toString();
		return ( Sstream.length() + 4 );
	}

	@Override
	public Queue<Object> hydrate(InputBuffer input) {
		
		//Leo los bytes correspondientes a un long, un short y a un int 
		byte[] data = new byte[4];
		data = input.read( data );
		ArrayByte arraydata = new ArrayByte( data );  
		
		int streamsize = PrimitiveTypeSerializer.toInt( arraydata.getArray() );
		
		data = new byte[4 + streamsize];
		data = input.read( data );
		arraydata = new ArrayByte( data );  
		
		
		InputStream stream = new ByteArrayInputStream( arraydata.getRightSubArray( streamsize +1 ).getArray() );
		
		Queue<Object> campo = new Queue<Object>();
		campo.add( stream );
		
		return campo;
	}


}
