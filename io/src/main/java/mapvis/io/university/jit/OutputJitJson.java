package mapvis.io.university.jit;

import mapvis.io.university.Major;
import mapvis.io.university.Node;

import java.io.IOException;
import java.util.List;

import static mapvis.io.university.BuildTree.build;
import static mapvis.io.university.ParseCVS.readCSVFile;

public class OutputJitJson {

    public static String outputJson(Node root){

        StringBuilder sb = new StringBuilder();
        process(root,sb, "");
        return sb.toString();
    }

    static void process(Node node, StringBuilder sb, String indent){
        sb.append(indent).append("{\n");
        sb.append(indent).append("  id:").append(node.id).append(",\n");
        sb.append(indent).append("  name:\"").append(node.label).append("\",\n");
        sb.append(indent).append("  data:{\n");
        sb.append(indent).append("    type:\"").append(node.type).append("\",\n");
        sb.append(indent).append("    level:").append(node.level).append(",\n");
        sb.append(indent).append("    $area:").append(node.size).append("\n");

        sb.append(indent).append("  },\n");

        if (node.children.size() == 0)
            sb.append(indent).append("  children:[]\n");
        else {
            sb.append(indent).append("  children:[\n");
            for (Node child : node.children) {
                process(child, sb,indent+"  ");
                sb.append(",\n");
            }
            sb.append(indent).append("  ]\n");
        }
        sb.append(indent).append("}");
    }


    public static void main(String[] args) throws IOException {
        List<Major> majors = readCSVFile("data/Student numbers.csv");
        Node root = build(majors);
        String s = outputJson(root);
        System.out.print(s);

    }

}
