package ar.com.datos.persistencia.variableLength;

import java.io.InputStream;
import java.util.Iterator;

import ar.com.datos.audio.AnotherInputStream;
import ar.com.datos.file.Address;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.exception.UnregisteredWordException;
import ar.com.datos.persistencia.exception.WordIsAlreadyRegisteredException;
import ar.com.datos.persistencia.variableLength.registros.RegistroInputStream;
import ar.com.datos.persistencia.variableLength.registros.RegistroOffsetWord;


/**
 *
 *Sound Persistence Service implementado con archivos de registro de 
 *longitud variable en bloques.
 */

public class SoundPersistenceServiceVariableLengthImpl implements SoundPersistenceService{

	//Archivos de palabras y de sonidos.
	private DynamicAccesor<RegistroOffsetWord> accesoapalabras;
	private DynamicAccesor<RegistroInputStream> accesoasonidos;
	
	
	//path de los archivos de palabras y de sonidos
	private String nombrearchivodepalabras;
	private String nombrearchivodesonidos;
	
	
	
	//tama�o de bloques por default
	public static  Integer BLOCK_SIZE_WORDS = 16;
	public static  Integer BLOCK_SIZE_INPUTSTREAM = 1024;
	
	
	
	//path de archivos por default
	private static  String DEFAULT_NAME_FILE_WORDS = "./resources/temp/palabras";
	private static  String DEFAULT_NAME_FILE_STREAM = "./resources/temp/sonidos";
	
	
	

	/**
	 * Constructor sin argumentos.Configura nombres de archivos por default.
	 * */
	
	public SoundPersistenceServiceVariableLengthImpl(){
		
		nombrearchivodepalabras = DEFAULT_NAME_FILE_WORDS;
		nombrearchivodesonidos = DEFAULT_NAME_FILE_STREAM;
		
		init();
	}
	
	
	
	/**
	 * Constructor con argumentos.Permite definir el path de los archivos
	 * en donde se guardan, y de los cuales se obtienen, palabras y sonidos.
	 * */
	
	public SoundPersistenceServiceVariableLengthImpl( String pathwords, String pathsounds ){
		
		nombrearchivodepalabras = pathwords;
		nombrearchivodesonidos = pathsounds;
		
		init();
	}
	
	
	/**
	 * Instanciaci�n y configuraci�n de archivos.
	 * Los deja listos para trabajar.
	 * */
	
	private void init() {
		accesoapalabras = new VariableLengthFileManager<RegistroOffsetWord>(nombrearchivodepalabras,BLOCK_SIZE_WORDS, RegistroOffsetWord.getSerializerStatic());
		accesoasonidos = new VariableLengthFileManager<RegistroInputStream>(nombrearchivodesonidos,BLOCK_SIZE_INPUTSTREAM, RegistroInputStream.getSerializerStatic());
	}



	@Override
	public void addWord(String word, AnotherInputStream stream)
			throws WordIsAlreadyRegisteredException {
		
		Address<Long, Short> offset = getOffsetOfWord( word );
		if ( offset != null ) throw new WordIsAlreadyRegisteredException();
		else
		{
			RegistroInputStream regis = new RegistroInputStream( stream );
			offset = accesoasonidos.addEntity( regis );
			
			RegistroOffsetWord registropalabra = new RegistroOffsetWord (offset,word);
			accesoapalabras.addEntity( registropalabra );
		}
		
	}



	@Override
	public boolean isRegistered(String word) {
		return ( getOffsetOfWord(word)!= null );
	}



	@Override
	public InputStream readWord(String word) throws UnregisteredWordException {
		
		Address<Long, Short> offset = getOffsetOfWord( word );
		
		if ( offset == null ) throw new UnregisteredWordException();
		else
			{
				RegistroInputStream regis = accesoasonidos.get( offset );
				return regis.getStream();
			}
	}

	
	
	/**
	 * Devuelve el offset de una determinada palabra.
	 * retorna null si la palabra no est� en el archivo.
	 *  
	 * */
	
	public Address<Long, Short> getOffsetOfWord( String word )
	{
		Iterator<RegistroOffsetWord> it = accesoapalabras.iterator();
		
		while ( it.hasNext() )
		{
			RegistroOffsetWord reg = it.next();
			if (reg.getPalabra().equals(word)) return reg.getOffset();
		}
		return null;
	}


	
}