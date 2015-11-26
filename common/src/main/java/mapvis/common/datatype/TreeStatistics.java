package mapvis.common.datatype;

/**
 * Created by dacc on 10/19/2015.
 */
public class TreeStatistics {
    public int maxDepth;
    public int numOfLeaves;
    public int sizeOfRootNode;
    public int numOfNodes;
    public int sumOfDepthsOfLeaves;
    public String maxDepthPathName;
    float averageDepth;
    boolean autoCalcAverageDepth;

    public TreeStatistics(final int maxDepth, final String maxDepthPathName) {
        this.maxDepth = maxDepth;
        this.maxDepthPathName = maxDepthPathName;
        this.sumOfDepthsOfLeaves = 0;
        this.numOfLeaves = 0;
        this.sizeOfRootNode = 0;
        this.autoCalcAverageDepth = true;
        this.numOfNodes = 0;
    }

    public TreeStatistics(final int maxDepth, final String maxDepthPathName, final int numOfLeaves,
                          final int sumOfDepthsOfLeaves, final int numOfNodes) {
        this.maxDepth = maxDepth;
        this.maxDepthPathName = maxDepthPathName;
        this.sumOfDepthsOfLeaves = sumOfDepthsOfLeaves;
        this.numOfLeaves = numOfLeaves;
        this.autoCalcAverageDepth = true;
        this.sizeOfRootNode = 0;
        this.numOfNodes = numOfNodes;
    }

    public static TreeStatistics createNew(final int maxDepth, final String maxDepthPathName,
                                           final int numOfLeaves, final int sumOfDepthsOfLeaves,
                                           final int numOfNodes) {
        return new TreeStatistics(maxDepth, maxDepthPathName, numOfLeaves, sumOfDepthsOfLeaves, numOfNodes);
    }

    public static TreeStatistics createNew(final int maxDepth, final String maxDepthPathName) {
        return new TreeStatistics(maxDepth, maxDepthPathName);
    }

    public float calcAverageDepth() {
        if (numOfLeaves == 0)
            return 0;

        averageDepth = ((float) sumOfDepthsOfLeaves) / ((float) numOfLeaves);
        return averageDepth;
    }

    public static String createLegend(){
        return    "\nMax Depth:\t The max number of edges from the a node to the tree's root node."
                + "\nMax Path:\t\t Sequence of nodes for max depth"
//                + "\n#Leaves:\t\t The number of nodes in the resulting tree without children (without *)"
                + "\nLeaves:\t\t The sum of all weights of the leaves, #hexagons (e.g. count of files, final UDC classes)"
                + "\n#Nodes:\t\t The number of nodes (without * and root, e.g. directories, UDC classes)"
                + "\nAvg Depth:\t The average depth per leave";
    }

    public String createStatisticsOverview(boolean withLegend){
        String result = toString() + (withLegend ? ("\n" + createLegend()) : "");
        return result;
    }

    @Override
    public String toString() {
        return "Statistics:"
                + "\nMax Depth:\t" + maxDepth
                + "\nMax Path:\t\t" + maxDepthPathName
//                + "\nLeaves:\t\t" + numOfLeaves
                + "\nLeaves:\t\t" + sizeOfRootNode
                + "\n#Nodes:\t\t" + numOfNodes
                + "\nAvg Depth:\t" + (autoCalcAverageDepth ? calcAverageDepth() : averageDepth);
    }
}
