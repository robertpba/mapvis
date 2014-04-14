package mapwiki.preprocessor;

class CalcCosSimEntry implements Comparable<CalcCosSimEntry> {
	private int pageID;
	private int articleCount;
	private String pageTitle;
	
	@Override
	public int compareTo(CalcCosSimEntry other) {
		if (this.pageID > other.pageID)
			return 1;
		else if (this.pageID == other.pageID)
			return 0;
		else
			return -1;
	}

	public void setPageID(int pageID) {
		this.pageID = pageID;
	}

	public int getPageID() {
		return pageID;
	}

	public void setArticleCount(int articleCount) {
		this.articleCount = articleCount;
	}

	public int getArticleCount() {
		return articleCount;
	}
	
	@Override
	public int hashCode() {
		return pageID;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CalcCosSimEntry)
			return pageID == ((CalcCosSimEntry)obj).pageID;
		else
			return false;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
}
