package mapvis.common.datatype;


public class NodeUtils {

    public static int rebuildId(Node root, int nextid) {
        root.id = Integer.toString(nextid++);
        for (Node child : root.getChildren()) {
            nextid = rebuildId(child, nextid);
        }
        return nextid;
    }

    public static void populateLevel(Node root, int level){
        root.setVal("level", level);
        for (Node child : root.getChildren()) {
            populateLevel(child, level + 1);
        }
    }

    int populateSize(Node root){
       return 0;
    }


}
