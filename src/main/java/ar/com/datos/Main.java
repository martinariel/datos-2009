package ar.com.datos;

import ar.com.datos.audio.AudioServiceHandler;
import ar.com.datos.audio.exception.AudioServiceHandlerException;

import ar.com.datos.wordandsoundservice.WaSService;
import ar.com.datos.wordandsoundservice.variableLength.WaSSVariableLengthImpl;

import ar.com.datos.parser.IParser;
import ar.com.datos.parser.SimpleTextParser;

/**
 *
 *
 * @author mfernandez
 *
 */

public class Main {

	private AudioServiceHandler servicioAudio;
	private WaSService servicioArchivos;
	private IParser parser;


	public Main() {

		servicioAudio 	 = new AudioServiceHandler();
		servicioArchivos = new WaSSVariableLengthImpl();
		parser			 = new SimpleTextParser();


	}

	public void init() {
		showMenu();
	}

	private void showMenu() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Main app = new Main();
		app.init();
	}

}
