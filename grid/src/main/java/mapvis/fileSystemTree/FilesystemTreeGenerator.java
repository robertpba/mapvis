package mapvis.fileSystemTree;

import mapvis.common.datatype.Node;
import mapvis.common.datatype.NodeUtils;

import java.io.File;

/**
 * Created by dacc on 10/14/2015.
 */
public class FilesystemTreeGenerator {
    private int id = 0;

    private int getNewID(){
        return id++;
    }

    private Node createSingleNodeFromFile(File file){
        Node node = new Node(Integer.toString(getNewID()), file.getName());
        return node;
    }

    private double getRoundedFileSizeInKB(File file){
        double fileSize = Math.round(file.length() / (1024.0 ));
        //set min size to 1
        fileSize = Math.max(fileSize, 1.0);
        return fileSize;
    }

    private Node createNodeForFile(File file) {
        Node node = createSingleNodeFromFile(file);

        double countDirectFilesInFolder = 0;
        double countSubdirFilesInFolder = 0;

        for (File child : file.listFiles()) {
            // for directories the sum of the size of the subfolders is used
            if(child.isDirectory()){
                Node childNode = createNodeForFile(child);
                node.getChildren().add(childNode);
                countSubdirFilesInFolder += (double) childNode.getVal("size");
            // files are counted
            }else if(child.isFile()){
                countDirectFilesInFolder += 1.0;
            }
        }
        // if there are files and subdirectories, create a node which holds all the files
        // to make sure the current node has the size of all subnodes including the files
        // directly in the folder
        if(countDirectFilesInFolder > 0 && countSubdirFilesInFolder != 0){
            Node dummyChildForDirectFilesInFolder = new Node(Integer.toString(getNewID()), "*");
            dummyChildForDirectFilesInFolder.setSize(countDirectFilesInFolder);
            node.getChildren().add(dummyChildForDirectFilesInFolder);
        }
        node.setVal("size", countDirectFilesInFolder + countSubdirFilesInFolder);
        return node;
    }

    public Node genTree(final File filePath) {

        if(!filePath.exists()){
            return createSingleNodeFromFile(filePath);
        }

        Node rootTreeNode = createNodeForFile(filePath);
        System.out.println("Tree Depth: " + NodeUtils.getTreeDepthStatistics(rootTreeNode));
        return rootTreeNode;
    }
}
