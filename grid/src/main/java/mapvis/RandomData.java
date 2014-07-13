package mapvis;

import mapvis.tree.MPTT;

import java.util.Random;

public class RandomData {
    static Random rn = new Random(1);
    static int lastId = 0;

    public static MPTT<Integer> getTree(){
        MPTT<Integer> mptt = new MPTT<>();
        mptt.setRoot(lastId = 0);
        getTree(mptt, 0, 0);
        mptt.refresh();
        return mptt;
    }

    static void getTree(MPTT<Integer> tree, Integer node, int level){
        if (level == 3)
            return ;

        int n = rn.nextInt(10);

        while (n-- > 0){
            int cid = ++lastId;
            tree.addChild(node, cid, rn.nextInt(100));
            getTree(tree, cid, level+1);
        }
    }
}
