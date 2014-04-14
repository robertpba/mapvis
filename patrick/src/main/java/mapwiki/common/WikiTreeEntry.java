package mapwiki.common;

import java.util.LinkedList;
import java.util.List;

public class WikiTreeEntry {
	private int pageID;
	private String pageTitle;
	private byte level;
	private WikiTreeEntry parent;
	private int articleCount;
	private float altitude;
	private List<WikiTreeEntry> children = new LinkedList<WikiTreeEntry>();
	
	public int getPageID() {
		return pageID;
	}

	protected void setPageID(int pageID) {
		this.pageID = pageID;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	protected void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public byte getLevel() {
		return level;
	}

	protected void setLevel(byte level) {
		this.level = level;
	}

	public WikiTreeEntry getParent() {
		return parent;
	}

	protected void setParent(WikiTreeEntry parent) {
		this.parent = parent;
	}

	public int getArticleCount() {
		return articleCount;
	}

	protected void setArticleCount(int articleCount) {
		this.articleCount = articleCount;
	}
	
	public List<WikiTreeEntry> getChildren() {
		return this.children;
	}

	protected WikiTreeEntry() {
	}
	
	@Override
	public String toString() {
		return this.pageTitle;
	}

	public float getAltitude() {
		return altitude;
	}

	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}
}
