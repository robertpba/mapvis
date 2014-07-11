package mapvis.grid;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XmlSerializer {

    public void ReadInto(FileInputStream file, Grid grid) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=factory.newDocumentBuilder();
        Document doc = builder.parse(file);

        NodeList nl = doc.getElementsByTagName("tile");

        for (int i=0;i<nl.getLength();i++){
            int x = Integer.parseInt(nl.item(i).getAttributes().getNamedItem("x").getNodeValue());
            int y = Integer.parseInt(nl.item(i).getAttributes().getNamedItem("y").getNodeValue());
            String str = nl.item(i).getNodeValue();
            grid.put(x,y,str);
        }
    }

    public void WriteToFile(Grid grid, OutputStream file) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("gird");
        doc.appendChild(root);
        grid.foreach(t->{
            Element tile = doc.createElement("tile");
            tile.setAttribute("x", Integer.toString(((Tile)t).getX()));
            tile.setAttribute("y", Integer.toString(((Tile)t).getY()));
            tile.setAttribute("id", ((Tile)t).getObj().toString());
            root.appendChild(tile);
        });

        TransformerFactory tFactory =
                TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

}
