package ar.com.datos.test.persistencia;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import ar.com.datos.audio.AnotherInputStream;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.exception.UnregisteredWordException;
import ar.com.datos.persistencia.exception.WordIsAlreadyRegisteredException;
import ar.com.datos.persistencia.variableLength.registros.RegistroInputStream;
import ar.com.datos.persistencia.variableLength.registros.RegistroOffsetWord;


/**
 *
 *Sound Persistence Service implementado con listas en memoria.Se utiliza para 
 *testear el funcionamiento de los algoritmos de busqueda e incersi�n de datos.
 */

public class SoundPersistenceServiceMemoryImpl implements SoundPersistenceService{

	//Archivos de palabras y de sonidos.
	private DynamicAccesor<BlockAddress<Long, Short>, RegistroOffsetWord> accesoapalabras;
	private DynamicAccesor<BlockAddress<Long, Short>, RegistroInputStream> accesoasonidos;
	
	
	//tama�o de bloques por default
	public static  Integer BLOCK_SIZE_WORDS = 1000;
	public static  Integer BLOCK_SIZE_INPUTSTREAM = 1000;
	
	
	/**
	 * Constructor sin argumentos.Configura nombres de archivos por default.
	 * */
	
	public SoundPersistenceServiceMemoryImpl(){
		init();
	}
	
	
	
	/**
	 * Constructor con argumentos.Permite definir el path de los archivos
	 * en donde se guardan, y de los cuales se obtienen, palabras y sonidos.
	 * */
	
	public SoundPersistenceServiceMemoryImpl( String pathwords, String pathsounds ){
		init();
	}
	
	
	/**
	 * Instanciaci�n y configuraci�n de archivos.
	 * Los deja listos para trabajar.
	 * */
	
	private void init() {
		accesoapalabras = new FictFile<RegistroOffsetWord>();
		accesoasonidos = new FictFile<RegistroInputStream>();
	}



	@Override
	public void addWord(String word, AnotherInputStream stream)
			throws WordIsAlreadyRegisteredException {
		
		BlockAddress<Long, Short> offset = getOffsetOfWord( word );
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
		
		BlockAddress<Long, Short> offset = getOffsetOfWord( word );
		
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
	
	public BlockAddress<Long, Short> getOffsetOfWord( String word )
	{
		Iterator<RegistroOffsetWord> it = accesoapalabras.iterator();
		
		while ( it.hasNext() )
		{
			RegistroOffsetWord reg = it.next();
			if (reg.getPalabra().equals(word)) return reg.getOffset();
		}
		return null;
	}



	@Override
	public void close() throws IOException {
		
	}


	
}
