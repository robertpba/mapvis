package mapvis.layouts.dac;

import mapvis.common.datatype.Node;
import mapvis.layouts.Dac;
import mapvis.layouts.peap.PEAPreliminaryLayoutAlgorithmTest;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;

public class DacTest {

    @Test
    public void testRun() throws Exception {

    }

    public static void main(String[] args) throws IOException {
        Yaml yaml = new Yaml();
        FileInputStream ins = new FileInputStream("io/data/rand02.yaml");
        Node node = yaml.loadAs(ins, Node.class);

        Dac.run(node, 2000);

        System.out.println(yaml.dumpAsMap(node));

        //OvalPanelCanvas.start(node);
        PEAPreliminaryLayoutAlgorithmTest.OvalPanelCanvas.export(node);
    }

}