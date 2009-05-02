package ar.com.datos.indexer.lexic;

import java.io.Closeable;
import java.io.IOException;

import ar.com.datos.file.exception.OutOfBoundsException;
import ar.com.datos.file.variableLength.StraightVariableLengthFile;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.indexer.lexic.serializer.LexicalSerializer;
/**
 * Administrador del léxico completo.
 * Es un thin-Wrapper de un archivo secuencial de acceso dinámico no actualizable
 * @author jbarreneche
 *
 */
public class LexicalManager implements Closeable {

	private StraightVariableLengthFile<LexicalData> lexical;
	private LexicalSerializer serializer = new LexicalSerializer();
	public LexicalManager(String fileName) {
		this.lexical = constructFile(fileName);
	}
	public String get(OffsetAddress address) {
		return this.lexical.get(address).toString();
	}
	public OffsetAddress add(String token) {
		incrementarContadorDeTerminos();
		OffsetAddress nuevoOffset = this.lexical.addEntity(new LexicalTermData(token));
		return nuevoOffset;
	}
	protected void incrementarContadorDeTerminos() {
		getSerializer().setCurrentToCounter();
		try {
			OffsetAddress counterAddress = new OffsetAddress(0L);
			LexicalCounterData lcd = (LexicalCounterData) this.lexical.get(counterAddress);
			lcd.increment();
			this.lexical.updateEntity(counterAddress, lcd);
		} catch (OutOfBoundsException obe) {
			this.lexical.addEntity(new LexicalCounterData(1L));
		}
		getSerializer().setCurrentToTerm();
		
	}
	public Long getNumberOfTerms() {
		try {
			getSerializer().setCurrentToCounter();
			OffsetAddress counterAddress = new OffsetAddress(0L);
			LexicalCounterData lcd = (LexicalCounterData) this.lexical.get(counterAddress);
			return lcd.getCurrentCount();
		} catch (OutOfBoundsException obe) {
			return 0L;
		} finally {
			getSerializer().setCurrentToTerm();
		}
	}
	protected StraightVariableLengthFile<LexicalData> constructFile(String fileName) {
		return new StraightVariableLengthFile<LexicalData>(fileName, getSerializer());
	}
	protected LexicalSerializer getSerializer() {
		return this.serializer;
	}
	@Override
	public void close() throws IOException {
		this.lexical.close();
	}
}
