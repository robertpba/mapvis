package mapvis.treeGenerator;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;

/**
 * Created by dacc on 10/23/2015.
 */
public interface ITreeGenerator {

    void configure(INode rootNode);

    INode genTree();
}
