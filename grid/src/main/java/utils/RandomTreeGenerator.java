package utils;

import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;

import java.util.Random;

public class RandomTreeGenerator {

    private int seed;

    public RandomTreeGenerator(int seed){
        this.seed = seed;
        rn = new Random(seed);
    }

    static Random rn;
    int lastId = 0;

    void getTree(MPTreeImp<Node> tree, Node node, int level, int maxLevel, int span, int weight){
        if (level > maxLevel)
            return ;

        int n = rn.nextInt(span);

        while (n-- > 0){
            String id = Integer.toString(++lastId);
            Node child = new Node(id, "#"+id);
            tree.addChild(node,  child, rn.nextInt(weight));
            node.getChildren().add(child);

            getTree(tree, child, level+1, maxLevel, span, weight);
        }
    }
    public MPTreeImp<Node> getTree(int level, int span, int weight){
        rn.setSeed(seed);
        lastId = 0;
        MPTreeImp<Node> mptree = new MPTreeImp<>();
        Node child = new Node("0","root");
        mptree.setRoot(child);
        getTree(mptree, child, 0, level, span, weight);
        return mptree;
    }
}
