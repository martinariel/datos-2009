package ar.com.datos.wordandsoundservice.variableLength;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Queue;

import ar.com.datos.wordandsoundservice.WaSService;
import ar.com.datos.wordandsoundservice.exception.*;

import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.file.variableLength.VariableLengthAddress;
import ar.com.datos.wordandsoundservice.variableLength.serializer.*;
import ar.com.datos.wordandsoundservice.variableLength.registros.RegistroOffsetWord;
import ar.com.datos.wordandsoundservice.variableLength.registros.RegistroInputStream;

import ar.com.datos.file.Address;

/**
 *Word and Sound Service implementado con archivos de registros de longitud variable
 *en Bloques. 
 */


public class WaSSVariableLengthImpl implements WaSService{

	public static  Integer BLOCK_SIZE_WORDS = 1000;
	public static  Integer BLOCK_SIZE_INPUTSTREAM = 1000;
	
	private static  String DEFAULT_NAME_FILE_WORDS = "C:/palabras";
	private static  String DEFAULT_NAME_FILE_STREAM = "C:/sonidos";
	
	private String nombrearchivodepalabras;
	private String nombrearchivodesonidos;
	
	
	private VariableLengthFileManager archivodepalabras;
	private VariableLengthFileManager archivodesonidos;
	
	
	
	public WaSSVariableLengthImpl()
	{
		nombrearchivodepalabras = DEFAULT_NAME_FILE_WORDS;
		nombrearchivodesonidos = DEFAULT_NAME_FILE_STREAM;
		
		init();
	}
	
	
	
	private void init()
	{
		archivodepalabras = new VariableLengthFileManager( nombrearchivodepalabras,
														   BLOCK_SIZE_WORDS,
														   new OffsetWordSerializer() );
		
		archivodesonidos = new VariableLengthFileManager( nombrearchivodesonidos,
				   										  BLOCK_SIZE_INPUTSTREAM,
				   										  new InputStreamSerializer() );
	}
	
	
	@Override
	public void addWord(String word, InputStream stream)
			throws WordIsAlreadyRegisteredException {
		
		try
		{
			VariableLengthAddress offset = getOffsetOfWord( word );
			throw new WordIsAlreadyRegisteredException();
		}
		catch( UnregisteredWordException ex ){}
		{
			RegistroInputStream regstream = new RegistroInputStream( stream ); 
			Address<><> offset = archivodesonidos.addEntity( regstream.toQueue() );
			archivodepalabras.addEntity( reg.toQueue() );
		}
	}
	
	
	
	
	
	@Override
	public InputStream readWord(String word) throws UnregisteredWordException {
		
		VariableLengthAddress offset = getOffsetOfWord( word );
		RegistroInputStream ris = new RegistroInputStream(archivodesonidos.get( offset ));
		return ris.getStream();
	}
	
	
	
	
	
	private VariableLengthAddress getOffsetOfWord ( String word )
		throws UnregisteredWordException
	{
		Iterator<Queue<Object>> itpalabras = archivodepalabras.iterator();
		boolean encontrado = false;
	
		RegistroOffsetWord regpalabra;
		
		while ( (!encontrado) || ( itpalabras.hasNext() ) )
		{
			Queue<Object> coladeregistro = new Queue<Object>;
			regpalabra = new RegistroOffsetWord( coladeregistro );
			if ( regpalabra.getPalabra() == word ) encontrado = true;
		}

		if ( encontrado ){ return regpalabra.getOffset(); }
		else throw new UnregisteredWordException();
	
	}
}
