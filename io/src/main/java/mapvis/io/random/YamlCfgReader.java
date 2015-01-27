package mapvis.io.random;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class YamlCfgReader {
    DumperOptions options;
    //Constructor constructor;
    Yaml yaml;

    public YamlCfgReader(){
        options = new DumperOptions();
        //options.setPrettyFlow(true);
        //options.setCanonical(true);

        //constructor = new Constructor(Config.class);
        //constructor.addTypeDescription(new TypeDescription(Config.class));

        yaml = new Yaml(/*constructor,*/ new Representer(), options);

    }

    public String dump(Config config) {
        return  yaml.dumpAsMap(config);
    }

    public Config read(String filename) throws FileNotFoundException {
        InputStream input = new FileInputStream(new File(filename));
        return yaml.loadAs(input, Config.class);
    }
}
