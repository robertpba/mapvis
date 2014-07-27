package mapvis;

import mapvis.tree.MPTree;
import mapvis.tree.TreeModel;

import java.util.Random;

public class RandomData {
    public static Random rn = new Random(1);
    static int lastId = 0;

    public static TreeModel<Integer> getTree(){
        MPTree<Integer> mptree = new MPTree<>();
        mptree.setRoot(lastId = 0);
        getTree(mptree, 0, 0, 2, 10, 100);
        return mptree;
    }

    static void getTree(MPTree<Integer> tree, Integer node, int level, int maxLevel, int span, int weight){
        if (level > maxLevel)
            return ;

        int n = rn.nextInt(span);

        while (n-- > 0){
            int cid = ++lastId;
            tree.addChild(node, cid, rn.nextInt(weight));
            getTree(tree, cid, level+1, maxLevel, span, weight);
        }
    }
    public static MPTree<Integer> getTree(int level, int span, int weight){
        MPTree<Integer> mptree = new MPTree<>();
        mptree.setRoot(lastId = 0);
        getTree(mptree, 0, 0, level, span, weight);
        return mptree;
    }
}
