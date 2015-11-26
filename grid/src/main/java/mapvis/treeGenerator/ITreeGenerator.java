package mapvis.treeGenerator;

import mapvis.common.datatype.INode;

/**
 * Created by dacc on 10/23/2015.
 */
public interface ITreeGenerator {

    /**
     * sets the root node required to process the tree
     * @param rootNode of the tree
     */
    void setRootNode(INode rootNode);

    /**
     * generates the tree using the defined rootNode by
     * querying the children of the rootNode until leaves
     * are reached
     * @return the rootNode of the generated Tree
     */
    INode genTree();
}
