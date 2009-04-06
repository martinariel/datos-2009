package ar.com.datos.test.persistencia;

import java.io.*;

import junit.framework.TestCase;

import ar.com.datos.audio.AnotherInputStream;
import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.exception.UnregisteredWordException;
import ar.com.datos.persistencia.exception.WordIsAlreadyRegisteredException;

/**
 * 
 * Test sobre la clase SoundPersistenceService.No se testea la persistencia de los datos
 * sino el funcionamiento de los algoritmos de consulta e incersión.
 * Se utilizan modulos ficticios para trabajar.FictFile y  
 * SoundPersistenceServiceMemoryImpl que implementan la misma funcionalidad pero guardando 
 * los datos en memoria.
 * 
 * */
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
		AnotherInputStream streamoriginal = new AnotherInputStream( palabradeprueba.getBytes() );
		
		//Test verificar que la palabra no estï¿½.
		
		assertFalse( sps.isRegistered( palabradeprueba ));
		
		//Test agregar palabra que no estï¿½.
		try
		{
			sps.addWord( palabradeprueba , streamoriginal );
		}
		catch ( WordIsAlreadyRegisteredException ex ) { fail("La Excepción no debería ser lanzada"); }
		
		
		//Test verificar que la palabra estï¿½.
		assertTrue( sps.isRegistered( palabradeprueba ));
		
		
		//Test recuperar palabra correctamente
		try
		{
			InputStream streamrecuperdo = sps.readWord( palabradeprueba );
			assertTrue( streamoriginal.equals( streamrecuperdo ) );
		}
		catch ( UnregisteredWordException ex ){ fail("La Excepción no debería ser lanzada"); }
		
		
		//Test agregar la misma palabra otra vez.
		String palabrasimilar = palabradeprueba.toString();
		AnotherInputStream streamdistinto = new AnotherInputStream( new String("streamfalso").getBytes());
		try
		{
			sps.addWord( palabrasimilar , streamdistinto );
			fail("Se debería lanzar una excepción");
		}
		catch ( WordIsAlreadyRegisteredException ex )
		{}
		
		
		
		//Test verificar que el stream de la palabra original no 
		//se modificï¿½.
		try
		{
			InputStream streamrecuperado = sps.readWord( palabradeprueba );
			assertTrue( streamrecuperado.equals( streamoriginal ) );
			assertFalse( streamrecuperado.equals( streamdistinto ) );

		}
		catch ( UnregisteredWordException ex ){ fail("La Excepción no debería ser lanzada"); }
		
		
	}
	
	public void testExcepciones()
	{
		limpiarSPS();
		String palabradeprueba = "palabra";
		AnotherInputStream streamdeprueba = new AnotherInputStream( palabradeprueba.toString().getBytes() );
		
		AnotherInputStream streamfalso = new AnotherInputStream( new String("otracadena").getBytes() );
		
		//Agrego 3 palabras nuevas distintas a la de prueba.
		try
		{
			
			sps.addWord( "hola" , streamfalso );
			sps.addWord( "que" , streamfalso );
			sps.addWord( "tal" , streamfalso );
			
		}
		catch ( WordIsAlreadyRegisteredException ex ) { fail("La Excepción no debería ser lanzada"); }
		
		//pido la palabra de prueba que no estï¿½.
		
		try
		{
			sps.readWord( palabradeprueba );
			fail("Debería lanzarse una excepcion");		}
		catch ( UnregisteredWordException ex ){ }
		
		//Agrego la palabra dos veces, la seguna deberï¿½a tirar la excepcion
		
		try
		{ sps.addWord(palabradeprueba,streamdeprueba); }
		catch ( WordIsAlreadyRegisteredException ex ){}
		
		try
		{ sps.addWord(palabradeprueba,streamdeprueba);fail("Debería lanzarse una excepción"); }
		catch ( WordIsAlreadyRegisteredException ex ){ }
	}
	
	public void testMultiplesPalabras()
	{
		limpiarSPS();
		
		String palabradeprueba = "palabra";
		AnotherInputStream streamdeprueba = new AnotherInputStream( palabradeprueba.toString().getBytes() );
		
		AnotherInputStream streamfalso = new AnotherInputStream( new String("otracadena").getBytes() );
		
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
			sps.addWord( "ï¿½?" , streamfalso );
			
		}
		catch ( WordIsAlreadyRegisteredException ex ) { fail("La Excepción no debería ser lanzada"); }
		
		//pregunto si estï¿½ la palabra de prueba.
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
		AnotherInputStream streamdistinto = new AnotherInputStream( new String("streamfalso").getBytes());
		try
		{
			sps.addWord( palabrasimilar , streamdistinto );
			fail("Debería lanzarse la excepción");
		}
		catch ( WordIsAlreadyRegisteredException ex )
		{}
	}
}
