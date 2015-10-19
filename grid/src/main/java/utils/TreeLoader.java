//package utils;
//
//import mapvis.common.datatype.MPTreeImp;
//import mapvis.common.datatype.Tree2;
//
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.util.*;
//import java.util.stream.*;
//
//public class TreeLoader {
//    public Map<Integer, Node> nodemap = new HashMap<>();
//    public int lastId = 0;
//    public Node root;
//
//    public void load(String file) throws FileNotFoundException {
//        MPTreeImp<Integer> mptree = new MPTreeImp<>();
//        mptree.setRoot(lastId = 0);
//
//        Map<Integer, ArrayList<Integer>> edges = new HashMap<>();
//
//        Scanner scanner = null;
//        try {
//            scanner = new Scanner(new FileReader(file));
//
//            while (scanner.hasNext()) {
//                Node node = new Node();
//                node.id = ++lastId;
//                node.pageId = scanner.nextInt();
//                node.name = scanner.next().replace('_', ' ');
//                node.maxHexagonLevelToShow = scanner.nextInt();
//
//                int parentId = scanner.nextInt();
//
//                nodemap.put(node.pageId, node);
//
//                if (parentId == 0) {
//                    root = node;
//                    scanner.next();
//                    scanner.next();
//                    scanner.next();
//                    continue;
//                }
//
//                scanner.next();
//                node.figure = scanner.nextInt();
//                scanner.next();
//
//                ArrayList<Integer> children = edges.get(parentId);
//                if (children == null)
//                    edges.put(parentId, children = new ArrayList<>());
//                children.add(node.pageId);
//
//            }
//
//            for (Map.Entry<Integer, ArrayList<Integer>> entry : edges.entrySet()) {
//                Node parent = nodemap.get(entry.getKey());
//                parent.children = entry.getValue().stream()
//                        .map(nodemap::get)
//                        .collect(Collectors.toList());
//                for (Node child : parent.children) {
//                    child.parent = parent;
//                }
//            }
//
//        }finally{
//            if (scanner != null) {
//                scanner.close();
//            }
//        }
//    }
//
//    void generateDummyNode(Node node){
//        if (node.children.size() > 0) {
//            int acc=0;
//            for (Node child : node.children) {
//                generateDummyNode(child);
//                acc += child.figure;
//            }
//            if (node.figure > 0){
//                Node dummy = new Node();
//                dummy.id = ++lastId;
//                dummy.maxHexagonLevelToShow = node.maxHexagonLevelToShow + 1;
//                dummy.figure = node.figure/5;
//                dummy.name = node.name;
//                dummy.parent = node;
//                node.children.add(dummy);
//                acc += dummy.figure;
//            }
//            node.figure = acc;
//        }
//    }
//
//
//    void prune(Node node, int maxlevel, int minweight){
//        List<Node> children = new ArrayList<>();
//
//        for (Node child : node.children) {
//            if (child.maxHexagonLevelToShow > maxlevel || child.figure < minweight)
//                continue;
//            prune(child, maxlevel, minweight);
//            children.add(child);
//        }
//        node.children = children;
//    }
//
//    public Tree2<Node> convertToTreeModel(){
//
//        MPTreeImp<Node> tree = new MPTreeImp<>();
//
//        generateDummyNode(root);
//        prune(root, 3, 5);
//
//        tree.setRoot(root);
//        translateTreeModel(tree, root);
//
//        return tree;
//    }
//
//    void translateTreeModel(MPTreeImp<Node> tree, Node node) {
//        if (node.children.size() > 0) {
//            for (Node child : node.children) {
//                System.out.printf("%d > %d @ %d\n", node.id, child.id, (int) node.figure);
//                tree.addChild(node, child, (int) child.figure);
//                translateTreeModel(tree, child);
//            }
//        }
//    }
//
//}
