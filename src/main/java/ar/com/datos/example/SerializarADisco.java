package ar.com.datos.example;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import ar.com.datos.serializer.HydrateInfo;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.ShortSerializer;
import ar.com.datos.serializer.common.StringSerializerDelimiter;

/**
 * Ejemplo !!
 * 
 * @author fvalido
 */
public class SerializarADisco {
	public static void main(String[] args) throws IOException {
		// Recomiendo que lean todo el javadoc de Serializer y clases relacionadas para 
		// entender el ejemplo, igual comento lo que voy haciendo.
		
		// Creo un archivo de acceso aleatorio.
		File file = new File("resources/test/prueba.dat");
		RandomAccessFile fileHandler = new RandomAccessFile(file,"rw");
		
		// Voy a serializar un String y un Short.
		String testString = "hola como va ?";
		short testShort = 23;
		
		// Un SERIALIZADOR permite deshidratar un objeto en una tira de bytes e
		// hidratarla de nuevo en el objeto original. Cada serializador sabe
		// deshidratar e hidratar un tipo en particular, y se pueden crear nuevos
		// serializadores de acuerdo a nuestras necesidades. Yo ya programe serializadores
		// para todos los tipos primitivos y String.		
		
		
		// Creo los serializadores a usar
		Serializer<Short> shortSerializer = (ShortSerializer)SerializerCache.getInstance().getSerializer(ShortSerializer.class);
		// Ver javadoc de StringSerializerDelimiter para entender el constructor...
		// Hay tambien un StringSerializerSize, quizas mirarlo les haga entender mejor el
		// concepto.
		Serializer<String> stringSerializer = new StringSerializerDelimiter(new byte[] {(byte)0, (byte)0});
		
		// Hago la deshidratacion. Obtengo como resultado una tira de bytes.		
		
		// Deshidrato el String
		byte[] stringBytes = stringSerializer.dehydrate(testString);
		
		// Deshidrato el short
		byte[] shortBytes = shortSerializer.dehydrate(testShort);
		
		
		// Concateno las dos tiras de Bytes en una sola que sera lo que escriba en el archivo.
		byte[] writeBytes = new byte[stringSerializer.getDehydrateSize(testString) + shortSerializer.getDehydrateSize(testShort)];
		// La linea de arriba es lo mismo que:
		//    byte[] writeBytes = new byte[stringBytes.length + shortBytes.length];
		// Pero supongo que en algun caso que no recuerdo me era util lo otro.
		System.arraycopy(stringBytes, 0, writeBytes, 0, stringBytes.length);
		System.arraycopy(shortBytes, 0, writeBytes, stringBytes.length, shortBytes.length);
		
		// Tengo la tira de bytes a escribir. La escribo en el archivo.
		fileHandler.seek(0);
		fileHandler.write(writeBytes);
		
		// Cierro el archivo y lo abro de nuevo.
		fileHandler.close();
		fileHandler = new RandomAccessFile(file,"r");
		
		// Leo la tira de bytes desde el archivo.
		// Tengo que saber cuantos bytes voy a leer desde el archivo.
		byte[] readBytes = new byte[writeBytes.length];
		fileHandler.seek(0);
		fileHandler.readFully(readBytes);
		
		// Ok... Tengo la tira de bytes... Ahora necesito rehidratarla. Para ello necesito
		// saber previamente la estructura de la tira de bytes a hidratar... Es decir... Yo se que
		// primero hay algo que fue deshidratado usando stringSerializer y a continuacion algo que
		// fue deshidratado usando shortSerializer. Es decir, tengo que hidratar los primeros
		// bytes usando el stringSerializer y luego, a los bytes que "restan" aplicarle el
		// shortSerializer.
		// Pero... como se cuantos bytes restan ? O peor aun: como se que parte de la tira de bytes
		// pasarle al metodo de hidratacion ? (En este ejemplo obviamente lo se, pero pensemos en
		// una tira de bytes que contiene deshidratados varios objetos que guarde antes pero que
		// ahora al levantarlos no tengo idea de cuantos bytes de la tira se corresponden con cada
		// objeto).
		// SOLUCION: Bueno, algo que si puedo saber a la hora de hidratar (y necesito saberlo si
		// o si) es que hidratador debo usar para levantar cada objeto y en que orden de secuencia
		// (en el ejemplo actual primero es un stringSerializer y luego un shortSerializer). El 
		// metodo hydrate recibe una tira de bytes que puede contener mas bytes de los que precisa 
		// ese objeto para ser hidratado; es decir, en nuestro ejemplo le podemos pasar al hidratador
		// la tira de bytes completa leido del archivo. El hidratador usa los bytes que necesita
		// empezando por el byte 0 (es decir que necesitamos que el obtejo deshidratado este al
		// principio de la tira de bytes) y devuelve el objeto hidratado junto con los bytes
		// sobrantes (los que estan a la derecha del objeto deshidratado en la tira que le pasamos).
		// De esta manera con solo saber que hidratador usar y en que orden podemos hidratar
		// varios objetos que deshidratados tienen un tamanio variable.
		// Nota: La agrupacion del objeto hidratado y los bytes sobrantes se llama HydrateInfo.
		
		// Bueno, entonces puedo hidratar:
		// A esta llamada de hydrate le paso la tira de bytes que lei antes.
		HydrateInfo<String> hydrateInfoString = stringSerializer.hydrate(readBytes);
		String testStringRead = hydrateInfoString.getHydratedObject();
		
		// A esta llamada de hydrate le paso los bytes que restaron de la anterior llamada.
		HydrateInfo<Short> hydrateInfoShort = shortSerializer.hydrate(hydrateInfoString.getHydrateRemaining());
		short testShortRead = hydrateInfoShort.getHydratedObject();
		
		// Obviamente tuvieron que sobrar 0 Bytes
		System.out.println(hydrateInfoShort.getHydrateRemaining().length);
		
		// Imprimo los objetos rehidratados
		System.out.println(testStringRead);
		System.out.println(testShortRead);
		
		// Borro el archivo creado
		file.delete();		
	}
}
