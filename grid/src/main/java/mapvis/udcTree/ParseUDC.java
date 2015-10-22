package mapvis.udcTree;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.Yaml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dacc on 10/20/2015.
 */
public class ParseUDC {

    public static List<mapvis.common.datatype.Node> UDCRootNodes = new ArrayList<>();
    public static HashMap<String, List<mapvis.common.datatype.Node>> parentToSubnode = new HashMap<>();
    public static Writer csvWriter;
    private static Writer yamlWriter;

    static public mapvis.common.datatype.Node createUDCTree()
    {
        try {
            readUDC();
            createSubnodesForUDCRootNodes();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
//            csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("udc.csv"), "utf-8"));
            yamlWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("udc.yaml"), "utf-8"));
            dumMainTableToYaml();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return UDCRootNodes.get(1);
    }

    public static void main(String[] args) throws ParserConfigurationException {
        try {
            csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("udc.csv"), "utf-8"));
            yamlWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("udc.yaml"), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        createUDCTree();

        dumMainTableToYaml();


//        mapvis.common.datatype.Node loadedNode = yaml.loadAs("udc.yaml", mapvis.common.datatype.Node.class);

    }

    private static void dumMainTableToYaml() {
        Yaml yaml = new Yaml();
        for(mapvis.common.datatype.Node rootNode: UDCRootNodes){
            createChildrenForNode(rootNode, parentToSubnode.get(rootNode.getId()));
            if("MAIN TABLES".equals(rootNode.getLabel())){
                yaml.dump(rootNode, yamlWriter);
            }
        }
    }

    private static void createSubnodesForUDCRootNodes() {
        for(mapvis.common.datatype.Node rootNode: UDCRootNodes){
            createChildrenForNodeNew(rootNode, parentToSubnode.get(rootNode.getId()));
        }
    }
    private static void createChildrenForNodeNew(mapvis.common.datatype.Node rootNode, List<mapvis.common.datatype.Node> childrenNodes) {
        if(childrenNodes == null) {
            rootNode.setSize(1);
            return;
        }
        int sizeOfChildren = 0;
//        rootNode.setChildren(childrenNodes);
        List<mapvis.common.datatype.Node> filteredChildrenNodes = new ArrayList<>();
        for (mapvis.common.datatype.Node childNode : childrenNodes) {
            List<mapvis.common.datatype.Node> childsOfChild = parentToSubnode.get(childNode.getId());
            if(childsOfChild == null || childsOfChild.size() == 0){
                sizeOfChildren++;
            }else{
                createChildrenForNode(childNode, childsOfChild);
                filteredChildrenNodes.add(childNode);
                sizeOfChildren += childNode.getSize();
            }
        }
        rootNode.setChildren(filteredChildrenNodes);
        rootNode.setSize(sizeOfChildren);
    }

    private static void createChildrenForNode(mapvis.common.datatype.Node rootNode, List<mapvis.common.datatype.Node> childrenNodes) {
        if(childrenNodes == null) {
            rootNode.setSize(1);
            return;
        }
        int sizeOfChildren = 0;
        rootNode.setChildren(childrenNodes);
        for (mapvis.common.datatype.Node childNode : childrenNodes) {
            createChildrenForNode(childNode, parentToSubnode.get(childNode.getId()));
            sizeOfChildren += childNode.getSize();
        }
        rootNode.setSize(sizeOfChildren);
    }

    private static void readUDC() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        try {
            FileInputStream in = new FileInputStream(new File("D:/downloads/datasets/Libraries/UDC/udcsummary-skos.rdf"));
            Document doc = db.parse(in, "UTF-8");

            NodeList nList = doc.getElementsByTagName("skos:Concept");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
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

    private static void processConceptNode(Element conceptNode)  {
        NodeList childNodes = conceptNode.getChildNodes();
        String conceptName = conceptNode.getAttribute("rdf:about");
        String parentNodeID = "";
        String englishLabel = "";

        for (int conceptNodeChildIndex = 0; conceptNodeChildIndex < childNodes.getLength(); conceptNodeChildIndex++) {
            if(childNodes.item(conceptNodeChildIndex).getNodeType() == Node.ELEMENT_NODE){
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
