package mapwiki.spectrum;

class CatLink implements Comparable<CatLink> {
	private String category1;
	private String category2;
	private double similarity;
	
	public String getCategory1() {
		return category1;
	}
	
	public void setCategory1(String category1) {
		this.category1 = category1;
	}
	
	public String getCategory2() {
		return category2;
	}
	
	public void setCategory2(String category2) {
		this.category2 = category2;
	}
	
	public double getSimilarity() {
		return similarity;
	}
	
	public void setSimilarity(double value) {
		this.similarity = value;
	}
	
	public CatLink(String cat1, String cat2, double sim) {
		this.category1 = cat1;
		this.category2 = cat2;
		this.similarity = sim;
	}

	@Override
	public String toString() {
		return category1 + " " + similarity + " " + category2;		
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null || !(arg0 instanceof CatLink))
			return false;
		CatLink other = (CatLink)arg0;
		return category1.equals(other.category1) && category2.equals(other.category2); 
	}

	@Override
	public int hashCode() {
		return (category1 + "|" + category2).hashCode();
	}

	@Override
	public int compareTo(CatLink o) {
		if (o == null)
			return 1;
		
		int result = Double.compare(this.similarity, o.similarity);
		if (result == 0 && this.category1 != null) {
			result = this.category1.compareTo(o.category1);
			if (result == 0 && this.category2 != null)
				result = this.category2.compareTo(o.category2);
		}
		return result;
	}
}
