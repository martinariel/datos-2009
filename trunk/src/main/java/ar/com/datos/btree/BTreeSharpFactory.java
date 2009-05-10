package ar.com.datos.btree;

import java.io.File;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeConfException;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.btree.sharp.impl.disk.AdministrativeBTreeSharp;
import ar.com.datos.btree.sharp.impl.disk.BTreeSharpConfigurationDisk;
import ar.com.datos.btree.sharp.impl.disk.ElementAndKeyListSerializerFactory;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListElementsSerializer;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListKeysSerializer;
import ar.com.datos.btree.sharp.impl.disk.node.DiskNode;
import ar.com.datos.btree.sharp.impl.disk.node.NodeReferenceDisk;
import ar.com.datos.btree.sharp.impl.disk.serializer.AdministrativeBTreeSharpSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.EspecialRootNodeSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.InternalNodeSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.LeafNodeSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.RootNodeSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.StateInternalNodeSerializer;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;
import ar.com.datos.btree.sharp.node.Node;
import ar.com.datos.file.BlockAccessor;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.InvalidAddressException;
import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.util.RelativePath;
import ar.com.datos.util.Tuple;

/**
 * Permite crear o levantar instancias de árbol b#.
 * Encapsula la creación de instancias de árbol b# simplificando esta tarea.
 * Patrón Factory y Facade.
 *
 * @author fvalido
 */
public class BTreeSharpFactory<E extends Element<K>, K extends Key> {
	/**
	 * Crea un árbol B# en memoria con la capacidad especificada.
	 *
	 * @throws BTreeConfException
	 * Si se produce un problema creando el árbol.
	 */
	public BTreeSharp<E, K> createBTreeSharpMemory(int nodeSize) throws BTreeConfException {
		if (nodeSize < 3) {
			throw new BTreeConfException("El tamaño de los nodos debe ser mayor o igual que 3.");
		}

		BTreeSharpConfiguration<E, K> bTreeSharpConfiguration = new BTreeSharpConfigurationMemory<E, K>(nodeSize);

		return new BTreeSharp<E, K>(bTreeSharpConfiguration);
	}

