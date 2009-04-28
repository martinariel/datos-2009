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
 * Permite crear o levantar instancias de �rbol b#.
 * Encapsula la creaci�n de instancias de �rbol b# simplificando esta tarea.
 * Patr�n Factory y Facade.
 *
 * @author fvalido
 */
public class BTreeSharpFactory<E extends Element<K>, K extends Key> {
	/**
	 * Crea un �rbol B# en memoria con las capacidades especificadas.
	 *
	 * @throws BTreeConfException
	 * Si se produce un problema creando el �rbol.
	 */
	public BTreeSharp<E, K> createBTreeSharpMemory(int internalNodeSize, int leafNodeSize) throws BTreeConfException {
		if (internalNodeSize < 3 || leafNodeSize < 3) {
			throw new BTreeConfException("El tama�o de los nodos debe ser mayor o igual que 3.");
		}

		BTreeSharpConfiguration<E, K> bTreeSharpConfiguration = new BTreeSharpConfigurationMemory<E, K>(internalNodeSize, leafNodeSize);

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
	 * Crea o Levanta un �rbol b# de disco.
	 * 
	 * @param internalFile
	 * Archivo para los nodos internos. Si overwrite es false y el archivo existe previamente
	 * se leer�n las configuraciones desde �l y se ignorar� el resto de los par�metros.
	 * Si el archivo no existe previamente o si overwrite es true se crear� un nuevo �rbol
	 * cuyos nodos internos y configuraci�n ser�n almacenados en este archivo.
	 * El efecto de llamar a este m�todo pasando un archivo internalFile existente y
	 * overwrite en false es el mismo que llamar a {@link #createBTreeSharpDisk(String)}.
	 *
	 * @param leafFile
	 * Archivo para los nodos hoja.
	 *
	 * @param internalNodeSize
	 * Tama�o de los nodos internos.
	 *
	 * @param leafNodeSize
	 * Tama�o de los nodos hoja.
	 *
	 * @param elementAndKeyListSerializerFactory
	 * Implementaci�n de {@link ElementAndKeyListSerializerFactory} que permite obtener los 
	 * serializadores correspondientes a la lista de {@link Element} y a la lista de {@link Key}.
	 *
	 * @param overwrite
	 * Ver lo explicado en internalFile.
	 *
	 * @throws BTreeSharpConfException
	 * Si se produce un problema levantando o creando el arbol.
	 */
	public BTreeSharp<E, K> createBTreeSharpDisk(String internalFile, String leafFile, int internalBlockSize,
										int leafBlockSize, Class<? extends ElementAndKeyListSerializerFactory<E, K>> serializerFactoryClass,
										boolean overwrite) throws BTreeConfException {
		if (!isPow(internalBlockSize, 2) || !isPow(leafBlockSize, 2) || internalBlockSize < 128 || leafBlockSize < 128) {
			throw new BTreeConfException("El tama�o de los nodos debe ser potencia de 2 y mayor a 128.");
		}
		if (!ElementAndKeyListSerializerFactory.class.isAssignableFrom(serializerFactoryClass)) {
			throw new BTreeConfException("serializerFactoryClass debe ser una implementaci�n de " + ElementAndKeyListSerializerFactory.class.getName());
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
					return createBTreeSharpDisk(internalFile, internalBlockSize);
				}
			}

			// Creo y guardo la configuraci�n del nodo.
			AdministrativeBTreeSharpSerializer<E, K> administrativeSerializer = new AdministrativeBTreeSharpSerializer<E, K>();
			String leafFileRelativePath = RelativePath.getRelativePath(internalF.getParentFile(), leafF);
			AdministrativeBTreeSharp<E, K> administrativeBTreeSharp = new AdministrativeBTreeSharp<E, K>(leafFileRelativePath, leafBlockSize, serializerFactoryClass.getName());
			BlockAccessor<BlockAddress<Long, Short>, AdministrativeBTreeSharp<E, K>> administrativeFile = new VariableLengthFileManager<AdministrativeBTreeSharp<E,K>>(internalFile, internalBlockSize, administrativeSerializer);
			administrativeFile.addEntity(administrativeBTreeSharp);
			administrativeFile.close();
			
			// Creo la configuraci�n del �rbol en disco.
			BTreeSharpConfigurationDisk<E, K> bTreeSharpConfigurationDisk = createBTreeSharpConfigurationDisk(internalFile, internalBlockSize, administrativeBTreeSharp);
			
			// Creo el �rbol
			BTreeSharp<E, K> btree = new BTreeSharp<E, K>(bTreeSharpConfigurationDisk);
			
			// Agrego el arbol donde se necesita.
			bTreeSharpConfigurationDisk.getStateInternalNodeSerializer().setBTree(btree);
			
			return btree;
		} catch (Exception e) {
			throw new BTreeConfException();
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
			// Solo deber�a ocurrir si el serializerFactoryFQDN no es una clase, o no tiene un constructor sin par�metros.
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
	 * Levanta el rootNode desde el archivo correspondiente en la posici�n correspondente.
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
			// Si obtengo esta excepci�n significa que nunca agregaron nada al �rbol por lo que nunca lleg�
			// a crearse la raiz: crear� el �rbol sin raiz (rootNode = null).
		}
		
		return rootNode;
	}
	
