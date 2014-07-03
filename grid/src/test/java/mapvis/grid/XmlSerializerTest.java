package mapvis.grid;

import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

import static org.testng.Assert.*;

public class XmlSerializerTest {

    @Test
    public void testReadInto() throws Exception {

    }

    @Test
    public void testWriteToFile() throws Exception {

        HashMapGrid grid = new HashMapGrid();
        grid.put(1,2,100);
        grid.put(1,3,101);
        grid.put(1,4,102);

        XmlSerializer xmlSerializer = new XmlSerializer();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();;
        xmlSerializer.WriteToFile(grid, outputStream);
        outputStream.writeTo(System.err);


    }
}