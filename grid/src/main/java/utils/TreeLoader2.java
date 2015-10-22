//package utils;
//
//import mapvis.common.datatype.MPTreeImp;
//import mapvis.common.datatype.Tree2;
//
//import org.supercsv.cellprocessor.constraint.NotNull;
//import org.supercsv.cellprocessor.constraint.UniqueHashCode;
//import org.supercsv.cellprocessor.ift.CellProcessor;
//import org.supercsv.io.CsvMapReader;
//import org.supercsv.prefs.CsvPreference;
//
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class TreeLoader2 {
//    final static String[] header = new String[] { "id", "maxHexagonLevelToShow", "label", "parentid", "size"};
//    final static CellProcessor[] processors = new CellProcessor[] {
//            new UniqueHashCode(),
//            new NotNull(),
//            new org.supercsv.cellprocessor.Optional(),
//            new org.supercsv.cellprocessor.Optional(),
//            new NotNull()
//    };
//
//    public Map<Integer, Node> nodemap = new HashMap<>();
//    public Node root;
//
//    public void load(String file) throws FileNotFoundException {
//        Map<Integer, ArrayList<Integer>> edges = new HashMap<>();
//
//        CsvMapReader reader = null;
//        Scanner scanner = null;
//        try {
//            reader = new CsvMapReader(new FileReader(file), CsvPreference.STANDARD_PREFERENCE);
//            reader.getHeader(true); // ignore the header
//            Map<String, Object> map;
//            while((map = reader.read(header, processors)) != null ) {
//                Node node = new Node();
//                node.id     = Integer.parseInt((String) map.get("id"));
//                node.maxHexagonLevelToShow  = Integer.parseInt((String) map.get("maxHexagonLevelToShow"));
//                node.name   =  map.get("label") == null?"":(String)map.get("label");
//                node.figure = Integer.parseInt((String)  map.get("size"));
//                int pid = Integer.parseInt((String) map.get("parentid"));
//
//                if (pid == -1){
//                    root = node;
//                } else {
//                    ArrayList<Integer> children = edges.get(pid);
//                    if (children == null)
//                        edges.put(pid, children = new ArrayList<>());
//                    children.add(node.id);
//                }
//                nodemap.put(node.id, node);
//            }
//
//            for (Map.Entry<Integer, ArrayList<Integer>> entry : edges.entrySet()) {
//                Node parent = nodemap.get(entry.getKey());
//                parent.children = entry.getValue().stream()
//                        .map(nodemap::get)
//                        .collect(Collectors.toList());
//                for (Node child : parent.children) {
//                    child.parent = parent;
//                }
//            }
//        } catch (FileNotFoundException ex) {
//            System.err.println("Could not find the CSV file: " + ex);
//        } catch (IOException ex) {
//            System.err.println("Error reading the CSV file: " + ex);
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException ex) {
//                    System.err.println("Error closing the reader: " + ex);
//                }
//            }
//        }
//    }
//
//    public Tree2<Node> convertToTreeModel(){
//
//        MPTreeImp<Node> tree = new MPTreeImp<>();
//
//        tree.setRoot(root);
//        translateTreeModel(tree, root);
//
//        return tree;
//    }
//
//    void translateTreeModel(MPTreeImp<Node> tree, Node node) {
//        if (node.children.size() > 0) {
//            for (Node child : node.children) {
//                System.out.printf("%d > %d @ %d\n", node.id, child.id, (int) node.figure);
//                tree.addChild(node, child, (int) child.figure);
//                translateTreeModel(tree, child);
//            }
//        }
//    }
//
//}
