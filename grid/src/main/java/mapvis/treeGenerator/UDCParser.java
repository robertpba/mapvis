package mapvis.treeGenerator;

import mapvis.common.datatype.INode;
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
    private List<INode> UDCRootNodes;
    private HashMap<String, List<INode>> parentToSubnode;
    private INode rootNode;

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

    public INode generateUDCCathegories(){
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


    private void setChildrenOfNode(INode node){
        List<INode> children = parentToSubnode.get(node.getId());
        if(children == null)
            return;
        node.setChildren(children);
        children.forEach(childNode -> setChildrenOfNode(childNode));
    }

    private void resolveCathegoryToChildrenRelations() {
        boolean showEntireUDC = true;
        if(showEntireUDC) {
            Node udcNode = new Node("-", "UDC");
            int sizeUDCNode = 0;
            for (INode rootNode : UDCRootNodes) {
                List<INode> firstOrderCathegories = parentToSubnode.get(rootNode.getId());
                firstOrderCathegories.stream().forEach(node -> {
                    setChildrenOfNode(node);
                });
                sizeUDCNode += rootNode.getSize();
                rootNode.setChildren(firstOrderCathegories);
                udcNode.getChildren().add(rootNode);
            }
            rootNode = udcNode;
        }else{
            INode mainTableRootNode = null;
            for(INode rootNode: UDCRootNodes){
                if("MAIN TABLES".equals(rootNode.getLabel())){
                    mainTableRootNode = rootNode;
                }
            }

            if(mainTableRootNode == null)
                return;

            List<INode> firstOrderCathegories = parentToSubnode.get(mainTableRootNode.getId());
            firstOrderCathegories.stream().forEach(node -> {
                setChildrenOfNode(node);
            });
            mainTableRootNode.setChildren(firstOrderCathegories);
            rootNode = mainTableRootNode;

        }
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
    static int processedConceptNodes = 0;

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
        Node node = new Node(conceptName, englishLabel);
        if(parentNodeID.isEmpty()){
            UDCRootNodes.add(node);
        }else{
            List<INode> treeNodeList = parentToSubnode.get(parentNodeID);
            if(treeNodeList == null){
                treeNodeList = new ArrayList<INode>();
                parentToSubnode.put(parentNodeID, treeNodeList);
            }
            treeNodeList.add(node);
        }
//        try {
//            csvWriter.write(conceptName  + "," + parentNodeID + "," + englishLabel + "\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Concept: " + conceptName + " Parent: " + parentNodeID + " Label: " + englishLabel);
    }
}
