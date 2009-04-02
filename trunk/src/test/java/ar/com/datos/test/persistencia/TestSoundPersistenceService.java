package ar.com.datos.test.persistencia;

import java.io.*;

import junit.framework.TestCase;

import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.exception.UnregisteredWordException;
import ar.com.datos.persistencia.exception.WordIsAlreadyRegisteredException;

public class TestSoundPersistenceService extends TestCase {

	private SoundPersistenceService sps = new SoundPersistenceServiceMemoryImpl();
	
	
	private void limpiarSPS()
	{
		sps = new SoundPersistenceServiceMemoryImpl();
	}
	
	public void testSoundPersistentServicePalabra()
	{
		//Prueba de agregar palabra, preguntar si esta y recuperar 
		//el stream correspondiente.
		
		//Inicializo palabras y Strings
		String palabradeprueba = "palabra";
		InputStream streamoriginal = new ByteArrayInputStream( palabradeprueba.getBytes() );
		
		//Test verificar que la palabra no está.
		
		assertFalse( sps.isRegistered( palabradeprueba ));
		
		//Test agregar palabra que no está.
		try
		{
			sps.addWord( palabradeprueba , streamoriginal );
			assertTrue( true );
		}
		catch ( WordIsAlreadyRegisteredException ex ) { assertTrue(false); }
		
		
		//Test verificar que la palabra está.
		assertTrue( sps.isRegistered( palabradeprueba ));
		
		
		//Test recuperar palabra correctamente
		try
		{
			InputStream streamrecuperdo = sps.readWord( palabradeprueba );
			assertTrue( true );
			assertTrue( streamoriginal.equals( streamrecuperdo ) );
		}
		catch ( UnregisteredWordException ex ){}
		
		
		//Test agregar la misma palabra otra vez.
		String palabrasimilar = palabradeprueba.toString();
		InputStream streamdistinto = new ByteArrayInputStream( new String("streamfalso").getBytes());
		try
		{
			sps.addWord( palabrasimilar , streamdistinto );
			assertTrue( false );
		}
		catch ( WordIsAlreadyRegisteredException ex )
		{ assertTrue(true); }
		
		
		
		//Test verificar que el stream de la palabra original no 
		//se modificó.
		try
		{
			InputStream streamrecuperado = sps.readWord( palabradeprueba );
			assertTrue( true );
			assertTrue( streamrecuperado.equals( streamoriginal ) );
			assertFalse( streamrecuperado.equals( streamdistinto ) );

		}
		catch ( UnregisteredWordException ex ){ assertTrue(false); }
		
		
	}
	
	public void testExcepciones()
	{
		limpiarSPS();
		String palabradeprueba = "palabra";
		InputStream streamdeprueba = new ByteArrayInputStream( palabradeprueba.toString().getBytes() );
		
		InputStream streamfalso = new ByteArrayInputStream( new String("otracadena").getBytes() );
		
		//Agrego 3 palabras nuevas distintas a la de prueba.
		try
		{
			
			sps.addWord( "hola" , streamfalso );
			sps.addWord( "que" , streamfalso );
			sps.addWord( "tal" , streamfalso );
			
			assertTrue( true );
		}
		catch ( WordIsAlreadyRegisteredException ex ) { assertTrue(false); }
		
		//pido la palabra de prueba que no está.
		
		try
		{
			sps.readWord( palabradeprueba );
			assertTrue( false );
		}
		catch ( UnregisteredWordException ex ){ assertTrue( true ); }
		
		//Agrego la palabra dos veces, la seguna debería tirar la excepcion
		
		try
		{ sps.addWord(palabradeprueba,streamdeprueba); }
		catch ( WordIsAlreadyRegisteredException ex ){}
		
		try
		{ sps.addWord(palabradeprueba,streamdeprueba);assertTrue(false); }
		catch ( WordIsAlreadyRegisteredException ex ){ assertTrue(true); }
	}
	
	public void testMúltiplesPalabras()
	{
		limpiarSPS();
		
		String palabradeprueba = "palabra";
		InputStream streamdeprueba = new ByteArrayInputStream( palabradeprueba.toString().getBytes() );
		
		InputStream streamfalso = new ByteArrayInputStream( new String("otracadena").getBytes() );
		
		//Agrego la palabra de prueba en el medio de otras.
		try
		{
			
			sps.addWord( "hola" , streamfalso );
			sps.addWord( "que" , streamfalso );
			sps.addWord( "tal" , streamfalso );
			sps.addWord( "saludo" , streamfalso );
			sps.addWord( "pais" , streamfalso );
			sps.addWord( "provincia" , streamfalso );
			
			sps.addWord( palabradeprueba , streamdeprueba );
			
			sps.addWord( "" , streamfalso );
			sps.addWord( "!!!" , streamfalso );
			sps.addWord( "perro" , streamfalso );
			sps.addWord( "¿?" , streamfalso );
			
			assertTrue( true );
		}
		catch ( WordIsAlreadyRegisteredException ex ) { assertTrue(false); }
		
		//pregunto si está la palabra de prueba.
		assertTrue( sps.isRegistered( palabradeprueba ));
		
		//Verifico que el stream sea el correcto
		try
		{
			InputStream streamobtenido= sps.readWord( palabradeprueba );
			assertEquals( streamdeprueba , streamobtenido );
		}
		catch ( UnregisteredWordException ex ){}
		
		//Intento agregarla de vuelta
		
		String palabrasimilar = palabradeprueba.toString();
		InputStream streamdistinto = new ByteArrayInputStream( new String("streamfalso").getBytes());
		try
		{
			sps.addWord( palabrasimilar , streamdistinto );
			assertTrue( false );
		}
		catch ( WordIsAlreadyRegisteredException ex )
		{ assertTrue(true); }
	}
}
