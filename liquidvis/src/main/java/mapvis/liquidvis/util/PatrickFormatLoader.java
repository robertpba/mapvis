package mapvis.liquidvis.util;

import mapvis.liquidvis.model.Node;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;

public class PatrickFormatLoader {

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
        loadCategoryName(categoryNameFilePath);
        return root;
    }

    public Map<Integer, Node> nodemap = new HashMap<>();

    protected void loadCategoryName(String categoryNameFilePath) throws FileNotFoundException
    {
        Map<Integer, ArrayList<Integer>> edges = new HashMap<>();

        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileReader(categoryNameFilePath));

            while (scanner.hasNext()) {
                Node node = new Node();
                node.id = numNodes++;
                node.pageId = scanner.nextInt();
                node.name = scanner.next().replace('_', ' ');
                node.level = scanner.nextInt();

                int parentId = scanner.nextInt();

                nodemap.put(node.pageId, node);
                nodes.add(node);

                if (parentId == 0) {
                    root = node;
                    scanner.next();
                    scanner.next();
                    scanner.next();
                    continue;
                }

                scanner.next();
                node.figure = scanner.nextInt();
                node.figure = Math.log10(node.figure) / Math.log10(1.1);
                node.figure = node.figure * 6;
                scanner.next();

                ArrayList<Integer> children = edges.get(parentId);
                if (children == null)
                    edges.put(parentId, children = new ArrayList<>());
                children.add(node.pageId);
            }

            for (Map.Entry<Integer, ArrayList<Integer>> entry : edges.entrySet()) {
                Node parent = nodemap.get(entry.getKey());
                parent.children = entry.getValue().stream()
                        .map(id->nodemap.get(id))
                        .toArray(Node[]::new);
                for (Node child : parent.children) {
                    child.parent = parent;
                }
            }

        }finally{
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    public static void main (String[] args)
    {
        PatrickFormatLoader loader = new PatrickFormatLoader();
        try {
            loader.loadCategoryName("data/simple.txt");

            printNode ("", loader.root);


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


}
