package mapwiki.layout.hexagon;

import java.io.Serializable;

public class HexagonText implements Serializable {
	private static final long serialVersionUID = 7620098643533953009L;

	public enum Type { CATEGORY, PAGE };
	
	private String text;
	private Type type = Type.CATEGORY;
	private int level;
	private int x;
	private int y;
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return text + "";
	}

	public HexagonText(int x, int y, String text, int level, Type type) {
		this.x = x;
		this.y = y;
		this.text = text;
		this.type = type;
		this.level = level;
	}
	
	public HexagonText() {
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}
}
