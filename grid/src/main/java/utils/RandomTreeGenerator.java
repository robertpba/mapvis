package utils;

import mapvis.Impl.MPTree;

import java.util.Random;

public class RandomTreeGenerator {

    private int seed;

    public RandomTreeGenerator(int seed){
        this.seed = seed;
        rn = new Random(seed);
    }

    static Random rn;
    int lastId = 0;

    void getTree(MPTree<Node> tree, Node node, int level, int maxLevel, int span, int weight){
        if (level > maxLevel)
            return ;

        int n = rn.nextInt(span);

        while (n-- > 0){
            Node child = new Node();
            child.id = ++lastId;
            tree.addChild(node,  child, rn.nextInt(weight));
            getTree(tree, child, level+1, maxLevel, span, weight);
        }
    }
    public MPTree<Node> getTree(int level, int span, int weight){
        rn.setSeed(seed);
        lastId = 0;
        MPTree<Node> mptree = new MPTree<>();
        Node child = new Node();
        child.id = 0;
        mptree.setRoot(child);
        getTree(mptree, child, 0, level, span, weight);
        return mptree;
    }
}
