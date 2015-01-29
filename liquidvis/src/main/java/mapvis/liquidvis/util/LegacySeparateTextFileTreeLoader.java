package mapvis.liquidvis.util;

import mapvis.common.datatype.NodeUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LegacySeparateTextFileTreeLoader{

    // = 1143
    public int numNodes;

    public ArrayList<Node> nodes = new ArrayList<>();

    public int height;
    public int width;

    public int numPolygon=0;

    // id : 667
    public Node root;

    public Node load(String finalPointInfoFilePath, String edgeInfoFilePath, String categoryNameFilePath) throws FileNotFoundException
    {
        loadFinalPointInfo(finalPointInfoFilePath);
        loadEdgeInfo(edgeInfoFilePath);
        loadCategoryName(categoryNameFilePath);
        return root;      	
    }

    protected void loadCategoryName(String categoryNameFilePath) throws FileNotFoundException
    {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileReader(categoryNameFilePath));
            for(int id = 0; scanner.hasNext(); id++)
            {
                nodes.get(id).name = scanner.next().replace('_',' ');
            }
        }finally{
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    protected void loadEdgeInfo(String filePath) throws FileNotFoundException {

        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileReader(filePath));

            // edge map :  source node index <1-----n> child nodes
            Map<Integer, ArrayList<Node>> edges = new HashMap<>();
            while(scanner.hasNext())
            {
                ArrayList<Node> children;
                int parentId = scanner.nextInt();
                children = edges.get(parentId);
                if (children == null)
                    edges.put(parentId, children = new ArrayList<>());

                int childId = scanner.nextInt();
                Node child = nodes.get(childId);
                children.add(child);
            }

            // builds up object graph
            for(int parentId : edges.keySet())
            {
                Node parent = nodes.get(parentId);
                ArrayList<Node> children = edges.get(parentId);
                parent.children = children.toArray(new Node[children.size()]);
                for (Node child : children)
                    child.parent = parent;
            }

            // find the root
            for (Node node : nodes) {
                if (node.parent == null)
                {
                    root = node;
                    break;
                }
            }
        }finally{
            if (scanner != null)
                scanner.close();
        }
    }

    protected void loadFinalPointInfo(String filePath) throws FileNotFoundException
    {
        Scanner scanner = null;

        try{
            scanner = new Scanner(new FileReader(filePath));

            // the first line consists of two space separated integers
            height = (int) scanner.nextDouble();
            width  = (int) scanner.nextDouble();

            // each of the following lines consists of  five fields
            for(int id = 0; scanner.hasNext(); id++)
            {
                Node node = new Node();
                node.id = id;
                node.pageId = scanner.nextInt();
                node.level  = scanner.nextInt();

                node.figure = scanner.nextDouble();
                node.x      = scanner.nextDouble();
                node.y      = scanner.nextDouble();

                // position[i][5]=-1;

                if (node.level==4)
                    numPolygon++;
                numNodes ++;

                nodes.add(node);
            }
        }finally{
            if (scanner != null)
                scanner.close();
        }
    }


    public static void main (String[] args) 
    {
        LegacySeparateTextFileTreeLoader loader = new LegacySeparateTextFileTreeLoader();
        try {
            loader.load("liquidvis/data/finalPointInfo.txt",
                    "liquidvis/data/edgeInfo.txt",
                    "liquidvis/data/categoryName.txt");
            
            //printNode ("", loader.root);


            Yaml yaml = new Yaml();
            mapvis.common.datatype.Node nodes1 = convertToNodes(loader.root);
            NodeUtils.rebuildId(nodes1,0);
            NodeUtils.populateLevel(nodes1,0);
            String s = yaml.dumpAsMap(nodes1);


            System.out.println(s);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    
    
    private static void printNode(String indent, Node node)
    {
        System.out.printf("%d  %s%-5d %s\n", node.level, indent, node.id, node.name);
        for (Node child : node.children)
        {
            printNode(indent+"  ", child);
        }
    }


    static mapvis.common.datatype.Node convertToNodes(Node n0){
        mapvis.common.datatype.Node n1 =
                new mapvis.common.datatype.Node(
                        Integer.toString(n0.id), n0.name);
        n1.setVal("articles", (int)n0.figure);
        n1.setVal("pageid", n0.pageId);
        n1.setVal("x", n0.x);
        n1.setVal("y", n0.y);

        for (Node child : n0.children) {
            mapvis.common.datatype.Node c1 = convertToNodes(child);
            n1.getChildren().add(c1);
        }

        return n1;
    }





}
