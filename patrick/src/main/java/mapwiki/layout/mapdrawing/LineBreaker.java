package mapwiki.layout.mapdrawing;

import java.util.ArrayList;
import java.util.List;

abstract class LineBreaker {
	public static final int LINE_BREAK_LEN = 7;
	public static final int MAX_TAIL_LEN = 2;
	
	/**
	 * Wrap a string using the length for one line defined in LINE_BREAK_LEN.
	 * @param s String to be processed.
	 * @return A string list with wrapped lines.
	 */
	public static List<String> wrapLine(String s) {
		String[] broken = s.split("_");
		ArrayList<String> list = new ArrayList<String>(broken.length);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < broken.length; i++) {
			String current = broken[i];
			String next = i < broken.length - 1 ? broken[i + 1] : "";
			sb.append(current);
			if (sb.length() < LINE_BREAK_LEN || next.length() <= MAX_TAIL_LEN) {
				sb.append(" ");
			} else {
				list.add(sb.toString().trim());
				sb.setLength(0);
			}
		}
		if (sb.length() > 0)
			list.add(sb.toString().trim());
		return list;
	}
}
