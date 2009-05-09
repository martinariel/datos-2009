package ar.com.datos.persistencia.variableLength;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import ar.com.datos.buffer.EntityOutputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.buffer.blockWriter.RestrictedBufferRealeaser;
import ar.com.datos.buffer.blockWriter.RestrictedOutputBuffer;
import ar.com.datos.buffer.blockWriter.SimpleRestrictedOutputBuffer;
import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.address.VariableLengthAddress;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.ShortSerializer;
/**
 * Maneja el grabado autom�tico
 * @author dev
 *
 */
public class BlockWriter implements RestrictedBufferRealeaser, OutputBuffer {

	private static final LongSerializer LONG_SERIALIZER = SerializerCache.getInstance().getSerializer(LongSerializer.class);
	private static final ShortSerializer SHORT_SERIALIZER = SerializerCache.getInstance().getSerializer(ShortSerializer.class);
	// Longitud del puntero al siguiente bloque en caso que un registro ocupe varios bloques
	private static final Integer INNER_BLOCK_POINTER_SIZE = new Long(LONG_SERIALIZER.getDehydrateSize(0L)).intValue();
	// Longitud de la marca de cantidad de registros
	private static final Integer REGISTRY_COUNTER_SIZE = new Long(SHORT_SERIALIZER.getDehydrateSize((short)0)).intValue();
	private BlockFile fileBlock;
	private ReplaceResponsable replaceResponsable = null;
	private SortedSet<Long> availableBlocks = new TreeSet<Long>();
	private Long lastHeadWritten = BlockFile.END_BLOCK;
	private FlushListener flushListener;
	private SimpleRestrictedOutputBuffer simpleRestrictedOutputBuffer;
	private Boolean flushed;
	public BlockWriter(BlockFile fileBlock) {
		this.fileBlock = fileBlock;
		this.simpleRestrictedOutputBuffer = new SimpleRestrictedOutputBuffer(getSimpleDataSize(), this);
		setFlushed(true);
	}

	public void addAvailableBlock(Long blockNumber) {
		this.availableBlocks.add(blockNumber);
		this.lastHeadWritten = this.availableBlocks.first(); 
	}
	protected EntityOutputBuffer getOutputBuffer() {
		return this.simpleRestrictedOutputBuffer;
	}
	public Integer getSimpleDataSize() {
		return this.fileBlock.getBlockSize().intValue() - REGISTRY_COUNTER_SIZE;
	}
	public Integer getMultipleBlockDataSize() {
		return getSimpleDataSize() - INNER_BLOCK_POINTER_SIZE;
	}

	public void flush() {
		// Nothing to flush
		if (this.getOutputBuffer().getEntitiesCount() == 0 || getFlushed()) return;
		
		Deque<Collection<ArrayByte>> retrieveEntities = this.simpleRestrictedOutputBuffer.retrieveEntities();
		if (this.availableBlocks.size() > 1) {
			// En los �nicos casos que voy a tener mas de un bloque disponible es cuando hay una sola entidad
			// La raz�n por la cual la entidad no forz� flush es porque se acort� (era una actualizaci�n)
			writeExistentMultipleBlock(retrieveEntities.getFirst());
		} else {
			writeExistentOneBlock(retrieveEntities);
		}
		for (Collection<ArrayByte> entidad : retrieveEntities) {
			this.simpleRestrictedOutputBuffer.addEntity(entidad);
		}
		availableBlocks.add(this.lastHeadWritten);
		notifyFlushListeners();
	}

