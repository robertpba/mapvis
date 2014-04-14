package mapwiki.layout.hexagon;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HexagonCanvas implements Serializable {
	private static final long serialVersionUID = -5809613139654516897L;
	
	private int width;
	private int height;
	private double hexSideLength;
	private Hexagon[][] hex;
	private LinkedList<HexagonText> textList = new LinkedList<HexagonText>();
	
	public HexagonCanvas(int w, int h, double hexagonSideLength) {
		width = w;
		height = h;
		hexSideLength = hexagonSideLength;
		hex = new Hexagon[w][h];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				hex[i][j] = new Hexagon(i, j);
	}
	
	public HexagonCanvas() {
		// This constructor is for serialization.
		this(0, 0, 0);
	}
	
	public double getHexSideLength() {
		return hexSideLength;
	}

	public void setHexSideLength(double hexSideLength) {
		this.hexSideLength = hexSideLength;
	}

	public Hexagon get(int x, int y) {
		if (x < 0 || x >= width)
			throw new IllegalArgumentException(String.format("X is out of bounds: %d/%d.", x, width));
		if (y < 0 || y >= height)
			throw new IllegalArgumentException(String.format("Y is out of bounds: %d/%d.", y, height));
		return hex[x][y];
	}
	
	public Hexagon findNeighbour(Hexagon hex, HexDir dir) {
		return findNeighbour(hex.x, hex.y, dir);
	}
	
	public Hexagon findNeighbour(int x, int y, HexDir dir) {
		int nx, ny;
		if (x % 2 != 0) {
			switch (dir) {
			case SOUTH: 		nx = x;		ny = y + 1;	break;
			case SOUTH_WEST:	nx = x - 1;	ny = y + 1; break;
			case NORTH_WEST:	nx = x - 1;	ny = y;		break;
			case NORTH:			nx = x;		ny = y - 1; break;
			case NORTH_EAST:	nx = x + 1;	ny = y;		break;
			case SOUTH_EAST:	nx = x + 1;	ny = y + 1; break;
			default:
				throw new IllegalArgumentException("Direction is incorrect.");
			}
		} else {
			switch (dir) {
			case SOUTH: 		nx = x;		ny = y + 1;	break;
			case SOUTH_WEST:	nx = x - 1;	ny = y; 	break;
			case NORTH_WEST:	nx = x - 1;	ny = y - 1;	break;
			case NORTH:			nx = x;		ny = y - 1; break;
			case NORTH_EAST:	nx = x + 1;	ny = y - 1;	break;
			case SOUTH_EAST:	nx = x + 1;	ny = y;		break;
			default:
				throw new IllegalArgumentException("Direction is incorrect.");
			}
		}
		
		if (nx >= 0 && nx < width && ny >= 0 && ny < height)
			return get(nx, ny);
		else
			return null;
	}
	
	public List<Hexagon> findNeighbours(int x, int y, List<Hexagon> outputList) {
		outputList.clear();
		for (HexDir dir: HexDir.values()) {
			Hexagon h = findNeighbour(x, y, dir);
			if (h != null)
				outputList.add(h);
		}
		return outputList;
	}
	
	public List<Hexagon> findNeighbours(Hexagon hex, List<Hexagon> outputList) {
		return findNeighbours(hex.x, hex.y, outputList);
	}
	
	public List<Hexagon> findNeighbours(int x, int y) {
		ArrayList<Hexagon> list = new ArrayList<Hexagon>(6);
		findNeighbours(x, y, list);
		return Collections.unmodifiableList(list);
	}
	
	public List<Hexagon> findNeighbours(Hexagon hex) {
		return findNeighbours(hex.x, hex.y);
	}

	public void draw(Graphics2D g) {
		if (hexSideLength == 0)
			throw new IllegalStateException("Hexagon length cannot be zero.");
		draw(new DefaultHexagonDrawer(hexSideLength), g);
	}
	
	public void draw(HexagonDrawer drawer, Graphics2D g) {
		drawer.draw(this, g);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return "HexagonCanvas [" + width + "x" + height + "]";
	}
	
	public void addTextLabel(int x, int y, String text, int level, HexagonText.Type type) {
		Hexagon h = get(x, y);
		HexagonText ht = new HexagonText(h.x, h.y, text, level, type);
		textList.add(ht);
	}
	
	public List<HexagonText> textLabels() {
		return textList;
	}
}
