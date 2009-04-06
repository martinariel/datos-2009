package ar.com.datos.test.audio;

import java.io.ByteArrayOutputStream;
import ar.com.datos.audio.AudioServiceHandler;
import junit.framework.TestCase;

public class TestAudioServiceHandler extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetInstance() {

		AudioServiceHandler service1 = AudioServiceHandler.getInstance();
		AudioServiceHandler service2 = AudioServiceHandler.getInstance();

		assertTrue(service1 == service2);


	}

	public void testIsRecording() {

		ByteArrayOutputStream audio = new ByteArrayOutputStream();
		AudioServiceHandler service = AudioServiceHandler.getInstance();

		service.record(audio);
		assertTrue(service.isRecording());
		service.stopRecording();
		assertFalse(service.isRecording());

	}

	public void testIsPlaying() {
		fail("Not yet implemented");
	}

	public void testRecord() {
		ByteArrayOutputStream audio = new ByteArrayOutputStream();
		AudioServiceHandler service = AudioServiceHandler.getInstance();

		service.record(audio);

		try {
			Thread.sleep(1000);
		}
		catch (Exception e){

		}
		service.stopRecording();

		assertTrue (audio.size() > 0);

	}

	public void testPlay() {
		//fail("Not yet implemented");
	}

	public void testStopRecording() {
		AudioServiceHandler service = AudioServiceHandler.getInstance();
		service.stopRecording();
		assertFalse(service.isRecording());
	}

	public void testStopPlaying() {
		AudioServiceHandler service = AudioServiceHandler.getInstance();
		service.stopPlaying();
		assertFalse(service.isPlaying());

	}

}
