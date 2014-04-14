package mapwiki.layout;


class SpringLayoutLink implements Comparable<SpringLayoutLink> {
	private double similarity;
	private Category category1;
	private Category category2;
	
	public SpringLayoutLink(double sim, Category c1, Category c2) {
		category1 = c1;
		category2 = c2;
		similarity = sim;
	}
	
	public boolean isRelated() {
		return similarity > 0;
	}

	public double getSimilarity() {
		return similarity;
	}

	public Category getCategory1() {
		return category1;
	}

	public Category getCategory2() {
		return category2;
	}
	
	@Override
	public String toString() {
		return "SpringLayoutLink [c1=" + category1.getPageTitle() +
			", c2=" + category2.getPageTitle() + ", sim=" +
			similarity + "]";
	}
	
	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null || !(arg0 instanceof SpringLayoutLink))
			return false;
		SpringLayoutLink other = (SpringLayoutLink)arg0;
		return category1.equals(other.category1) && category2.equals(other.category2); 
	}

	@Override
	public int hashCode() {
		return (category1 + "|" + category2).hashCode();
	}

	@Override
	public int compareTo(SpringLayoutLink o) {
		if (o == null)
			return 1;
		
		int result = Double.compare(similarity, o.similarity);
		if (result == 0 && category1 != null) {
			result = category1.getPageTitle().compareTo(o.category1.getPageTitle());
			if (result == 0 && category2 != null)
				result = category2.getPageTitle().compareTo(o.category2.getPageTitle());
		}
		return result;
	}
}
