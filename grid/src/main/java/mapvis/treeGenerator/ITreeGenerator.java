package mapvis.treeGenerator;

import mapvis.common.datatype.INode;

/**
 * Created by dacc on 10/23/2015.
 */
public interface ITreeGenerator {

    void configure(INode rootNode);

    INode genTree();
}