	/**
	 * Crea la coniguraci�n del �rbol en disco.
	 */
	@SuppressWarnings("unchecked")
	private BTreeSharpConfigurationDisk<E, K> createBTreeSharpConfigurationDisk(String internalFile, int internalBlockSize, AdministrativeBTreeSharp<E, K> administrativeBTreeSharp) throws Exception {
		// Creo la configuraci�n del �rbol. Establecer� sus par�metros luego.
		BTreeSharpConfigurationDisk<E, K> bTreeSharpConfigurationDisk = new BTreeSharpConfigurationDisk<E, K>();
		
		// Primero creo los serializadores.
		Class<ElementAndKeyListSerializerFactory<E, K>> serializerFactoryClass = (Class<ElementAndKeyListSerializerFactory<E, K>>)Class.forName(administrativeBTreeSharp.getElementAndKeyListSerializerFactoryClassFQDN());
		Tuple<ListKeysSerializer<K>, ListElementsSerializer<E, K>> listKeyAndElementSerializer = createListSerializers(serializerFactoryClass);
		Tuple<LeafNodeSerializer<E, K>, StateInternalNodeSerializer<E, K>> leafAndInternalNodeSerializer = createNodesSerializers(listKeyAndElementSerializer, bTreeSharpConfigurationDisk);
		
		// Ya puedo crear los archivos
		BlockAccessor<BlockAddress<Long,Short>, Node<E, K>> internalNodeFile = new VariableLengthFileManager<Node<E, K>>(internalFile, internalBlockSize, leafAndInternalNodeSerializer.getSecond());			
		String leafFile = administrativeBTreeSharp.getLeafFileName();
		File internalF = new File(internalFile);
		String leafFileRelative = internalF.getParent() + "/" + leafFile;
		BlockAccessor<BlockAddress<Long,Short>, Node<E, K>> leafNodeFile = new VariableLengthFileManager<Node<E, K>>(leafFileRelative, administrativeBTreeSharp.getLeafBlockSize(), (Serializer)leafAndInternalNodeSerializer.getFirst());
		
		// Establezco la informaci�n correspondiente a la configuraci�n del �rbol en el BTreeSharpConfigurationDisk
		bTreeSharpConfigurationDisk.setLeafNodesFileManager(leafNodeFile);
		bTreeSharpConfigurationDisk.setInternalNodesFileManager(internalNodeFile);
		bTreeSharpConfigurationDisk.setLeafNodeSerializer(leafAndInternalNodeSerializer.getFirst());
		bTreeSharpConfigurationDisk.setStateInternalNodeSerializer(leafAndInternalNodeSerializer.getSecond());
		bTreeSharpConfigurationDisk.setListKeysSerializer(listKeyAndElementSerializer.getFirst());
		bTreeSharpConfigurationDisk.setListElementsSerializer(listKeyAndElementSerializer.getSecond());
		bTreeSharpConfigurationDisk.setMaxCapacityLeafNode(leafNodeFile.getDataSizeFor((short)1));
		bTreeSharpConfigurationDisk.setMaxCapacityInternalNode(internalNodeFile.getDataSizeFor((short)1));
		bTreeSharpConfigurationDisk.setMaxCapacityRootNode(internalNodeFile.getDataSizeFor((short)2));
		
		return bTreeSharpConfigurationDisk;
	}
	
	/**
	 * Levanta un �rbol b# de disco.
	 * PRE: El �rbol ya existe en disco.
	 *
	 * @param internalFile
	 * Ubicaci�n del archivo de nodos internos en disco.
	 * @param internalBlockSize
	 * Tama�o del bloque del archivo de nodos internos.
	 *
	 * @throws BTreeConfException
	 * Si se produce un problema levantando el �rbol.
	 */
	public BTreeSharp<E, K> createBTreeSharpDisk(String internalFile, int internalBlockSize) throws BTreeConfException {
		try {
			File internalF = new File(internalFile);
			if (!internalF.exists()) {
				throw new BTreeConfException();
			}

			// Primero levanto la configuraci�n del �rbol
			AdministrativeBTreeSharpSerializer<E, K> administrativeSerializer = new AdministrativeBTreeSharpSerializer<E, K>();
			BlockAccessor<BlockAddress<Long, Short>, AdministrativeBTreeSharp<E, K>> administrativeFile = new VariableLengthFileManager<AdministrativeBTreeSharp<E,K>>(internalFile, internalBlockSize, administrativeSerializer);
			AdministrativeBTreeSharp<E, K> administrativeBTreeSharp = new AdministrativeBTreeSharp<E, K>(administrativeFile);
			administrativeFile.close();

			// Creo la configuraci�n del �rbol en disco.
			BTreeSharpConfigurationDisk<E, K> bTreeSharpConfigurationDisk = createBTreeSharpConfigurationDisk(internalFile, internalBlockSize, administrativeBTreeSharp);

			// Obtengo el nodo raiz.
			Node<E, K> rootNode = readRootNodeFromDisk(bTreeSharpConfigurationDisk.getInternalNodesFileManager(), administrativeBTreeSharp.getRootNodePosition());
			
			// Creo el �rbol
			BTreeSharp<E, K> btree = new BTreeSharp<E, K>(bTreeSharpConfigurationDisk, rootNode);
			
			// Agrego el arbol donde se necesita.
			bTreeSharpConfigurationDisk.getStateInternalNodeSerializer().setBTree(btree);
			
			return btree;
 		} catch (BTreeConfException e) {
 			throw e;
 		} catch (Exception e) {
			throw new BTreeConfException(e);
		}
	}
}
