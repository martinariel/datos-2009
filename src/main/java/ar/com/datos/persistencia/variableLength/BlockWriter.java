package ar.com.datos.persistencia.variableLength;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import ar.com.datos.buffer.EntityOutputBuffer;
import ar.com.datos.buffer.RestrictedBufferRealeaser;
import ar.com.datos.buffer.RestrictedOutputBuffer;
import ar.com.datos.buffer.SimpleRestrictedOutputBuffer;
import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.file.BlockFile;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
/**
 * Maneja el grabado automático
 * @author dev
 *
 */
public class BlockWriter implements RestrictedBufferRealeaser {

	private BlockFile fileBlock;
	private ReplaceResponsable replaceResponsable = null;
	private SortedSet<Long> availableBlocks = new TreeSet<Long>();
	private Long lastHeadWritten;
	private FlushListener flushListener;
	private SimpleRestrictedOutputBuffer simpleRestrictedOutputBuffer;
	public BlockWriter(BlockFile fileBlock) {
		this.fileBlock = fileBlock;
		this.simpleRestrictedOutputBuffer = new SimpleRestrictedOutputBuffer(getSimpleDataSize(), this);
	}

	public void addAvailableBlock(Long blockNumber) {
		this.availableBlocks.add(blockNumber);
	}
	public EntityOutputBuffer getOutputBuffer() {
		return this.simpleRestrictedOutputBuffer;
	}
	private Integer getSimpleDataSize() {
		// XXX: Corregir por el cálculo bien...
		return this.fileBlock.getBlockSize().intValue() - 2;
	}
	private Integer getMultipleBlockDataSize() {
		// XXX: Corregir por el cálculo bien...
		return getSimpleDataSize() - 8;
	}


	public Long getCurrentWrittingBlock() {
		if (lastHeadWritten == null) {
			if (availableBlocks.isEmpty()) {
				flush();
			} else {
				lastHeadWritten = availableBlocks.first();
			}
		}
		return this.lastHeadWritten;
	}
	public Short getCurrentWrittingEntityNumber() {
		Short entitiesCount = this.getOutputBuffer().getEntitiesCount();
		return (entitiesCount == 0)? 0 : --entitiesCount;
	}
	public void flush() {
		// Nothing to flush
		if (this.getOutputBuffer().getEntitiesCount() == 0) return;
		
		Deque<Collection<ArrayByte>> retrieveEntities = this.simpleRestrictedOutputBuffer.retrieveEntities();
		writeExistentOneBlock(retrieveEntities);
		for (Collection<ArrayByte> entidad : retrieveEntities) {
			this.simpleRestrictedOutputBuffer.addEntity(entidad);
		}
		availableBlocks.add(this.lastHeadWritten);
		notifyFlushListeners();
	}

	public void requireReplaceTo(ReplaceResponsable replaceResponsable) {
		this.replaceResponsable = replaceResponsable;
	}

	public void unsetRequireReplace() {
		requireReplaceTo(null);
	}
	public Boolean isReplaceRequiredEnabled() {
		return this.replaceResponsable != null;
	}
	@Override
	public void release(RestrictedOutputBuffer ob) {
		if (isReplaceRequiredEnabled()) {
			ob.removeLastEntity();
			this.replaceResponsable.notifyExceed(this);
		} else {
			Deque<Collection<ArrayByte>> d = ob.retrieveEntities();
			Collection<ArrayByte> last = d.removeLast();
			if (!d.isEmpty()) {
				writeExistentOneBlock(d);
				writeExistent(last, ob);
			} else {
				writeExistentMultipleBlock(last);
			}
			notifyFlushListeners();
		}
	}

	private void writeExistent(Collection<ArrayByte> last, RestrictedOutputBuffer ob) {
		Integer total = 0;
		for (ArrayByte ab: last) total += ab.getLength();
		if (total < getSimpleDataSize()) {
			Deque<Collection<ArrayByte>> d = new ArrayDeque<Collection<ArrayByte>>(1);
			d.add(last);
			writeExistentOneBlock(d);
			ob.addEntity(last);
		} else {
			writeExistentMultipleBlock(last);
		}
	}

