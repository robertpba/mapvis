package mapwiki.common;

public class PageEntry {
	protected int pageID;
	protected String pageTitle;
	protected String category;
	protected int pageLength;
	
	protected PageEntry() {
	}
	
	public int getPageID() {
		return pageID;
	}
	
	public String getPageTitle() {
		return pageTitle;
	}
	
	public String getCategory() {
		return category;
	}
	
	public int getPageLength() {
		return pageLength;
	}
	
	@Override
	public String toString() {
		return pageTitle;
	}

	@Override
	public int hashCode() {
		return pageID;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj == null)
			return false;
		else if (getClass() != obj.getClass())
			return false;
		else
			return ((PageEntry)obj).pageID == this.pageID;
	}
}
