package mapvis.liquidvis.util;

import mapvis.liquidvis.model.Node;

import java.io.*;
import java.util.*;

public class PatrickFormatLoader {

    // = 1143
    public int numNodes;

    public List<Node> nodes = new ArrayList<>();

    public int height;
    public int width;

    public int numPolygon=0;

    // id : 667
    public Node root;

    public Node load(String treefile, String pointfile) throws IOException
    {
        loadCategoryName(treefile);
        loadPoints(pointfile);
        scaleAttribute(root);
        filterUnpositionedNodes(root);
        filterSmallNodes(root);
        refinePoints();
        nodes = visbl;

        return root;
    }

    public Map<Integer, Node> nodemap = new HashMap<>();

    public ArrayList<Node> leaves = new ArrayList();

    public List<Node> visbl = new ArrayList<>();

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
                //node.figure = Math.log10(node.figure) / Math.log10(1.1);
                //node.figure = node.figure * 6;
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

    protected void loadPoints(String filePath) throws IOException {
        Scanner scanner = null;

        double xmax = 0;
        double ymax = 0;

        try {
            FileInputStream stream = new FileInputStream(filePath);
            stream.skip(3); // skip BOM 65279 / EF BB BF
            scanner = new Scanner(new InputStreamReader(stream));

            while (scanner.hasNext()) {
                int id = scanner.nextInt();
                scanner.next();
                scanner.next();

                Node node = nodemap.get(id);
                node.x = scanner.nextDouble();
                node.y = scanner.nextDouble();

                xmax = Math.max(node.x, xmax);
                ymax = Math.max(node.y, ymax);

                visbl.add(node);
            }

            width  = (int)xmax;
            height = (int)ymax;
        }finally{
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    protected void scaleAttribute(Node root){
        //root.x = root.x * 36;
        //root.y = root.y * 36;
        root.figure = root.figure == 0.0? 0.0 : Math.log10(root.figure) / Math.log10(1.1) * 10;
        for (Node child : root.children) {
            scaleAttribute(child);
        }
    }

    protected boolean filterUnpositionedNodes(Node root) {
        if (root.children.length == 0) {
            if (!visbl.contains(root))
                return false;
            return true;
        }

        List<Node> children = new ArrayList<>();

        for (Node child : root.children) {
            if (filterUnpositionedNodes(child))
                children.add(child);
        }

        if (children.isEmpty())
            return false;

        root.children = children.toArray(new Node[children.size()]);
        return true;
    }


    protected boolean filterSmallNodes(Node root) {
        if (root.children.length == 0) {
            if (root.figure <= 0.01)
                return false;

            leaves.add(root);
            return true;
        }

        List<Node> children = new ArrayList<>();

        for (Node child : root.children) {
            if (filterSmallNodes(child))
                children.add(child);
        }

        if (children.isEmpty())
            return false;

        root.children = children.toArray(new Node[children.size()]);
        return true;
    }


    protected void refinePoints()
    {
        for (Node node : nodes) {
            node.x = node.x * 2;
            node.y = node.y * 2;
        }
        width  = (width + 50)* 2;
        height = (height + 50)* 2;
    }

    public static void main (String[] args) throws IOException {
        PatrickFormatLoader loader = new PatrickFormatLoader();

        loader.loadCategoryName("data/simple.txt");
        loader.loadPoints("data/points.txt");
        loader.scaleAttribute(loader.root);
        loader.filterUnpositionedNodes(loader.root);
        loader.filterSmallNodes(loader.root);
        loader.refinePoints();

        printNode (loader, "", loader.root, 6);
    }


    private static void printNode(PatrickFormatLoader loader, String indent, Node node, int maxlevel)
    {
        if (node.level >= maxlevel)
            return;

        System.out.printf("%d  %s%-5d %s %f (%5d, %5d)\n",
                node.level, indent, node.pageId, node.name, node.figure, (int)node.x, (int)node.y);
        for (Node child : node.children)
        {
            printNode(loader, indent+"  ", child, maxlevel);
        }
    }


}
