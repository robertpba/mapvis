package mapvis.common.datatype;


import java.util.*;

public class NodeUtils {

    public static int rebuildId(INode root, int nextid) {
        root.setId(Integer.toString(nextid++));
        for (INode child : root.getChildren()) {
            nextid = rebuildId(child, nextid);
        }
        return nextid;
    }

    public static void populateSize(INode root)
    {
        if(root.getChildren().size() == 0){
            root.setSize(-1);
            return;
        }

        int sizeOfCurrentNode = 0;

//        Iterator<Node> it = root.getChildren().iterator();
//        while (it.hasNext()) {
//            Node child = it.next();
//            if(!populateSize(child)) {
//                it.remove();
//                continue;
//            }
//            sizeOfCurrentNode += child.getSize();
//        }
        List<INode> filteredChildren = new ArrayList<>();
        for(INode child: root.getChildren()){
            populateSize(child);
            if(child.getSize() < 0){
                sizeOfCurrentNode++;
                continue;
            }
            sizeOfCurrentNode += child.getSize();
            filteredChildren.add(child);
        }
        root.setChildren(filteredChildren);
        root.setSize(sizeOfCurrentNode);
    }

    public static void populateLevel(INode root, int level){
        root.setVal("level", level);
        for (INode child : root.getChildren()) {
            populateLevel(child, level + 1);
        }
    }

    private static TreeStatistics getTreeStatisticsOfSubNode(final INode node, final int currDepth)
    {
        if(node.getSize() == 0 || node.getLabel().equals("*")){
            return TreeStatistics.createNew(currDepth, node.getLabel(), 0, 0, 0);
        }else if(node.getChildren().size() == 0){
            return TreeStatistics.createNew(currDepth, node.getLabel(), 1, currDepth, 1);
        }

        TreeStatistics maxTreeDepthStatistics = TreeStatistics.createNew(0, node.getLabel());
        maxTreeDepthStatistics.numOfNodes = 1;
        for(INode child: node.getChildren()){
            // children with no size are dummy nodes e.g. folder with no files
//            maxTreeDepthStatistics.numOfNodes++;
            if(child.getSize() == 0){
                continue;
            }

            TreeStatistics childStatistics = getTreeStatisticsOfSubNode(child, currDepth + 1);
            maxTreeDepthStatistics.numOfNodes += childStatistics.numOfNodes;
            maxTreeDepthStatistics.numOfLeaves += childStatistics.numOfLeaves;

            maxTreeDepthStatistics.sumOfDepthsOfLeaves += childStatistics.sumOfDepthsOfLeaves;
            if(childStatistics.maxDepth > maxTreeDepthStatistics.maxDepth){
                maxTreeDepthStatistics.maxDepthPathName = node.getLabel() + ("->" + childStatistics.maxDepthPathName);
                maxTreeDepthStatistics.maxDepth = childStatistics.maxDepth;
            }
        }
        return maxTreeDepthStatistics;
    }

    public static TreeStatistics diffTreeStatistics(TreeStatistics oldStats, TreeStatistics newStats){
        if(oldStats == null || newStats == null)
            return new TreeStatistics(0, "");
        int diffNumOfLeaves = oldStats.numOfLeaves - newStats.numOfLeaves;
        int diffMaxDepth = oldStats.maxDepth - newStats.maxDepth;
        int diffSumOfDepthsOfLeaves = oldStats.sumOfDepthsOfLeaves - newStats.sumOfDepthsOfLeaves;
        int diffNumOfNodes = oldStats.numOfNodes - newStats.numOfNodes;
        int diffSizeOfRootNodes = oldStats.sizeOfRootNode - newStats.sizeOfRootNode;
        float diffAverageDepth = oldStats.calcAverageDepth() - newStats.calcAverageDepth();
        TreeStatistics diffStats = TreeStatistics.createNew(-diffMaxDepth, "", -diffNumOfLeaves,
                -diffSumOfDepthsOfLeaves, -diffNumOfNodes);
        diffStats.sizeOfRootNode = -diffSizeOfRootNodes;
        diffStats.autoCalcAverageDepth = false;
        diffStats.averageDepth = -diffAverageDepth;
        return diffStats;
    }

    public static TreeStatistics getTreeDepthStatistics(final INode node)
    {
        TreeStatistics treeStatistics = getTreeStatisticsOfSubNode(node, 0);
        treeStatistics.sizeOfRootNode = (int) node.getSize();
        treeStatistics.numOfNodes -= 1;//substract 1 to get #nodes without root
        return treeStatistics;
    }

    public static Node filterByDepth(final INode node, final int depth){
        if(depth == 0){
            Node cappedNode = new Node(node.getId(), node.getLabel());
            cappedNode.setSize(node.getSize());
            return cappedNode;
        }
        Node subTreeNode = new Node(node.getId(), node.getLabel());
//        List<Node> filteredChilds = new ArrayList<>();
        for(INode child: node.getChildren()){
            Node newChildNode = filterByDepth(child, depth - 1);
            subTreeNode.getChildren().add(newChildNode);
        }
        subTreeNode.setSize(node.getSize());
        return subTreeNode;
    }

    public static double filterBySize(INode root, double min, String sizeKey){
        if (sizeKey == null) sizeKey = "size";

        if (root.getChildren().isEmpty())
            return (double) root.getVal("size");

        double size = 0;

        List<INode> removed = new LinkedList<>();

        for (INode child : root.getChildren()) {
            double sizeChild = filterBySize(child, min, sizeKey);
            if (sizeChild < min)
                removed.add(child);
            else
                size += sizeChild;
        }

        root.getChildren().removeAll(removed);
        root.setVal(sizeKey, size);

        return size;
    }

    public static List<INode> getLeaves(INode node){
        if (node.getChildren().isEmpty())
            return Arrays.asList(node);

        ArrayList<INode> leaves = new ArrayList<>();

        node.getChildren().stream()
                .map(NodeUtils::getLeaves)
                .forEach(leaves::addAll);
        return leaves;

    }

    public static List<INode> getDecedents(INode node){
        ArrayList<INode> nodes = new ArrayList<>();

        node.getChildren().stream()
                .map(NodeUtils::getDecedents)
                .forEach(nodes::addAll);

        nodes.addAll(node.getChildren());

        return nodes;
    }

}
