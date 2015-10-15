package mapvis.fileSystemTree;

import javafx.util.Pair;
import mapvis.common.datatype.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by dacc on 10/14/2015.
 */
public class FilesystemTreeGenerator {
    private int id = 0;

    private int getNewID(){
        return id++;
    }

//    private double createChildrenForNode(final File file, Node node) {
//        double fileSize = 0;
//        File[] children = file.listFiles();
//        for (File child : children) {
//            Node childNode = new Node(Integer.toString(getNewID()), child.getName());
//            if(child.isDirectory()){
//                fileSize += createChildrenForNode(child, childNode);
//            }else{
//                fileSize += getRoundedUpFileSizeInMB(child);
//            }
//            node.getChildren().add(childNode);
//        }
//
//        node.setVal("size", fileSize);
//        return numOfChildren;
//    }
//

//
//    public Node genTree(ITreeNode root) {
//        Node rootNode = new Node(Integer.toString(getNewID()), root.getName());
//        int numOfChildren = createChildrenForNode(root, rootNode);
//        rootNode.setVal("size", (double) numOfChildren);
//        return rootNode;
//    }
    private Node createSingleFromFile(File file){
        Node node = new Node(Integer.toString(getNewID()), file.getName());
        return node;
    }

    private double getRoundedFileSizeInMBMinOneMB(File file){
        double fileSize = Math.round(file.length() / (1024.0 ));
        //set min size to 1
        fileSize = Math.max(fileSize, 1.0);
        return fileSize;
    }

    private Node createNodeForFile(File file) {
        Node node = createSingleFromFile(file);
        double fileSize = 0;
        if(file.isDirectory()){
            for (File child : file.listFiles()) {
                Node childNode = createNodeForFile(child);
                node.getChildren().add(childNode);
                fileSize += (double) childNode.getVal("size");
            }
        }else{
            fileSize = getRoundedFileSizeInMBMinOneMB(file);
        }

        node.setVal("size", fileSize);
        System.out.println("Size of File " + node.getLabel() + " : " + fileSize + " MB");
        return node;
    }

    public Node genTree(final File filePath) {

        if(!filePath.exists()){
            return createSingleFromFile(filePath);
        }

        Node rootTreeNode = createNodeForFile(filePath);
        return rootTreeNode;
    }
}