	private void writeExistentMultipleBlock(Collection<ArrayByte> datos) {
		Collection<Collection<ArrayByte>> segmentos = splitInSegments(datos);
		Iterator<Collection<ArrayByte>> it = segmentos.iterator();
		if (availableBlocks.isEmpty()) {
			for (Integer i = 0; i < segmentos.size(); i++) this.availableBlocks.add(this.fileBlock.getTotalBlocks() + i);
		}
		this.lastHeadWritten = availableBlocks.first();
		boolean first = true;
		// Agrego a los datos la información de control
		while(it.hasNext()) {
			Long bloqueActual = availableBlocks.first();
			availableBlocks.remove(bloqueActual);
			Collection<ArrayByte> segmento = it.next();
			Long proximoBloque = it.hasNext()? availableBlocks.first() : bloqueActual;
			segmento.add(new SimpleArrayByte(PrimitiveTypeSerializer.toByte(proximoBloque)));
			segmento.add(new SimpleArrayByte(PrimitiveTypeSerializer.toByte((short)(first? -1:0))));
			this.fileBlock.writeBlock(bloqueActual, segmento);
			first = false;
		}
	}
	/**
	 * Corta en pedazos de igual tamaño igual al tamaño de datos para registros que se encuentran en varios bloques
	 * rellenando con bytes vací­os el ultimo segmento
	 * @param extractLast
	 * @return
	 */
	private Collection<Collection<ArrayByte>> splitInSegments(Collection<ArrayByte> extractLast) {
		Integer tamanioSegmentos = getMultipleBlockDataSize();
		Integer sumaParcial = 0;
		Collection<ArrayByte> pedacitoActual = new ArrayList<ArrayByte>();
		Collection<Collection<ArrayByte>> pedacitos = new ArrayList<Collection<ArrayByte>>();
		for (ArrayByte ab: extractLast) {
			if ((ab.getLength() + sumaParcial) > tamanioSegmentos) {
				pedacitoActual.add(ab.getLeftSubArray(tamanioSegmentos - sumaParcial));
				pedacitos.add(pedacitoActual);
				
				ArrayByte resto = ab.getRightSubArray(tamanioSegmentos - sumaParcial);
				pedacitoActual = new ArrayList<ArrayByte>();
				while (resto.getLength() > tamanioSegmentos) {
					pedacitoActual.add(resto.getLeftSubArray(tamanioSegmentos));
					pedacitos.add(pedacitoActual);
					resto = resto.getRightSubArray(tamanioSegmentos);
					pedacitoActual = new ArrayList<ArrayByte>();
				}
				sumaParcial = resto.getLength();
				if (resto.getLength() > 0) {
					pedacitoActual.add(resto);
				}
			} else {
				pedacitoActual.add(ab);
				sumaParcial += ab.getLength();
			}
		}
		if (sumaParcial > 0) {
			if (sumaParcial < tamanioSegmentos) {
				pedacitoActual.add(new SimpleArrayByte(new byte[tamanioSegmentos - sumaParcial]));
			}
			pedacitos.add(pedacitoActual);
		}
		return pedacitos;
	}
	
	private void writeExistentOneBlock(Deque<Collection<ArrayByte>> d) {
		if (availableBlocks.isEmpty()) this.availableBlocks.add(this.fileBlock.getTotalBlocks());

		Integer cantidadDeElementos = d.size();
		Collection<ArrayByte> datos = new ArrayList<ArrayByte>();
		for (Collection<ArrayByte> masDatos : d) datos.addAll(masDatos);
		
		this.lastHeadWritten = availableBlocks.first();
		fileBlock.writeBlock(this.lastHeadWritten, completeBlock(datos, cantidadDeElementos));
		availableBlocks.remove(this.lastHeadWritten);
	}

	private void notifyFlushListeners() {
		if (this.flushListener != null) this.flushListener.flushed();
	}

	private Collection<ArrayByte> completeBlock(Collection<ArrayByte> datos, Integer cantidadDeElementos) {
		Collection<ArrayByte> retorno = new ArrayList<ArrayByte>(datos);
		Integer total = 0;
		for (ArrayByte parte : datos) total += parte.getLength();
		Integer diferencia = getSimpleDataSize().intValue() - total;
		if (diferencia > 0) retorno.add(new SimpleArrayByte(new byte[diferencia]));
		
		retorno.add(new SimpleArrayByte(PrimitiveTypeSerializer.toByte(cantidadDeElementos.shortValue())));
		
		return retorno;
	}

	public void addFlushListener(FlushListener flushListener) {
		this.flushListener = flushListener;
	}

}