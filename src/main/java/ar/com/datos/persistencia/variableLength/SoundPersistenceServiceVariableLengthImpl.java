package ar.com.datos.persistencia.variableLength;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Queue;

import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.exception.*;
import ar.com.datos.persistencia.variableLength.registros.*;

import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.file.DynamicAccesor;


/**
 *Word and Sound Service implementado con archivos de registros de longitud variable
 *en Bloques. 
 */


public class SoundPersistenceServiceVariableLengthImpl implements SoundPersistenceService{

	private DynamicAccesor<RegistroOffsetWord> accesoapalabras;
	private DynamicAccesor<RegistroInputStream> accesosonidos;
	
	//path de los archivos de palabras y de sonidos
	private String nombrearchivodepalabras;
	private String nombrearchivodesonidos;
	
	
	//tamaño de bloques por default
	public static  Integer BLOCK_SIZE_WORDS = 1000;
	public static  Integer BLOCK_SIZE_INPUTSTREAM = 1000;
	
	//path de archivos por default
	private static  String DEFAULT_NAME_FILE_WORDS = "C:/palabras";
	private static  String DEFAULT_NAME_FILE_STREAM = "C:/sonidos";
	

	public SoundPersistenceServiceVariableLengthImpl()
	{
		nombrearchivodepalabras = DEFAULT_NAME_FILE_WORDS;
		nombrearchivodesonidos = DEFAULT_NAME_FILE_STREAM;
		
		init();
	}
	
	
	
	private void init()
	{
		accesoapalabras = new VariableLengthFileManager<RegistroOffsetWord>(nombrearchivodepalabras,BLOCK_SIZE_WORDS);
		accesosonidos = new VariableLengthFileManager<RegistroInputStream>(nombrearchivodesonidos,BLOCK_SIZE_INPUTSTREAM);
	}



	@Override
	public void addWord(String word, InputStream stream)
			throws WordIsAlreadyRegisteredException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean isRegistered(String word) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public InputStream readWord(String word) throws UnregisteredWordException {
		// TODO Auto-generated method stub
		return null;
	}




	
}
