package mapvis.io.university.ehta;

import mapvis.io.university.Major;
import mapvis.io.university.Node;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;


import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static mapvis.io.university.BuildTree.build;
import static mapvis.io.university.ParseCVS.readCSVFile;

public class OutputTree {

    final static String[] header = new String[] { "id", "level", "label", "parentid", "size"};
    final static CellProcessor[] processors = new CellProcessor[] {
            new UniqueHashCode(),
            new NotNull(),
            new Optional(),
            new Optional(),
            new NotNull()
    };

    public static void output(Node root) throws IOException {
        ICsvMapWriter writer = null;
        try {

         writer = new CsvMapWriter(
                new FileWriter("university_data_tree.csv"),
                CsvPreference.STANDARD_PREFERENCE);

            writer.writeHeader(header);


        process(-1, root, writer);

    }
    finally {
        if( writer != null ) {
            writer.close();
        }
    }

}

    private static void process(int parentid, Node node, ICsvMapWriter writer) throws IOException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", node.id);
        map.put("level", node.level);
        map.put("label", node.label);
        map.put("parentid",parentid);
        map.put("size", node.size);

        writer.write(map,header,processors);

        for (Node child : node.children) {
            process(node.id, child, writer);
        }
    }

    public static void main(String[] args) throws IOException {
        List<Major> majors = readCSVFile("data/Student numbers.csv");
        Node root = build(majors);
        output(root);
    }

}
