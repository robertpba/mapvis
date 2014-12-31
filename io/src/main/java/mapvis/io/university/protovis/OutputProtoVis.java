package mapvis.io.university.protovis;

import mapvis.io.university.Major;
import mapvis.io.university.Node;

import java.io.IOException;
import java.util.List;

import static mapvis.io.university.BuildTree.build;
import static mapvis.io.university.ParseCVS.readCSVFile;

public class OutputProtoVis {

    public static String outputJson(Node root){

        StringBuilder sb = new StringBuilder();
        process(root,sb, "");
        return sb.toString();
    }

    static void process(Node node, StringBuilder sb, String indent){
        if (node.children.isEmpty())
            sb.append(indent).append("\"").append(node.label).append("\"").append(":").append(node.size);
        else{
            sb.append(indent).append("\"").append(node.label).append("\"").append(":{\n");

            for (Node child : node.children) {
                process(child, sb,indent+"  ");
                sb.append(",\n");
            }


            sb.append(indent).append("}");
        }
    }


    public static void main(String[] args) throws IOException {
        List<Major> majors = readCSVFile("data/Student numbers.csv");
        Node root = build(majors);
        String s = outputJson(root);
        System.out.print(s);

    }

}
