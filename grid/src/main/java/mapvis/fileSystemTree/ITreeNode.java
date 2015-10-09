package mapvis.fileSystemTree;

import java.util.List;

/**
 * Created by dacc on 10/8/2015.
 */
public interface ITreeNode {

    List<ITreeNode> getChildren();

    String getName();

}
