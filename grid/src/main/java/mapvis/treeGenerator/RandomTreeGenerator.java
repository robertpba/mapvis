package mapvis.treeGenerator;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;

import java.util.Random;

public class RandomTreeGenerator {

    private int seed;
    private static Random rn;
    private int lastId = 0;

    public RandomTreeGenerator(int seed){
        this.seed = seed;
        rn = new Random(seed);
    }

    private void getTree(MPTreeImp<INode> tree, INode node, int level, int maxLevel, int span, int weight){
        if (level > maxLevel){
            return ;
        }

        int n = rn.nextInt(span);
        int size = 0;
        while (n-- > 0){
            String id = Integer.toString(++lastId);
            Node child = new Node(id, "#"+id);
            tree.addChild(node, child, rn.nextInt(weight));

            getTree(tree, child, level+1, maxLevel, span, weight);
            node.getChildren().add(child);
        }
    }
    public MPTreeImp<INode> getTree(int level, int span, int weight){
        rn.setSeed(seed);
        lastId = 0;
        MPTreeImp<INode> mptree = new MPTreeImp<>();
        Node child = new Node("0","root");
        mptree.setRoot(child);
        getTree(mptree, child, 0, level, span, weight);
        return mptree;
    }
}
