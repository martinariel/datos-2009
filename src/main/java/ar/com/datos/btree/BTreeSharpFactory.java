package ar.com.datos.btree;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeConfException;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.btree.sharp.impl.memory.AdministrativeBTreeSharpMemory;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;

/**
 * Permite crear o levantar instancias de árbol b#.
 * Encapsula la creación de instancias de árbol b# simplificando esta tarea.
 * Patrón Factory y Facade.
 *
 * @author fvalido
 */
public class BTreeSharpFactory<E extends Element<K>, K extends Key> {
	/**
	 * Crea un árbol B# en memoria con las capacidades especificadas.
	 *
	 * @throws BTreeConfException
	 * Si se produce un problema creando el árbol.
	 */
	public BTreeSharp<E, K> createBTreeSharpMemory(short internalNodeSize, short leafNodeSize) throws BTreeConfException {
		if (internalNodeSize < 3 || leafNodeSize < 3) {
			throw new BTreeConfException();
		}

		BTreeSharpConfiguration<E, K> bTreeSharpConfiguration = new BTreeSharpConfigurationMemory<E, K>(internalNodeSize, leafNodeSize);

		return new BTreeSharp<E, K>(bTreeSharpConfiguration, new AdministrativeBTreeSharpMemory<E, K>());
	}

	/**
	 * Indica si number es potencia de pow.
	 */
	private boolean isPow(short number, short pow) {
		boolean isPow = true;
		short number2;
		while (isPow && number > 1) {
			number2 = (short)(number / pow);
			isPow = (number2 * pow) == number;
			
			number = number2;
		}
		
		return isPow;
	}
	
	/**
	 * Crea o Levanta un árbol b# de disco.
	 *
	 * @param internalFile
	 * Archivo para los nodos internos. Si overwrite es false y el archivo existe previamente
	 * se leerán las configuraciones desde el y se ignorará el resto de los parámetros.
	 * Si el archivo no existe previamente o si overwrite es true se creará un nuevo árbol
	 * cuyos nodos internos y configuración serán almacenados en este archivo.
	 * El efecto de llamar a este método pasando un archivo internalFile existente y
	 * overwrite en false es el mismo que llamar a {@link #createBTreeSharpDisk(String)}.
	 *
	 * @param leafFile
	 * Archivo para los nodos hoja.
	 *
	 * @param internalNodeSize
	 * Tamaño de los nodos internos.
	 *
	 * @param leafNodeSize
	 * Tamaño de los nodos hoja.
	 *
	 * @param elementAndKeyListSerializerFactory
	 * Implementación de {@link ElementAndKeyListSerializerFactory} que permite obtener los
	 * serializadores correspondientes a la lista de {@link Element} y a la lista de {@link Key}.
	 *
	 * @param overwrite
	 * Ver lo explicado en internalFile.
	 *
	 * @throws BTreeSharpConfException
	 * Si se produce un problema levantando o creando el arbol.
	 */
// FIXME
//	public BTreePlus<E, K> createBTreePlusDisk(String internalFile, String leafFile, short internalNodeSize,
//										short leafNodeSize, Class serializerFactoryClass,
//										boolean overwrite) throws BTreePlusConfException {
//		if (!isPow(internalNodeSize, (short)2) || !isPow(leafNodeSize, (short)2) || internalNodeSize < 64 || leafNodeSize < 64) {
//			throw new BTreePlusConfException();
//		}
//		try {
//			File internalF = new File(internalFile);
//			File leafF = new File(leafFile);
//
//			if (overwrite) {
//				if (internalF.exists()) {
//					internalF.delete();
//				}
//				if (leafF.exists()) {
//					leafF.delete();
//				}
//			} else {
//				if (internalF.exists()) {
//					return createBTreePlusDisk(internalFile);
//				}
//			}
//
//			RandomAccessFile fileInternal = new RandomAccessFile(internalF, "rw");
//			String leafFileRelative = RelativePath.getRelativePath(internalF.getParentFile(), leafF);
//			AdministrativeBTreePlusDisk<E, K> administrativeBTreePlusDisk = new AdministrativeBTreePlusDisk<E, K>(fileInternal, leafFileRelative, serializerFactoryClass.getName(), leafNodeSize, internalNodeSize);
//
//			BTreePlusConfigurationDisk<E, K> bTreePlusConfiguration = createBTreePlusConfiguration(fileInternal, leafFile, internalNodeSize, leafNodeSize, serializerFactoryClass, administrativeBTreePlusDisk.getAdministrativeBlockCount());
//			administrativeBTreePlusDisk.setInternalNodeFileManager(bTreePlusConfiguration.getInternalNodeFileManager());
//			administrativeBTreePlusDisk.setLeafNodeFileManager(bTreePlusConfiguration.getLeafNodeFileManager());
//			
//			return new BTreePlus<E, K>(bTreePlusConfiguration, administrativeBTreePlusDisk);
//		} catch (Exception e) {
//			throw new BTreePlusConfException();
//		}
//	}

//	@SuppressWarnings("unchecked")
//	private BTreePlusConfigurationDisk<E, K> createBTreePlusConfiguration(RandomAccessFile internalFile, String leafFile, short internalNodeSize,
//										short leafNodeSize, Class serializerFactoryClass, short administrativeBlockCount) throws BTreePlusConfException {
//		try {
//			RandomAccessFile fileLeaf = new RandomAccessFile(leafFile, "rw");
//
//			ElementAndKeyListSerializerFactory<E, K> elementAndKeyListSerializerFactory = (ElementAndKeyListSerializerFactory<E, K>)serializerFactoryClass.newInstance();
//
//			Serializer<List<E>> listElementSerializer = elementAndKeyListSerializerFactory.createListElementSerializer();
//			Serializer<List<K>> listKeySerializer = elementAndKeyListSerializerFactory.createListKeySerializer();
//
//			LeafNodeSerializer<E, K> leafNodeSerializer = new LeafNodeSerializer<E, K>((Serializer<List<E>>)listElementSerializer);
//			InternalNodeSerializer<E, K> internalNodeSerializer = new InternalNodeSerializer<E, K>(listKeySerializer);
//
//			LeafNodeFileManager<E, K> leafNodeFileManager = new LeafNodeFileManager<E, K>(leafNodeSerializer, internalNodeSerializer, fileLeaf);
//			InternalNodeFileManager<E, K> internalNodeFileManager = new InternalNodeFileManager<E, K>(leafNodeSerializer, internalNodeSerializer, internalFile, administrativeBlockCount);
//
//			return new BTreePlusConfigurationDisk<E, K>(internalNodeSize, leafNodeSize, leafNodeFileManager, internalNodeFileManager);
//		} catch (Exception e) {
//			throw new BTreePlusConfException();
//		}
//	}

