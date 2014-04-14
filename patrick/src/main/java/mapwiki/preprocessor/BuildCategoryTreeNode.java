package mapwiki.preprocessor;

import java.io.PrintStream;

class BuildCategoryTreeNode {
	private int pageID;
	private String pageTitle;
	private int level;
	private int parentPageID;
	private String parentPageTitle;
	private int articleCount;
	private float altitude;
	
	public BuildCategoryTreeNode(int pageID, String pageTitle) {
		this.pageID = pageID;
		this.pageTitle = pageTitle;
	}

	public int getPageID() {
		return pageID;
	}

	public void setPageID(int pageID) {
		this.pageID = pageID;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getParentPageID() {
		return parentPageID;
	}

	public void setParentPageID(int parentPageID) {
		this.parentPageID = parentPageID;
	}

	public String getParentPageTitle() {
		return parentPageTitle;
	}

	public void setParentPageTitle(String parentPageTitle) {
		this.parentPageTitle = parentPageTitle;
	}

	@Override
	public int hashCode() {
		return pageID;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BuildCategoryTreeNode other = (BuildCategoryTreeNode) obj;
		if (pageID != other.pageID)
			return false;
		return true;
	}
	
	public void write(PrintStream out) {
		out.print(getPageID());
		out.print("\t");
		out.print(getPageTitle());
		out.print("\t");
		out.print(getLevel());
		out.print("\t");
		out.print(parentPageID == 0 ? "" : getParentPageID());
		out.print("\t");
		out.print(parentPageID == 0 ? "" : getParentPageTitle());
		out.print("\t");
		out.print(getArticleCount());
		out.print("\t");
		out.print(getAltitude());
		out.println();
	}

	public int getArticleCount() {
		return articleCount;
	}

	public void setArticleCount(int articleCount) {
		this.articleCount = articleCount;
	}

	@Override
	public String toString() {
		return pageID + " " + pageTitle;
	}

	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}

	public float getAltitude() {
		return altitude;
	}
}
