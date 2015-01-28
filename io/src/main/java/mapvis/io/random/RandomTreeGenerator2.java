package mapvis.io.random;

import mapvis.common.datatype.Node;

import java.util.Random;

public class RandomTreeGenerator2 {
    Config config;
    private int nextid;
    private Random rn;

    public RandomTreeGenerator2(){
        this.rn = new Random();
    }

    public Node getTree(Config config){
        this.config = config;
        rn.setSeed(config.seed);
        nextid = 0;

        Node root = generateSubTree(0);
        root.setLabel("root");

        RandomColorGenerator colorGenerator = new RandomColorGenerator();
        colorGenerator.color(root,1);

        return root;
    }

    Node generateSubTree(int level) {
        String id = Integer.toString(nextid++);
        Node node = new Node(id, "#"+id);
        node.setVal("level", level);

        int size = 0; // sum of sizes

        if (level >= config.level_max) {
            LevelCfg levelCfg = config.levels.get(level-1);
            size = getRandomWeight(levelCfg);
        } else {
            LevelCfg levelCfg = config.levels.get(level);
            int n = getRandomWidth(levelCfg);

            while (n-- > 0) {
                Node child = generateSubTree(level+1);
                node.getChildren().add(child);
                size += (int) child.getVal("size");
            }
        }

        node.setVal("size", size);
        return node;
    }

    private int getRandomWidth(LevelCfg levelCfg) {
        return levelCfg.width_min + rn.nextInt(levelCfg.width_max - levelCfg.width_min);
    }
    private int getRandomWeight(LevelCfg levelCfg) {
        return levelCfg.weight_min + rn.nextInt(levelCfg.weight_max - levelCfg.weight_min);
    }


}
