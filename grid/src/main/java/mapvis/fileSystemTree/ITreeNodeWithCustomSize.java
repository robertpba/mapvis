package mapvis.fileSystemTree;

import java.util.List;

/**
 * Created by dacc on 10/14/2015.
 */
public interface ITreeNodeWithCustomSize{
    List<ITreeNodeWithCustomSize> getChildren();

    String getName();

    double getNodeSize();
}
