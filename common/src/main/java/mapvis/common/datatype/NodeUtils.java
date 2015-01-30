package mapvis.common.datatype;


import java.util.*;

public class NodeUtils {

    public static int rebuildId(Node root, int nextid) {
        root.id = Integer.toString(nextid++);
        for (Node child : root.getChildren()) {
            nextid = rebuildId(child, nextid);
        }
        return nextid;
    }

    public static void populateLevel(Node root, int level){
        root.setVal("level", level);
        for (Node child : root.getChildren()) {
            populateLevel(child, level + 1);
        }
    }

    int populateSize(Node root){
       return 0;
    }

    public static double filterBySize(Node root, double min, String sizeKey){
        if (sizeKey == null) sizeKey = "size";

        if (root.getChildren().isEmpty())
            return (double) root.getVal("size");

        double size = 0;

        List<Node> removed = new LinkedList<>();

        for (Node child : root.getChildren()) {
            double sizeChild = filterBySize(child, min, sizeKey);
            if (sizeChild < min)
                removed.add(child);
            else
                size += sizeChild;
        }

        root.getChildren().removeAll(removed);
        root.setVal(sizeKey, size);

        return size;
    }

    public static List<Node> getLeaves(Node node){
        if (node.getChildren().isEmpty())
            return Arrays.asList(node);

        ArrayList<Node> leaves = new ArrayList<>();

        node.getChildren().stream()
                .map(NodeUtils::getLeaves)
                .forEach(leaves::addAll);
        return leaves;

    }

    public static List<Node> getDecedents(Node node){
        ArrayList<Node> nodes = new ArrayList<>();

        node.getChildren().stream()
                .map(NodeUtils::getDecedents)
                .forEach(nodes::addAll);

        nodes.addAll(node.getChildren());

        return nodes;
    }

}
