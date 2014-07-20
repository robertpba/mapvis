package mapvis;

import mapvis.tree.MPTT;

import java.util.Random;

public class RandomData {
    public static Random rn = new Random(1);
    static int lastId = 0;

    public static MPTT<Integer> getTree(){
        MPTT<Integer> mptt = new MPTT<>();
        mptt.setRoot(lastId = 0);
        getTree(mptt, 0, 0, 2, 10, 100);
        mptt.refresh();
        return mptt;
    }

    static void getTree(MPTT<Integer> tree, Integer node, int level, int maxLevel, int span, int weight){
        if (level > maxLevel)
            return ;

        int n = rn.nextInt(span);

        while (n-- > 0){
            int cid = ++lastId;
            tree.addChild(node, cid, rn.nextInt(weight));
            getTree(tree, cid, level+1, maxLevel, span, weight);
        }
    }
    public static MPTT<Integer> getTree(int level, int span, int weight){
        MPTT<Integer> mptt = new MPTT<>();
        mptt.setRoot(lastId = 0);
        getTree(mptt, 0, 0, level, span, weight);
        mptt.refresh();
        return mptt;
    }
}
