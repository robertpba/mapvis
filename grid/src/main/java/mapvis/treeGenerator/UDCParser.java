package mapvis.treeGenerator;

import mapvis.common.datatype.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dacc on 10/22/2015.
 */
public class UDCParser {
    private String path;
    private List<Node> UDCRootNodes;
    private HashMap<String, List<Node>> parentToSubnode;
    private mapvis.common.datatype.Node rootNode;

    public UDCParser() {
        this.path = null;
        this.UDCRootNodes = new ArrayList<>();
        this.parentToSubnode = new HashMap<>();
    }

    public void configure(String path){
        this.UDCRootNodes.clear();
        this.parentToSubnode.clear();
        this.path = path;
        this.rootNode = null;
    }

    public mapvis.common.datatype.Node generateUDCCathegories(){
        if(this.rootNode == null) {
            try {
                readUDC();
                resolveCathegoryToChildrenRelations();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
        return rootNode;
    }


    private void setChildrenOfNode(mapvis.common.datatype.Node node){
        List<mapvis.common.datatype.Node> children = parentToSubnode.get(node.getId());
        if(children == null)
            return;
        node.setChildren(children);
        children.forEach(childNode -> setChildrenOfNode(childNode));
    }

    private void resolveCathegoryToChildrenRelations() {
        mapvis.common.datatype.Node mainTableRootNode = null;
        for(mapvis.common.datatype.Node rootNode: UDCRootNodes){
            if("AUXILIARY TABLES".equals(rootNode.getLabel())){
                mainTableRootNode = rootNode;
            }
        }

        if(mainTableRootNode == null)
            return;

        List<mapvis.common.datatype.Node> firstOrderCathegories = parentToSubnode.get(mainTableRootNode.getId());
        firstOrderCathegories.stream().forEach(node -> {
            setChildrenOfNode(node);
        });
        mainTableRootNode.setChildren(firstOrderCathegories);
        rootNode = mainTableRootNode;
    }

    private void readUDC() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        try {
//            FileInputStream in = new FileInputStream(new File("D:/downloads/datasets/Libraries/UDC/udcsummary-skos.rdf"));
            FileInputStream in = new FileInputStream(new File(path));
            Document doc = db.parse(in, "UTF-8");
            NodeList nList = doc.getElementsByTagName("skos:Concept");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                org.w3c.dom.Node nNode = nList.item(temp);
                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    processConceptNode(eElement);
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processConceptNode(Element conceptNode)  {
        NodeList childNodes = conceptNode.getChildNodes();
        String conceptName = conceptNode.getAttribute("rdf:about");
        String parentNodeID = "";
        String englishLabel = "";

        for (int conceptNodeChildIndex = 0; conceptNodeChildIndex < childNodes.getLength(); conceptNodeChildIndex++) {
            if(childNodes.item(conceptNodeChildIndex).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
                Element conceptNodeElement = (Element) childNodes.item(conceptNodeChildIndex);
                switch (conceptNodeElement.getNodeName()){
                    case "skos:broader":
                        parentNodeID = conceptNodeElement.getAttribute("rdf:resource");
                        break;
                    case "skos:prefLabel":
                        if( conceptNodeElement.getAttribute("xml:lang").equals("en")){
                            englishLabel = conceptNodeElement.getTextContent();
                        }
                        break;

                }
            }
        }
        mapvis.common.datatype.Node node = new mapvis.common.datatype.Node(conceptName, englishLabel);
        if(parentNodeID.isEmpty()){
            UDCRootNodes.add(node);
        }else{
            List<mapvis.common.datatype.Node> treeNodeList = parentToSubnode.get(parentNodeID);
            if(treeNodeList == null){
                treeNodeList = new ArrayList<mapvis.common.datatype.Node>();
                parentToSubnode.put(parentNodeID, treeNodeList);
            }
            treeNodeList.add(node);
        }
//        try {
//            csvWriter.write(conceptName  + "," + parentNodeID + "," + englishLabel + "\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        System.out.println("Concept: " + conceptName + " Parent: " + parentNodeID + " Label: " + englishLabel);
    }
}
