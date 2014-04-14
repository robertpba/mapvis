package mapwiki.layout.hexagon;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import mapwiki.layout.Category;
import mapwiki.layout.CategoryProvider;

public class Hexagon implements Serializable {
	private static final long serialVersionUID = 2539207945763915961L;
	
	public static final int CATEGORY_ESSENTIAL = 10000; 
	public static final int EMPTY = 0;
	public int x;
	public int y;
	
	private boolean essential;	// True if this hexagon is an essential for a category.
	private float altitude;	// Represents the current most significant altitude.
	private int category = EMPTY;	// Represents the current most significant category.
	private transient Category catObjectCache = null;
	private transient Map<String, Integer> catInfo = new TreeMap<String, Integer>();
	private transient double distance = Double.MAX_VALUE;
	
	public void resetCatInfo() {
		essential = false;
		category = EMPTY;
		catObjectCache = null;
		distance = Double.MAX_VALUE;
		altitude = 0;
		catInfo.clear();
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void assignCategory(Category cat, float weight, double d, int flags) {
		if (flags == CATEGORY_ESSENTIAL) {
			essential = true;
			setCategory(cat);
			distance = Double.MIN_VALUE;
			altitude = weight;
			return;
		}
		
		// If not essential, determine the category by distance.
		if (d <= distance) {
			distance = d;
			altitude = weight;
			setCategory(cat);
		}
	}
	
	@Override
	public String toString() {
		return "Hexagon [" + x + "," + y + "]";
	}

	public float getAltitude() {
		return altitude;
	}
	
	public int getCategory() {
		return category;
	}
	
	// Helper method to obtain the category object easier and with cache.
	public Category getCategory(CategoryProvider cp) {
		if (catObjectCache != null) {
			return catObjectCache;
		} else {
			catObjectCache = cp.findByID(category);
			return catObjectCache;
		}
	}
	
	private void setCategory(Category c) {
		this.category = c.getPageID();
		this.catObjectCache = c;
	}
	
	public boolean isEssential() {
		return essential;
	}
	
	public Hexagon(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Hexagon() {
		// For serialization use.
	}
	
	public boolean isEmpty() {
		return category == EMPTY;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		// Create the map again because no initialization performed after
		// un-serialization.
		catInfo = new TreeMap<String, Integer>();
	}
}
