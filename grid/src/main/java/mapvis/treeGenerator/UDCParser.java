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
 * This class is used to parse the UDC Summary Linked Data
 * to create a tree of INodes. The UDC Summary Linked Data
 * is available on http://www.udcdata.info/.
 */
public class UDCParser {

    private String path;
    private List<INode> UDCRootNodes;
    private HashMap<String, List<INode>> parentToSubnode;
    private INode rootNode;
    private boolean showEntireUDC = true;

    public UDCParser() {
        this.path = null;
        this.UDCRootNodes = new ArrayList<>();
        this.parentToSubnode = new HashMap<>();
    }

    public void initialize(String path){
        this.UDCRootNodes.clear();
        this.parentToSubnode.clear();
        this.path = path;
        this.rootNode = null;
    }

    public INode generateUDCCathegories(){
        if(path == null)
            return new Node("0", "empty Path");

        if(this.rootNode == null) {
            try {
                readUDC();
                resolveCategoryToChildrenRelations();
            } catch (ParserConfigurationException e) {
                System.out.println("UDC Summary Parsing failed (ParserConfig): " + e.getMessage());
            } catch (SAXException e) {
                System.out.println("UDC Summary Parsing failed (SAX): " + e.getMessage());
            } catch (IOException e) {
                System.out.println("UDC Summary Parsing failed (IO): " + e.getMessage());
            }
        }
        return rootNode;
    }


    private void setChildrenOfNode(INode node){
        //query children of parent node
        List<INode> children = parentToSubnode.get(node.getId());
        if(children == null)
            return; //leaf

        //process children
        node.setChildren(children);
        children.forEach(childNode -> setChildrenOfNode(childNode));
    }

    private void resolveCategoryToChildrenRelations() {

        if(showEntireUDC) {
            //UDC Summary contains AUXILIARY TABLES and MAIN TABLES
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
            //create Tree only containing MAIN TABLES of UDC Summary
            INode mainTableRootNode = null;
            for(INode rootNode: UDCRootNodes){
                if("MAIN TABLES".equals(rootNode.getLabel())){
                    mainTableRootNode = rootNode;
                }
            }

            if(mainTableRootNode == null)
                return;

            //recursively set children
            List<INode> firstOrderCategories = parentToSubnode.get(mainTableRootNode.getId());
            firstOrderCategories.stream().forEach(node -> {
                setChildrenOfNode(node);
            });

            mainTableRootNode.setChildren(firstOrderCategories);
            rootNode = mainTableRootNode;
        }
    }

    /**
     * Parses the UDC Linked Data Summary at the specified path and
     * stores the parent to child mapping in parentToSubnode;
     * UDC RootNodes are stored in UDCRootNodes
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private void readUDC() throws ParserConfigurationException, IOException, SAXException {
        //Create DOM parser to read to parse the XML
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        FileInputStream in = new FileInputStream(new File(path));
        Document doc = db.parse(in, "UTF-8");

        //query first oder hierarchy
        NodeList nList = doc.getElementsByTagName("skos:Concept");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            org.w3c.dom.Node nNode = nList.item(temp);
            //process each concept
            if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                processConceptNode(eElement);
            }
        }
    }

    private void processConceptNode(Element conceptNode)  {
        NodeList childNodes = conceptNode.getChildNodes();
        String conceptName = conceptNode.getAttribute("rdf:about");
        String parentNodeID = "";
        String englishLabel = "";

        for (int conceptNodeChildIndex = 0; conceptNodeChildIndex < childNodes.getLength(); conceptNodeChildIndex++) {
            //process XML attributes to set parentNode (skos:broader) and label (skos:prefLabel)
            if(childNodes.item(conceptNodeChildIndex).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
                Element conceptNodeElement = (Element) childNodes.item(conceptNodeChildIndex);
                switch (conceptNodeElement.getNodeName()){
                    case "skos:broader":
                        parentNodeID = conceptNodeElement.getAttribute("rdf:resource");
                        break;
                    case "skos:prefLabel":
                        //select english description label
                        if( conceptNodeElement.getAttribute("xml:lang").equals("en")){
                            englishLabel = conceptNodeElement.getTextContent();
                        }
                        break;
                }
            }
        }

        Node node = new Node(conceptName, englishLabel);
        if(parentNodeID.isEmpty()){
            //no parent node => one of the two UDC root nodes found
            UDCRootNodes.add(node);
        }else{
            //store parent of node in HashMap to reconstruct the tree afterwards
            List<INode> treeNodeList = parentToSubnode.get(parentNodeID);
            if(treeNodeList == null){
                //entry for parent does not yet exist
                treeNodeList = new ArrayList<INode>();
                parentToSubnode.put(parentNodeID, treeNodeList);
            }
            treeNodeList.add(node);
        }
    }
}
