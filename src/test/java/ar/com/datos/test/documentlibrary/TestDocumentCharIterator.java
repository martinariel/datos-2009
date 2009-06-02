package ar.com.datos.test.documentlibrary;

import java.util.Iterator;

import ar.com.datos.documentlibrary.MemoryDocument;
import junit.framework.TestCase;

public class TestDocumentCharIterator extends TestCase {

    public void testDocumentCharIterator (){
        MemoryDocument doc = new MemoryDocument();

        doc.open();

        for (int i = 0; i < 100; i++)
            doc.addLine("Test linea");

        Iterator<Character> iterador = doc.getCharacterIterator();

        int i = 0;
        while ( iterador.hasNext() ) {
            iterador.next();
            i++;
        }

        doc.close();

        assertEquals(i, 1000);

    }

    public void testInitialPosition (){

        MemoryDocument doc = new MemoryDocument();

        doc.open();

        doc.addLine("Test linea");

        Iterator<Character> iterador = doc.getCharacterIterator(1);

        if (iterador.hasNext())
            assertEquals(iterador.next().charValue(), 'e');
        else
            fail();

        if (iterador.hasNext())
            assertEquals(iterador.next().charValue(), 's');
        else
            fail();

        if (iterador.hasNext())
            assertEquals(iterador.next().charValue(), 't');
        else
            fail();

        if (iterador.hasNext())
            assertEquals(iterador.next().charValue(), ' ');
        else
            fail();

        if (iterador.hasNext())
            assertEquals(iterador.next().charValue(), 'l');
        else
            fail();

        if (iterador.hasNext())
            assertEquals(iterador.next().charValue(), 'i');
        else
            fail();

        if (iterador.hasNext())
            assertEquals(iterador.next().charValue(), 'n');
        else
            fail();

        if (iterador.hasNext())
            assertEquals(iterador.next().charValue(), 'e');
        else
            fail();

        if (iterador.hasNext())
            assertEquals(iterador.next().charValue(), 'a');
        else
            fail();

        assertFalse(iterador.hasNext());

        doc.close();

    }

    public void testMultiLineInitialPosition (){

        MemoryDocument doc = new MemoryDocument();

        doc.open();

        String test = "prueba de string";

        for (int i = 0; i < test.length(); i++)
            doc.addLine(new Character(test.charAt(i)).toString());

        Iterator<Character> iterador = doc.getCharacterIterator(15);

        if (iterador.hasNext()){
            assertEquals ( iterador.next().charValue() , 'g');
        }
        else
            fail();

        doc.close();

    }

    public void testEmptyDocument() {

        MemoryDocument doc = new MemoryDocument();

        doc.open();

        Iterator<Character> iterator = doc.getCharacterIterator();

        if (iterator.hasNext())
            fail();

        try
        {
            iterator.next();
            fail();
        }
        catch(Exception e){}
    }

    public void testNoSuchElementException () {
        MemoryDocument doc = new MemoryDocument();

        doc.open();
        try
        {
            doc.getCharacterIterator(10);
            fail();
        }
        catch(Exception e){}

    }

}