	/**
	 * Levanta un árbol b# de disco.
	 * PRE: El arbol ya existe en disco.
	 *
	 * @param internalFile
	 * Ubicación del archivo de hojas internas en disco.
	 *
	 * @throws BTreePlusConfException
	 * Si se produce un problema levantando el árbol.
	 */
// FIXME
//	public BTreePlus<E, K> createBTreePlusDisk(String internalFile) throws BTreePlusConfException {
//		try {
//			File internalF = new File(internalFile);
//			if (!internalF.exists()) {
//				throw new BTreePlusConfException();
//			}
//
//			RandomAccessFile fileInternal = new RandomAccessFile(internalF, "rw");
//			AdministrativeBTreePlusDisk<E, K> administrativeBTreePlusDisk = new AdministrativeBTreePlusDisk<E, K>(fileInternal);
//
//			String leafFile = administrativeBTreePlusDisk.getLeafFileName();
//			String leafFileRelative = internalF.getParent() + "/" + leafFile;
//			short internalNodeSize = administrativeBTreePlusDisk.getInternalNodeSize();
//			short leafNodeSize = administrativeBTreePlusDisk.getLeafNodeSize();
//			Class serializerFactoryClass = Class.forName(administrativeBTreePlusDisk.getElementAndKeyListSerializerFactoryClassFQDN());
//			short administrativeBlockCount = administrativeBTreePlusDisk.getAdministrativeBlockCount();
//
//			BTreePlusConfigurationDisk<E, K> bTreePlusConfiguration = createBTreePlusConfiguration(fileInternal, leafFileRelative, internalNodeSize, leafNodeSize, serializerFactoryClass, administrativeBlockCount);
//			administrativeBTreePlusDisk.setInternalNodeFileManager(bTreePlusConfiguration.getInternalNodeFileManager());
//			administrativeBTreePlusDisk.setLeafNodeFileManager(bTreePlusConfiguration.getLeafNodeFileManager());
//			
//			if (administrativeBTreePlusDisk.getRootNodePosition() >= 0) {
//				NodeReference<E, K> rootNodeReference = new NodeReferenceDisk<E, K>(administrativeBTreePlusDisk.getRootNodePosition(), bTreePlusConfiguration.getInternalNodeFileManager());				
//				return new BTreePlus<E, K>(bTreePlusConfiguration, administrativeBTreePlusDisk, rootNodeReference);
//			} else {
//				return new BTreePlus<E, K>(bTreePlusConfiguration, administrativeBTreePlusDisk);
//			}
// 		} catch (Exception e) {
//			throw new BTreePlusConfException();
//		}
//	}
}
