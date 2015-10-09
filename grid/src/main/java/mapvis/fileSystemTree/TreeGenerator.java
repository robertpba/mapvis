package mapvis.fileSystemTree;

import mapvis.common.datatype.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 10/8/2015.
 */
public class TreeGenerator {

    private int id = 0;

    private int getNewID(){
        return id++;
    }
//    final String directory;

//    public FileSystemTreeGenerator(String directory) {
//        this.directory = directory;
//    }
//
//    private Node createSubtreeForDirectory(File dir, int id)
//    {
//        Node currNode = new Node(new Integer(id).toString(), dir.getName());
//
//        File[] filesList = dir.listFiles();
//        List<Node> children = new ArrayList<Node>();
//        int nodeSize = 0;
//        for(File file : filesList){
//            if(file.isDirectory()){
//                id++;
//                nodeSize++;
//                children.add(createSubtreeForDirectory(file, id));
//            }
//        }
//        currNode.setVal("size", (double) nodeSize);
//        currNode.setChildren(children);
//        return currNode;
//    }

    private Node genSubTree(ITreeNode iTreeNode) {
        Node node = new Node(Integer.toString(getNewID()), iTreeNode.getName());
        List<ITreeNode> children = iTreeNode.getChildren();

        for (ITreeNode child : children) {
            ++id;
            node.getChildren().add(genSubTree(child));
        }
        node.setVal("size", (double) children.size());
        return node;
    }

    public Node genTree(ITreeNode root) {
        return genSubTree(root);
    }
}
