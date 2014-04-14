package mapwiki.layout;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class Category {
	private int pageID;
	private String pageTitle;
	private Point location;
	private Point pivot;
	private Dimension size;		// Size that is proportional to article number.
	private Dimension border;	// A border that contains all its children areas.
	private int area;
	private int articleCount;
	
	public Rectangle drawingRect() {
		if (location == null)
			throw new NullPointerException("Location is null: " + pageTitle);
		if (pivot == null)
			throw new NullPointerException("Pivot is null: " + pageTitle);
		
		Dimension d = border != null ? border : size; 
		int x = location.x - pivot.x;
		int y = location.y - pivot.y;
		int width = d.width;
		int height = d.height;
		return new Rectangle(x, y, width, height);
	}
	
	public Point getPivot() {
		return pivot;
	}

	public void setPivot(Point pivot) {
		this.pivot = pivot;
	}
	
	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
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
	
	public Category() {
	}
	
	public Category(int pageID, String pageTitle) {
		this.pageID = pageID;
		this.pageTitle = pageTitle;
	}

	@Override
	public String toString() {
		return "Category [id=" + pageID + ", title=" + pageTitle + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Category)
			return pageID == ((Category)obj).pageID;
		else
			return false;
	}

	@Override
	public int hashCode() {
		return pageID;
	}

	public Dimension getBorder() {
		return border;
	}

	public void setBorder(Dimension border) {
		this.border = border;
	}

	public void setArticleCount(int articleCount) {
		this.articleCount = articleCount;
	}

	public int getArticleCount() {
		return articleCount;
	}
}
