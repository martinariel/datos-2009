package ar.com.datos.parser;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.io.*;

/**
 * Implementacion de un parser utilizando expresiones regulares
 *
 * @author mfernandez
 *
 */
public abstract class AbstractTextParser implements IParser {


	public Collection<String> parseTextFile(String filePath) throws IOException {

		File archivo = new File(filePath);

		List<String> resultado = new LinkedList<String>();
		BufferedReader reader = null;

		try {

			reader = new BufferedReader(new FileReader(archivo));
		    for (String linea = reader.readLine(); linea != null; linea = reader.readLine()) {
		       procesarLinea(normalizarLinea(linea), resultado);
		     }

		}
		catch (IOException e){
			throw e;
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				}
				catch (IOException e) {}
			}
		}

		return resultado;

	}

	/**
	 * @param linea
	 * @return Linea normalizada, en minuscula, solo con las palabras y delimitador
	 * " "
	 **/
	private String normalizarLinea(String linea){

		//TODO mejorar esto
		linea = linea.toLowerCase();
		linea = linea.replaceAll("á", "a");
		linea = linea.replaceAll("é", "e");
		linea = linea.replaceAll("í", "i");
		linea = linea.replaceAll("ó", "o");
		linea = linea.replaceAll("í", "u");
		linea = linea.replaceAll("[^a-zA-Z]", " ");
		linea = linea.replaceAll("  ", " ");

		return linea;
	}

	/**
	 *
	 * @param linea
	 * @param resultado
	 */
	protected abstract void procesarLinea(String linea, Collection<String> resultado);

}
