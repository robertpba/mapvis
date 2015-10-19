package mapvis.common.datatype;

/**
 * Created by dacc on 10/19/2015.
 */
public class TreeStatistics {
    public int maxDepth;
    public int numOfLeaves;
    public int sumOfDepthsOfLeaves;
    public String maxDepthPathName;
    float averageDepth;
    boolean autoCalcAverageDepth;

    public TreeStatistics(final int maxDepth, final String maxDepthPathName) {
        this.maxDepth = maxDepth;
        this.maxDepthPathName = maxDepthPathName;
        this.sumOfDepthsOfLeaves = 0;
        this.numOfLeaves = 0;
        this.autoCalcAverageDepth = true;
    }

    public TreeStatistics(final int maxDepth, final String maxDepthPathName, final int numOfLeaves, final int sumOfDepthsOfLeaves) {
        this.maxDepth = maxDepth;
        this.maxDepthPathName = maxDepthPathName;
        this.sumOfDepthsOfLeaves = sumOfDepthsOfLeaves;
        this.numOfLeaves = numOfLeaves;
        this.autoCalcAverageDepth = true;
    }

    public static TreeStatistics createNew(final int maxDepth, final String maxDepthPathName, final int numOfLeaves, final int sumOfDepthsOfLeaves) {
        return new TreeStatistics(maxDepth, maxDepthPathName, numOfLeaves, sumOfDepthsOfLeaves);
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

    @Override
    public String toString() {
        return "Statistics:"
                + "\nMax Depth:\t" + maxDepth
                + "\nMax Path:\t\t" + maxDepthPathName
                + "\nLeaves:\t\t" + numOfLeaves
                + "\nAvg Depth:\t" + (autoCalcAverageDepth ? calcAverageDepth() : averageDepth);
    }
}
