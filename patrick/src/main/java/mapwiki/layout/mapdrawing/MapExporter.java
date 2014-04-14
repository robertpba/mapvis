package mapwiki.layout.mapdrawing;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import mapwiki.layout.hexagon.Hexagon;
import mapwiki.layout.hexagon.HexagonCanvas;

public class MapExporter {
	protected PrintStream out;
	private static final String[] COL_COLOUR = new String[] { "cid",
		"r", "g", "b" };
	private static final String[] COL_HEXAGON = new String[] { "x", "y",
		"cat", "alt", "cat_level", "colour" };
	private static final String[] COL_BORDER = new String[] { "x", "y",
		"ne", "se", "s", "sw", "nw", "n" };
	private static final String[] COL_LABEL = new String[] { "title", 
		"x1", "y1", "x2", "y2", "level" };
	
	public void exportCanvas(HexagonCanvas c) {
		writeInsert("canvas", new String[] { "w", "h", "hex_side_length" },
			new Object[] { c.getWidth(), c.getHeight(), c.getHexSideLength() } );
	}
	
	
	public void exportHexagon(Hexagon h, String cat, int catLevel, int colourLevel) {
		writeInsert("hexagon", COL_HEXAGON, new Object[] { h.x, h.y, cat,
				h.getAltitude(), catLevel, colourLevel });
	}

	public void exportBorder(Hexagon h, int... borders) {
		writeInsert("border", COL_BORDER, new Object[] { h.x, h.y, borders[0],
				borders[1], borders[2], borders[3], borders[4], borders[5] });
	}
	
	public void exportColours(AltitudeLevel[] levels) {
		for (int i = 0; i < levels.length; i++) {
			Color c = levels[i].colour;
			writeInsert("colour", COL_COLOUR, new Object[] { i, c.getRed(), 
					c.getGreen(), c.getBlue() } );
		}
	}
	
	public MapExporter(String filename) throws FileNotFoundException, UnsupportedEncodingException {
		out = new PrintStream(filename, "utf-8");
		writeCreateStatements();
	}
	
	public void close() {
		if (out != null) {
			out.println("COMMIT;");
			out.close();
			out = null;
		}
	}
	
	protected void writeCreateStatements() {
		// Begin transaction.
		out.println("BEGIN TRANSACTION;");
		// Canvas table.
		out.println("CREATE TABLE canvas (w INTEGER, h INTEGER, hex_side_length REAL);");
		// Colour table.
		out.println("CREATE TABLE colour (cid INTEGER PRIMARY KEY, r INTEGER, g INTEGER, " +
				"b INTEGER, a INTEGER);");
		// Hexagon table.
		out.println("CREATE TABLE hexagon (x INTEGER, y INTEGER, cat VARCHAR(255), " +
				"alt REAL, cat_level INTEGER, colour INTEGER, UNIQUE (x, y), " +
				"FOREIGN KEY (colour) REFERENCES colour (cid));");
		// Border table.
		out.println("CREATE TABLE border (x INTEGER, y INTEGER, ne INTEGER, se INTEGER, " +
				"s INTEGER, sw INTEGER, nw INTEGER, n INTEGER, UNIQUE (x, y));");
		// Label table.
		out.println("CREATE TABLE label (title VARCHAR(255), x1 INTEGER, y1 INTEGER, " +
				"x2 INTEGER, y2 INTEGER, level INTEGER);");
		out.println("CREATE INDEX idx_label_x1 ON label (x1);");
		out.println("CREATE INDEX idx_label_y1 ON label (y1);");
		out.println("CREATE INDEX idx_label_x2 ON label (x2);");
		out.println("CREATE INDEX idx_label_y2 ON label (y2);");
	}
	
	protected void writeInsert(String table, String[] cols, Object[] values) {
		if (cols.length != values.length)
			throw new IllegalArgumentException("Number of columns and values do not match.");
		
		out.print("INSERT INTO ");
		out.print(table);
		out.print(" (");
		for (int i = 0; i < cols.length; i++) {
			out.print(cols[i]);
			out.print(i >= cols.length - 1 ? ")" : ",");
		}
		out.print(" VALUES (");
		for (int i = 0; i < values.length; i++) {
			if (values[i] instanceof String) {
				out.print("'");
				out.print(((String)values[i]).replaceAll("'", "''"));
				out.print("'");
			} else {
				out.print(values[i]);
			}
			out.print(i >= values.length - 1 ? ")" : ",");
		}	
		out.println(";");
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (out != null)
			close();
		super.finalize();
	}


	public void exportCatLabel(CategoryLabel cl) {
		List<String> lines = cl.lines;
		CategoryBoundary cb = cl.catBoundary;
		StringBuilder sb = new StringBuilder(lines.size() * 50);
		for (int i = 0; i < lines.size(); i++) {
			sb.append(lines.get(i));
			if (i < lines.size() - 1)
				sb.append("\n");
		}
		writeInsert("label", COL_LABEL, new Object[] { sb.toString(), cb.rectX1, 
				cb.rectY1, cb.rectX2, cb.rectY2, cl.level });
	}
}
