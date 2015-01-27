package mapvis.common.datatype;

import java.util.ArrayList;
import java.util.List;

public interface Node {
    public String getId();
    public void setId(String id);

    public String getLabel();
    public void setLabel(String name);

    public Tree getTree();
    public void setTree(Tree tree);
}
