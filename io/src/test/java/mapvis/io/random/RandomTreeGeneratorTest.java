package mapvis.io.random;

import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.MapNodeImp;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.*;

public class RandomTreeGeneratorTest {

    @Test
    public void testGetTree() throws Exception {

        Config config = getConfig();
        System.out.print( new YamlCfgReader().dump(config));

        RandomTreeGenerator generator = new RandomTreeGenerator();
        MPTreeImp<MapNodeImp> tree = generator.getTree(config);

        System.out.println(tree);
    }

    Config getConfig(){
        Config config = new Config();
        config.level_min = 2;
        config.level_max = 2;
        config.root_label = "root";
        config.levels = new ArrayList<>(2);

        LevelCfg levelCfg = new LevelCfg();
        levelCfg.weight_min = 20;
        levelCfg.weight_max = 100;
        levelCfg.width_min  = 4;
        levelCfg.width_max  = 7;

        config.levels.add(levelCfg);

        levelCfg = new LevelCfg();
        levelCfg.weight_min = 20;
        levelCfg.weight_max = 100;
        levelCfg.width_min  = 3;
        levelCfg.width_max  = 10;
        config.levels.add(levelCfg);

        return config;

    }
}