	public void requireReplaceTo(ReplaceResponsable replaceResponsable) {
		this.replaceResponsable = replaceResponsable;
	}
	public ReplaceResponsable getRequireResponsable() {
		return this.replaceResponsable;
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
			if (!this.simpleRestrictedOutputBuffer.getCurrentSize().equals(getSimpleDataSize())) {
				requireReplace(ob);
			}
		} else {
			simpleRelease(ob);
		}
	}

	private void simpleRelease(RestrictedOutputBuffer ob) {
		Integer loadedSize = this.simpleRestrictedOutputBuffer.getCurrentSize();
		Deque<Collection<ArrayByte>> d = ob.retrieveEntities();
		Collection<ArrayByte> last = d.removeLast();
		if (!d.isEmpty()) {
			writeExistentOneBlock(d);
			writeExistent(last, ob);
		} else {
			if (loadedSize > getSimpleDataSize() || this.availableBlocks.size() > 1)
				writeExistentMultipleBlock(last);
			else {
				writeExistentOneBlock(last);
			}
		}
		notifyFlushListeners();
	}

	private void requireReplace(RestrictedOutputBuffer ob) {
		Deque<Collection<ArrayByte>> entidades = ob.retrieveEntities();
		Integer cantidad = entidades.size();
		for (Integer i = 0; i < cantidad; i++) {
			Collection<ArrayByte> entidad = entidades.removeFirst();
			if (i.equals(this.replaceResponsable.replaceObjectNumber().intValue())) {
				this.replaceResponsable.notifyExceed(this);
			} else {
				ob.addEntity(entidad);
			}
		}
	}

	private void writeExistent(Collection<ArrayByte> last, RestrictedOutputBuffer ob) {
		Integer total = 0;
		for (ArrayByte ab: last) total += ab.getLength();
		if (total <= getSimpleDataSize()) {
			writeExistentOneBlock(last);
			this.availableBlocks.add(this.lastHeadWritten);
			ob.addEntity(last);
		} else {
			writeExistentMultipleBlock(last);
		}
	}

	private void writeExistentOneBlock(Collection<ArrayByte> last) {
		Deque<Collection<ArrayByte>> d = new ArrayDeque<Collection<ArrayByte>>(1);
		d.add(last);
		writeExistentOneBlock(d);
	}

	private void writeExistentMultipleBlock(Collection<ArrayByte> datos) {
		Collection<Collection<ArrayByte>> segmentos = splitInSegments(datos);
		Iterator<Collection<ArrayByte>> it = segmentos.iterator();
		Integer i = 0;
		while (availableBlocks.size() < segmentos.size()) {
			this.availableBlocks.add(this.fileBlock.getTotalBlocks() + i);
			i++;
		}
		this.lastHeadWritten = availableBlocks.first();
		boolean first = true;
		// Agrego a los datos la informaci�n de control
		while(it.hasNext()) {
			Long bloqueActual = availableBlocks.first();
			Collection<ArrayByte> segmento = it.next();
			availableBlocks.remove(bloqueActual);
			Long proximoBloque = !availableBlocks.isEmpty()? availableBlocks.first() : bloqueActual;
			segmento.add(new SimpleArrayByte(PrimitiveTypeSerializer.toByte(proximoBloque)));
			segmento.add(new SimpleArrayByte(PrimitiveTypeSerializer.toByte((short)(first? -1:0))));
			this.fileBlock.writeBlock(bloqueActual, segmento);
			first = false;
		}
	}
	/**
	 * Corta en pedazos de igual tama�o igual al tama�o de datos para registros que se encuentran en varios bloques
	 * rellenando con bytes vac�os el ultimo segmento
	 * @param segmentSource
	 * @return
	 */
	private Collection<Collection<ArrayByte>> splitInSegments(Collection<ArrayByte> segmentSource) {
		Integer segmentSize = getMultipleBlockDataSize();
		Integer currentSegmentSize = 0;
		Collection<ArrayByte> currentSegment = new ArrayList<ArrayByte>();
		Collection<Collection<ArrayByte>> segments = new ArrayList<Collection<ArrayByte>>();
		for (ArrayByte currentArrayByte: segmentSource) {
			if ((currentArrayByte.getLength() + currentSegmentSize) > segmentSize) {
				currentSegment.add(currentArrayByte.getLeftSubArray(segmentSize - currentSegmentSize));
				segments.add(currentSegment);
				
				ArrayByte resto = currentArrayByte.getRightSubArray(segmentSize - currentSegmentSize);
				currentSegment = new ArrayList<ArrayByte>();
				while (resto.getLength() > segmentSize) {
					currentSegment.add(resto.getLeftSubArray(segmentSize));
					segments.add(currentSegment);
					resto = resto.getRightSubArray(segmentSize);
					currentSegment = new ArrayList<ArrayByte>();
				}
				currentSegmentSize = resto.getLength();
				if (resto.getLength() > 0) {
					currentSegment.add(resto);
				}
			} else {
				currentSegment.add(currentArrayByte);
				currentSegmentSize += currentArrayByte.getLength();
			}
		}
		if (currentSegmentSize > 0) {
			if (currentSegmentSize < segmentSize) {
				currentSegment.add(new SimpleArrayByte(new byte[segmentSize - currentSegmentSize]));
			}
			segments.add(currentSegment);
		}
		return segments;
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
		setFlushed(true);
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

	@Override
	public void write(byte[] data) {
		setFlushed(false);
		this.getOutputBuffer().write(data);
	}

	@Override
	public void write(byte data) {
		setFlushed(false);
		this.getOutputBuffer().write(data);
	}

	public BlockAddress<Long, Short> closeEntity() {
		this.getOutputBuffer().closeEntity();
		if (availableBlocks.isEmpty() && this.getOutputBuffer().getEntitiesCount() > 0) {
			flush();
		}
		Short entitiesCount = this.getOutputBuffer().getEntitiesCount();
		return new VariableLengthAddress(this.lastHeadWritten, (entitiesCount == 0)? 0 : --entitiesCount);
	}

	public Integer getEntitiesCount() {
		return this.simpleRestrictedOutputBuffer.getEntitiesCount().intValue();
	}

	public Long getCurrentWrittingBlock() {
		return this.lastHeadWritten;
	}

	public void clearBuffer() {
		setFlushed(true);
		this.simpleRestrictedOutputBuffer = new SimpleRestrictedOutputBuffer(getSimpleDataSize(), this);
	}

	protected Boolean getFlushed() {
		return flushed;
	}

	protected void setFlushed(Boolean flushed) {
		this.flushed = flushed;
	}

}