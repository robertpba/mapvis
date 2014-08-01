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

    void getTree(MPTree<Integer> tree, Integer node, int level, int maxLevel, int span, int weight){
        if (level > maxLevel)
            return ;

        int n = rn.nextInt(span);

        while (n-- > 0){
            int cid = ++lastId;
            tree.addChild(node, cid, rn.nextInt(weight));
            getTree(tree, cid, level+1, maxLevel, span, weight);
        }
    }
    public MPTree<Integer> getTree(int level, int span, int weight){
        rn.setSeed(seed);
        lastId = 0;
        MPTree<Integer> mptree = new MPTree<>();
        mptree.setRoot(lastId = 0);
        getTree(mptree, 0, 0, level, span, weight);
        return mptree;
    }
}