	/**
	 * Indica si number es potencia de pow.
	 */
	private boolean isPow(int number, int pow) {
		boolean isPow = true;
		int number2;
		while (isPow && number > 1) {
			number2 = number / pow;
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
	 * se leerán las configuraciones desde él y se ignorará el resto de los parámetros.
	 * Si el archivo no existe previamente o si overwrite es true se creará un nuevo árbol
	 * cuyos nodos internos y configuración serán almacenados en este archivo.
	 * El efecto de llamar a este método pasando un archivo internalFile existente y
	 * overwrite en false es el mismo que llamar a {@link #createBTreeSharpDisk(String)}.
	 *
	 * @param leafFile
	 * Archivo para los nodos hoja.
	 *
	 * @param blockSize
	 * Tamaño de los bloques.
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
	public BTreeSharp<E, K> createBTreeSharpDisk(String internalFile, String leafFile, int blockSize,
										Class<? extends ElementAndKeyListSerializerFactory<?, K>> serializerFactoryClass,
										boolean overwrite) throws BTreeConfException {
		if (!isPow(blockSize, 2) || blockSize < 128) {
			throw new BTreeConfException("El tamaño de los nodos debe ser potencia de 2 y mayor o igual a 128.");
		}
		try {
			File internalF = new File(internalFile);
			File leafF = new File(leafFile);

			if (overwrite) {
				if (internalF.exists()) {
					internalF.delete();
				}
				if (leafF.exists()) {
					leafF.delete();
				}
			} else {
				if (internalF.exists()) {
					return createBTreeSharpDisk(internalFile, blockSize);
				}
			}

			// Creo y guardo la configuración del nodo.
			AdministrativeBTreeSharpSerializer<E, K> administrativeSerializer = new AdministrativeBTreeSharpSerializer<E, K>();
			File internalFParent = internalF.getParentFile();
			if (internalFParent == null) {
				internalFParent = new File("./");
			}
			String leafFileRelativePath = RelativePath.getRelativePath(internalFParent, leafF);
			AdministrativeBTreeSharp<E, K> administrativeBTreeSharp = new AdministrativeBTreeSharp<E, K>(leafFileRelativePath, serializerFactoryClass.getName());
			BlockAccessor<BlockAddress<Long, Short>, AdministrativeBTreeSharp<E, K>> administrativeFile = new VariableLengthFileManager<AdministrativeBTreeSharp<E,K>>(internalFile, blockSize, administrativeSerializer);
			administrativeFile.addEntity(administrativeBTreeSharp);
			administrativeFile.close();
			
			// Creo la configuración del árbol en disco.
			BTreeSharpConfigurationDisk<E, K> bTreeSharpConfigurationDisk = createBTreeSharpConfigurationDisk(internalFile, blockSize, administrativeBTreeSharp);
			
			// Creo el árbol
			BTreeSharp<E, K> btree = new BTreeSharp<E, K>(bTreeSharpConfigurationDisk);
			
			// Agrego el arbol donde se necesita.
			bTreeSharpConfigurationDisk.getStateInternalNodeSerializer().setBTree(btree);
			
			return btree;
		} catch (Exception e) {
			throw new BTreeConfException(e);
		}
	}

	/**
	 * Crea, a partir del nombre de la clase factory, el serializador de lista de Keys y el serializador de
	 * lista de Elements.
	 */
	private Tuple<ListKeysSerializer<K>, ListElementsSerializer<E, K>> createListSerializers(Class<ElementAndKeyListSerializerFactory<E, K>> serializerFactoryClass) throws BTreeConfException {
		try {
			ElementAndKeyListSerializerFactory<E, K> elementAndKeyListSerializerFactory = serializerFactoryClass.newInstance();
			ListKeysSerializer<K> listKeysSerializer = elementAndKeyListSerializerFactory.createListKeySerializer();
			ListElementsSerializer<E, K> listElementsSerializer = elementAndKeyListSerializerFactory.createListElementSerializer();
			return new Tuple<ListKeysSerializer<K>, ListElementsSerializer<E,K>>(listKeysSerializer, listElementsSerializer);
		} catch (Exception e) {
			// Solo debería ocurrir si el serializerFactoryFQDN no es una clase, o no tiene un constructor sin parámetros.
			throw new BTreeConfException(e);
		}
	}

	/**
	 * Crea los serializadores para el archivo de hojas y de nodos internos.
	 */
	private Tuple<LeafNodeSerializer<E, K>, StateInternalNodeSerializer<E, K>> createNodesSerializers(Tuple<ListKeysSerializer<K>, ListElementsSerializer<E, K>> listKeyAndElementSerializer,
												BTreeSharpConfigurationDisk<E, K> bTreeSharpConfigurationDisk) {
		InternalNodeSerializer<E, K> internalNodeSerializer = new InternalNodeSerializer<E, K>(listKeyAndElementSerializer.getFirst(), bTreeSharpConfigurationDisk);
		EspecialRootNodeSerializer<E, K> especialRootNodeSerializer = new EspecialRootNodeSerializer<E, K>(listKeyAndElementSerializer.getSecond(), bTreeSharpConfigurationDisk);
		RootNodeSerializer<E, K> rootNodeSerializer = new RootNodeSerializer<E, K>(listKeyAndElementSerializer.getFirst(), bTreeSharpConfigurationDisk);
		
		StateInternalNodeSerializer<E, K> stateInternalNodeSerializer = new StateInternalNodeSerializer<E, K>(internalNodeSerializer, rootNodeSerializer, especialRootNodeSerializer);
		LeafNodeSerializer<E, K> leafNodeSerializer = new LeafNodeSerializer<E, K>(listKeyAndElementSerializer.getSecond(), bTreeSharpConfigurationDisk);

		return new Tuple<LeafNodeSerializer<E, K>, StateInternalNodeSerializer<E, K>>(leafNodeSerializer, stateInternalNodeSerializer);
	}
	
	/**
	 * Levanta el rootNode desde el archivo correspondiente en la posición correspondente.
	 */
	private Node<E, K> readRootNodeFromDisk(BlockAccessor<BlockAddress<Long,Short>, Node<E, K>> internalNodeFile, BlockAddress<Long, Short> rootNodePosition) {
		// Levanto el nodo raiz.
		Node<E, K> rootNode = null;
		try {
			rootNode = internalNodeFile.get(rootNodePosition);
			
			// Obtengo una referencia al nodo.
			NodeReferenceDisk<E, K> rootNodeReference = new NodeReferenceDisk<E, K>(rootNodePosition, internalNodeFile, rootNode.getNodeType());

			// La pongo en el nodo.
			DiskNode<E, K> rootNodeDisk = (DiskNode<E, K>)rootNode;
			rootNodeDisk.setNodeReference(rootNodeReference);
		} catch (InvalidAddressException e) {
			// Si obtengo esta excepción significa que nunca agregaron nada al árbol por lo que nunca llegó
			// a crearse la raiz: crearé el árbol sin raiz (rootNode = null).
		}
		
		return rootNode;
	}
	
	/**
	 * Crea la coniguración del árbol en disco.
	 */
	@SuppressWarnings("unchecked")
	private BTreeSharpConfigurationDisk<E, K> createBTreeSharpConfigurationDisk(String internalFile, int blockSize, AdministrativeBTreeSharp<E, K> administrativeBTreeSharp) throws Exception {
		// Creo la configuración del árbol. Estableceré sus parámetros luego.
		BTreeSharpConfigurationDisk<E, K> bTreeSharpConfigurationDisk = new BTreeSharpConfigurationDisk<E, K>();
		
		// Primero creo los serializadores.
		Class<ElementAndKeyListSerializerFactory<E, K>> serializerFactoryClass = (Class<ElementAndKeyListSerializerFactory<E, K>>)Class.forName(administrativeBTreeSharp.getElementAndKeyListSerializerFactoryClassFQDN());
		Tuple<ListKeysSerializer<K>, ListElementsSerializer<E, K>> listKeyAndElementSerializer = createListSerializers(serializerFactoryClass);
		Tuple<LeafNodeSerializer<E, K>, StateInternalNodeSerializer<E, K>> leafAndInternalNodeSerializer = createNodesSerializers(listKeyAndElementSerializer, bTreeSharpConfigurationDisk);
		
		// Ya puedo crear los archivos
		BlockAccessor<BlockAddress<Long,Short>, Node<E, K>> internalNodeFile = new VariableLengthFileManager<Node<E, K>>(internalFile, blockSize, leafAndInternalNodeSerializer.getSecond());			
		String leafFile = administrativeBTreeSharp.getLeafFileName();
		File internalF = new File(internalFile);
		File internalFParent = internalF.getParentFile();
		if (internalFParent == null) {
			internalFParent = new File("./");
		}
		String leafFileRelative = internalFParent + "/" + leafFile;
		BlockAccessor<BlockAddress<Long,Short>, Node<E, K>> leafNodeFile = new VariableLengthFileManager<Node<E, K>>(leafFileRelative, blockSize, (Serializer)leafAndInternalNodeSerializer.getFirst());
		
		// Establezco la información correspondiente a la configuración del árbol en el BTreeSharpConfigurationDisk
		bTreeSharpConfigurationDisk.setLeafNodesFileManager(leafNodeFile);
		bTreeSharpConfigurationDisk.setInternalNodesFileManager(internalNodeFile);
		bTreeSharpConfigurationDisk.setLeafNodeSerializer(leafAndInternalNodeSerializer.getFirst());
		bTreeSharpConfigurationDisk.setStateInternalNodeSerializer(leafAndInternalNodeSerializer.getSecond());
		bTreeSharpConfigurationDisk.setListKeysSerializer(listKeyAndElementSerializer.getFirst());
		bTreeSharpConfigurationDisk.setListElementsSerializer(listKeyAndElementSerializer.getSecond());
		bTreeSharpConfigurationDisk.setMaxCapacityNode(internalNodeFile.getDataSizeFor((short)1));
		bTreeSharpConfigurationDisk.setMaxCapacityRootNode(internalNodeFile.getDataSizeFor((short)2));
		
		return bTreeSharpConfigurationDisk;
	}
	
	/**
	 * Levanta un árbol b# de disco.
	 * PRE: El árbol ya existe en disco.
	 *
	 * @param internalFile
	 * Ubicación del archivo de nodos internos en disco.
	 * @param blockSize
	 * Tamaño del bloque del archivo.
	 *
	 * @throws BTreeConfException
	 * Si se produce un problema levantando el árbol.
	 */
	public BTreeSharp<E, K> createBTreeSharpDisk(String internalFile, int blockSize) throws BTreeConfException {
		try {
			File internalF = new File(internalFile);
			if (!internalF.exists()) {
				throw new BTreeConfException();
			}

			// Primero levanto la configuración del árbol
			AdministrativeBTreeSharpSerializer<E, K> administrativeSerializer = new AdministrativeBTreeSharpSerializer<E, K>();
			BlockAccessor<BlockAddress<Long, Short>, AdministrativeBTreeSharp<E, K>> administrativeFile = new VariableLengthFileManager<AdministrativeBTreeSharp<E,K>>(internalFile, blockSize, administrativeSerializer);
			AdministrativeBTreeSharp<E, K> administrativeBTreeSharp = new AdministrativeBTreeSharp<E, K>(administrativeFile);
			administrativeFile.close();

			// Creo la configuración del árbol en disco.
			BTreeSharpConfigurationDisk<E, K> bTreeSharpConfigurationDisk = createBTreeSharpConfigurationDisk(internalFile, blockSize, administrativeBTreeSharp);

			// Creo el árbol
			BTreeSharp<E, K> btree = new BTreeSharp<E, K>(bTreeSharpConfigurationDisk);
			
			// Agrego el arbol donde se necesita.
			bTreeSharpConfigurationDisk.getStateInternalNodeSerializer().setBTree(btree);
			
			// Obtengo el nodo raiz.
			Node<E, K> rootNode = readRootNodeFromDisk(bTreeSharpConfigurationDisk.getInternalNodesFileManager(), administrativeBTreeSharp.getRootNodePosition());
			
			// Establezco el nodo raiz en el árbol
			btree.setRootNode(rootNode);
			
			return btree;
 		} catch (BTreeConfException e) {
 			throw e;
 		} catch (Exception e) {
			throw new BTreeConfException(e);
		}
	}
}
