//package mapvis.io.university.ehta;
//
//import mapvis.Impl.MPTree;
//import utils.Node;
//
//public class OutputMPTree {
//
//    public MPTree<Node> getMPTree(mapvis.io.university.Node root) {
//        MPTree<Node> tree = new MPTree<Node>();
//
//        Node unode = new Node();
//        unode.id = root.id;
//        unode.level = root.level;
//        unode.name = root.label;
//        unode.figure = root.size;
//
//        tree.setRoot(unode);
//
//        for (mapvis.io.university.Node child : root.children) {
//            process(unode, child, tree);
//        }
//
//        return tree;
//    }
//
//    public Node process(Node parent, mapvis.io.university.Node node, MPTree<Node> tree){
//        Node unode = new Node();
//        unode.id    = node.id;
//        unode.level = node.level;
//        unode.name  = node.label;
//        unode.figure = node.size;
//
//        tree.addChild(parent, unode, node.size);
//
//        for (mapvis.io.university.Node child : node.children) {
//            process(unode, child, tree);
//        }
//
//        return unode;
//    }
//}
