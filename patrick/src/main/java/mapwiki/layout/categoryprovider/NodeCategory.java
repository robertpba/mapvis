package mapwiki.layout.categoryprovider;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;
import mapwiki.layout.Category;

import java.util.List;

public class NodeCategory extends Category {
    public INode node;
    public NodeCategory parent;
    public List<NodeCategory> children;
}
