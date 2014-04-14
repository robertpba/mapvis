package mapwiki.common;

public class WikiSimilarityEntry {
	private int pageID1;
	private int pageID2;
	private double similarity;
	private int coCount;
	
	public int getPageID1() {
		return pageID1;
	}

	public int getPageID2() {
		return pageID2;
	}

	public double getSimilarity() {
		return similarity;
	}

	public int getCoCount() {
		return coCount;
	}

	protected WikiSimilarityEntry(int id1, int id2) {
		if (id1 > id2) {
			this.pageID1 = id2;
			this.pageID2 = id1;
		} else {
			this.pageID1 = id1;
			this.pageID2 = id2;
		}
	}

	protected void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	protected void setCoCount(int coCount) {
		this.coCount = coCount;
	}

	@Override
	public String toString() {
		return String.format("[%d, %d, %f, %d]", pageID1, pageID2,
				similarity, coCount);
	}
}
