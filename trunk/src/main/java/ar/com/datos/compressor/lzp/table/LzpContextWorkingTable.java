package ar.com.datos.compressor.lzp.table;

import ar.com.datos.btree.BTree;
import ar.com.datos.btree.BTreeSharpFactory;
import ar.com.datos.compressor.lzp.table.serializer.LzpWorkingTablePersistenceSerializerFactory;
import ar.com.datos.util.UnsignedInt;


/**
 * Tabla de trabajo de contextos para el Lzp.
 * 
 * Notas de implemntación:
 * El tamaño de un LzpContextPosition es 8 bytes (char, char e int).
 * Por tanto 506 * 8 = 4048. Se disponen de 48 bytes extras para llegar a los
 * 4096 bytes (4Kb) que son utilizadas para otras cosas / se reservan.
 * Manejo el array "workingTable" como una hashTable. Para ello tomo el hashCode del lzpContext y
 * le aplico un mod 22 obteniendo 22 particiones de 23 posiciones de la workingTable de 506 posiciones
 * (lo más cercano a la raiz de 506). 
 * Entre esas 23 posiciones busco si está el lzpContext pasado. Si está reemplazo la posición. Si no
 * está llevo el lzpContext más antiguo (de posición menor) a disco y ubico allí el lzpContextPosition
 * a agregar.
 * 
 * @author fvalido
 *
 */
public class LzpContextWorkingTable {
	private static final String BTREE_FILE_NAME = "lzpworkingtable";
	private static final String BTREE_FILE_NAME_INTERNAL_SUFFIX = ".node";
	private static final String BTREE_FILE_NAME_LEAF_SUFFIX = ".leaf";
	/** Tabla de trabajo en memoria. */
	private LzpContextPosition[] workingTable;
	/** Persistencia en disco de los más antiguos */
	private BTree<LzpContextPosition, LzpContext> persistence;

	/**
	 * Constructor.
	 */
	public LzpContextWorkingTable() {

		this.workingTable = new LzpContextPosition[506];

		// Persistencia.
		this.persistence = new BTreeSharpFactory<LzpContextPosition, LzpContext>().createBTreeSharpDisk(BTREE_FILE_NAME + BTREE_FILE_NAME_INTERNAL_SUFFIX, BTREE_FILE_NAME + BTREE_FILE_NAME_LEAF_SUFFIX, 256, LzpWorkingTablePersistenceSerializerFactory.class, true);
	}

	/**
	 * Obtiene la posición del {@link LzpContext} pasado. De no encontrarse se devuelve la posición
	 * del LzpContext más antiguo dentro de la porción correspondiente (ver detalles de implementación
	 * en javadoc de la clase).
	 **/
	private int findPosition(LzpContext lzpContext) {
		int portionStartIndex = (lzpContext.hashCode() % 22) * 23;
		
		int oldestPosition = portionStartIndex;
		boolean found = false;
		int i = portionStartIndex;
		while (i < portionStartIndex + 23 && !found) {
			if (this.workingTable[i] == null || this.workingTable[i].getKey().equals(lzpContext)) {
				found = true;
			} else {
				if (this.workingTable[i].getPosition().compareTo(this.workingTable[oldestPosition].getPosition()) > 0) {
					oldestPosition = i;
				}
				i++;
			}
		}
		
		return (found) ? i : oldestPosition;
	}
	
	/**
	 * Agrega o reemplaza la información de un contexto con la información pasada.
	 */
	public void addOrReplace(LzpContext lzpContext, long position) {
		int positionInArray = findPosition(lzpContext);
		
		LzpContextPosition lzpContextPositionAdd = new LzpContextPosition(lzpContext, new UnsignedInt(position));
		
		if (!this.workingTable[positionInArray].getKey().equals(lzpContext)) {
			// Si la posición encontrada no es la del lzpContext buscado entonces es la del más antiguo
			// de la porción ==> Lo envio a disco.
			this.persistence.addElement(this.workingTable[positionInArray]);
		}
		// Reemplazo en la posición encontrada el lzpContextPosition
		this.workingTable[positionInArray] = lzpContextPositionAdd;
		
	}
	
	/**
	 * Averigua la posición almacenada para el {@link LzpContext} pasado.
	 * 
	 * @return
	 * La posición, o null si no se lo encuentra.
	 */
	public Long getPosition(LzpContext lzpContext) {
		int positionInArray = findPosition(lzpContext);
		
		LzpContextPosition lzpContextPosition = this.workingTable[positionInArray];
		// Si el lzpContextPosition de la posición encontrada no es el buscado, entonces
		// no está en memoria... Lo busco en disco.
		if (!lzpContextPosition.getKey().equals(lzpContext)) {
			lzpContextPosition = this.persistence.findElement(lzpContext);
		}
		
		return (lzpContextPosition == null) ? null : lzpContextPosition.getPosition().longValue();
	}
	
	/**
	 * Deja sin efecto la tabla actual.
	 */
	public void close() {
		this.persistence.close();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.persistence.close();
	}
}
