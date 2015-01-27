package mapvis.io.random;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public String root_label = "root";
    public int level_max = 2; // the root is not counted
    public int level_min = 2; // at least 1
    public int seed = 0;
    public List<LevelCfg> levels = new ArrayList<>();
}
