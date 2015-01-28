package mapvis.io.random;

import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;

import java.util.Random;

public class RandomTreeGenerator {
    Config config;
    private int nextid;
    private Random rn;

    public RandomTreeGenerator(){
        this.rn = new Random();
    }

    public MPTreeImp<Node> getTree(Config config){
        this.config = config;
        rn.setSeed(config.seed);
        nextid = 1;

        MPTreeImp<Node> mptree = new MPTreeImp<>();
        Node child = new Node("0", "root");

        mptree.setRoot(child);
        generateChildren(config.levels.get(0), mptree, child, 0);
        return mptree;

    }

    void generateChildren(LevelCfg levelCfg, MPTreeImp<Node> tree, Node node, int level){
        if (level >= config.level_max)
            return ;

        int n = getRandomWidth(levelCfg);

        levelCfg = config.levels.get(level);
        while (n-- > 0){
            String id = Integer.toString(nextid++);
            Node child = new Node(id, id);
            tree.addChild(node, child, getRandomWeight(levelCfg));
            generateChildren(levelCfg, tree, child, level + 1);
        }
    }

    private int getRandomWidth(LevelCfg levelCfg) {
        return levelCfg.width_min + rn.nextInt(levelCfg.width_max - levelCfg.width_min);
    }
    private int getRandomWeight(LevelCfg levelCfg) {
        return levelCfg.weight_min + rn.nextInt(levelCfg.weight_max - levelCfg.weight_min);
    }

}
