package mapvis.fileSystemTree;

import mapvis.common.datatype.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 10/7/2015.
 */
public class FilesystemTreeGenerator {

    final String directory;

    public FilesystemTreeGenerator(String directory) {
        this.directory = directory;
    }

    private Node createSubtreeForDirectory(File dir, int id)
    {
        Node currNode = new Node(new Integer(id).toString(), dir.getName());

        File[] filesList = dir.listFiles();
        List<Node> children = new ArrayList<Node>();
        int nodeSize = 0;
        for(File file : filesList){
            if(file.isDirectory()){
                id++;
                nodeSize++;
                children.add(createSubtreeForDirectory(file, id));
            }
        }
        currNode.setVal("size", (double) nodeSize);
        currNode.setChildren(children);
        return currNode;
    }

    public Node genTree()
    {
        File fileDir = new File(directory);
        if(!fileDir.exists())
            return new Node(new Integer(0).toString(), "root");

        return createSubtreeForDirectory(fileDir, 0);
    }

}
