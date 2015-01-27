package mapvis.io.random;

import java.util.ArrayList;

import static org.testng.Assert.*;

public class YamlCfgReaderTest {

    @org.testng.annotations.Test
    public void testDump() throws Exception {
        Config config = new Config();
        config.levels = new ArrayList<>();
        config.levels.add(new LevelCfg());
        config.levels.add(new LevelCfg());
        YamlCfgReader reader = new YamlCfgReader();
        System.err.print(reader.dump(config));
    }

    @org.testng.annotations.Test
    public void testRead() throws Exception {
        YamlCfgReader reader = new YamlCfgReader();
        Config config = reader.read("io/data/cfg.yaml");
    }
}