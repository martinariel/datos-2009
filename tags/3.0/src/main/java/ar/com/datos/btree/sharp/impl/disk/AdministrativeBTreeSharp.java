package ar.com.datos.btree.sharp.impl.disk;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.file.BlockAccessor;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.address.VariableLengthAddress;

/**
 * Permite manejar la información administrativa del árbol en disco.
 *
 * @author fvalido
 */
public class AdministrativeBTreeSharp<E extends Element<K>, K extends Key> {
    /** Nombre del archivo de hojas, path relativo al archivo de nodos internos. */
    private String leafNodeFileName;
    /** 
     * Fully Qualified Domain Name de la Clase que permite crear los serializadores de
     * listas de {@link Element} y {@link Key}.
     */
    private String elementAndKeyListSerializerFactoryClassFQDN;
    /** Posición del nodo raiz en el archivo */
    private BlockAddress<Long, Short> rootNodePosition;
    
    /**
     * Constructor que recibe la información directamente.
     */
	public AdministrativeBTreeSharp(String leafNodeFileName, String elementAndKeyListSerializerFactoryClassFQDN) {
        this.leafNodeFileName = leafNodeFileName;
        this.elementAndKeyListSerializerFactoryClassFQDN = elementAndKeyListSerializerFactoryClassFQDN;
	}

    /**
     * Constructor que levanta la información desde el archivo pasado.
     * 
     * @throws BTreeException
     * Si se produce algún problema leyendo la información administrativa del árbol en el archivo.
     */
	public AdministrativeBTreeSharp(BlockAccessor<BlockAddress<Long, Short>, AdministrativeBTreeSharp<E, K>> internalNodeFileManager) throws BTreeException {
		BlockAddress<Long, Short> position = new VariableLengthAddress(0L, (short)0);
        AdministrativeBTreeSharp<E, K> aux = internalNodeFileManager.get(position);
        this.leafNodeFileName = aux.getLeafFileName();
        this.elementAndKeyListSerializerFactoryClassFQDN = aux.getElementAndKeyListSerializerFactoryClassFQDN();
        this.rootNodePosition = new VariableLengthAddress((long)internalNodeFileManager.getAmountOfBlocksFor(position).shortValue(), (short)0);
	}

    /**
     * Permite obtener el path relativo hasta el archivo de hojas tomado desde el path del
     * archivo actual (de nodos internos).
     */
    public String getLeafFileName() {
        return this.leafNodeFileName;
    }

    /**
     * Permite obtener el nombre de la clase (Fully Qualified Domain Name, FQDN) Factory que se encarga
     * de crear los serializadores de listas de {@link Element} y {@link Key}.
     */
    public String getElementAndKeyListSerializerFactoryClassFQDN() {
        return this.elementAndKeyListSerializerFactoryClassFQDN;
    }

    /**
	 * Permite obtener la posición del nodo raiz.
	 */
	public BlockAddress<Long, Short> getRootNodePosition() {
		return rootNodePosition;
	}
}
