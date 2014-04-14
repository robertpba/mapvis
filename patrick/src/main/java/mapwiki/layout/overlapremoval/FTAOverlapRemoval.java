package mapwiki.layout.overlapremoval;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FTAOverlapRemoval<T> {
	protected enum Direction { RIGHT, LEFT, UP, DOWN };
	protected enum CoordType { X, Y };
	
	private int xGap = 0;
	private int yGap = 0;
	
	private RectConverter<T> rc;
	private EntryComparator<T> xComp = new EntryComparator<T>(CoordType.X);
	private EntryComparator<T> yComp = new EntryComparator<T>(CoordType.Y);
	private List<Entry<T>> entries = new ArrayList<Entry<T>>();
	
	public FTAOverlapRemoval(List<T> elementList, RectConverter<T> converter) {
		this.rc = converter;
		for (T obj: elementList) {
			FTAEntry<T> entry = new FTAEntry<T>(obj, this.rc.convert(obj));
			entries.add(entry);
		}
	}
	
	public List<Entry<T>> entryList() {
		return Collections.unmodifiableList(entries);
	}
	
	public void run() {
		rightHorizontalScan();
		leftHorizontalScan();
		upVerticalScan();
		downVerticalScan();
	}
	
	private void rightHorizontalScan() {
		Collections.sort(entries, xComp);
		for (int i = 0; i < entries.size(); i++) {
			Entry<T> vi = entries.get(i);
			Set<Entry<T>> rns = findNS(vi, Direction.RIGHT);
			if (!rns.isEmpty()) {
				Set<Entry<T>> rtns = findTNS(vi, Direction.RIGHT);
				double f = 0;
				for (Entry<T> vj: rns) {
					double fx = Math.abs(vi.getNewRect().getMaxX() - vj.getNewRect().getX());
					double fy = Math.min(Math.abs(vi.getNewRect().getMaxY() - vj.getNewRect().getY()), 
							Math.abs(vi.getNewRect().getY() - vj.getNewRect().getMaxY()));
					double delta = Math.min(fx, fy);
					if (delta == fx && (delta < f || f == 0))
						f = delta;
				}
				if (f != 0) {
					for (Entry<T> e: rtns)
						e.getNewRect().x += f + xGap;
				}
			}
		}
	}
	
	private void leftHorizontalScan() {
		Collections.sort(entries, xComp);
		for (int i = entries.size() - 1; i >= 0; i--) {
			Entry<T> vi = entries.get(i);
			Set<Entry<T>> lns = findNS(vi, Direction.LEFT);
			if (!lns.isEmpty()) {
				Set<Entry<T>> ltns = findTNS(vi, Direction.LEFT);
				double f = 0;
				for (Entry<T> vj: lns) {
					double fx = Math.abs(vi.getNewRect().getX() - vj.getNewRect().getMaxX());
					double fy = Math.min(Math.abs(vi.getNewRect().getY() - vj.getNewRect().getMaxY()), 
							Math.abs(vi.getNewRect().getMaxY() - vj.getNewRect().getY()));
					double delta = Math.min(fx, fy);
					if (delta == fx && (delta > f || f == 0))
						f = delta;
				}
				if (f != 0) {
					for (Entry<T> e: ltns)
						e.getNewRect().x -= f - xGap;
				}
			}
		}
	}
	
	private void upVerticalScan() {
		Collections.sort(entries, yComp);
		for (int i = entries.size() - 1; i >= 0; i--) {
			Entry<T> vi = entries.get(i);
			Set<Entry<T>> uns = findNS(vi, Direction.UP);
			if (!uns.isEmpty()) {
				Set<Entry<T>> utns = findTNS(vi, Direction.UP);
				double f = 0;
				for (Entry<T> vj: uns) {
					double fy1 = Math.abs(vi.getNewRect().getMaxY() - vj.getNewRect().getY()); 
					double fy2 = Math.abs(vi.getNewRect().getY() - vj.getNewRect().getMaxY());
					double delta = Math.min(fy1, fy2);
					if (delta == fy1 && (delta > f || f == 0))
						f = delta;
				}
				if (f != 0) {
					for (Entry<T> e: utns)
						e.getNewRect().y -= f - yGap;
				}
			}
		}
	}
	
	private void downVerticalScan() {
		Collections.sort(entries, yComp);
		for (int i = 0; i < entries.size(); i++) {
			Entry<T> vi = entries.get(i);
			Set<Entry<T>> dns = findNS(vi, Direction.DOWN);
			if (!dns.isEmpty()) {
				Set<Entry<T>> dtns = findTNS(vi, Direction.DOWN);
				double f = 0;
				for (Entry<T> vj: dns) {
					double fy1 = Math.abs(vi.getNewRect().getY() - vj.getNewRect().getMaxY()); 
					double fy2 = Math.abs(vi.getNewRect().getMaxY() - vj.getNewRect().getY());
					double delta = Math.min(fy1, fy2);
					if (delta == fy2 && (delta > f || f == 0))
						f = delta;
				}
				if (f != 0) {
					for (Entry<T> e: dtns)
						e.getNewRect().y += f + yGap;
				}
			}
		}
	}
	
	private static boolean intersects(Rectangle r1, Rectangle r2) {
		return r1 != null && r1.intersects(r2);
	}
	
	private Set<Entry<T>> findNS(Entry<T> argEntry, Direction dir) {
		HashSet<Entry<T>> nsSet = new HashSet<Entry<T>>();
		for (Entry<T> e: entries) {
			Rectangle r = e.getNewRect();
			Rectangle rect = argEntry.getNewRect();
			if (!rect.equals(r) && intersects(r, rect)) {
				if ((dir == Direction.LEFT && r.getX() < rect.getX()) ||
					(dir == Direction.RIGHT && r.getX() >= rect.getX()) || 
					(dir == Direction.UP && r.getY() < rect.getY()) ||
					(dir == Direction.DOWN && r.getY() >= rect.getY()))
					nsSet.add(e);
			}
		}
		return nsSet;
	}
	
	private Set<Entry<T>> findTNS(Entry<T> v, Direction dir) {
		HashSet<Entry<T>> tnsSet = new HashSet<Entry<T>>();
		HashSet<Entry<T>> oldSet = new HashSet<Entry<T>>();
		LinkedList<Entry<T>> tempList = new LinkedList<Entry<T>>();
		int i = 1;
		while (true) {
			oldSet.clear();
			tempList.clear();
			if (i == 1) {
				Set<Entry<T>> nsv = findNS(v, dir);
				for (Entry<T> u: nsv)
					tnsSet.addAll(findNS(u, dir));
				tnsSet.addAll(nsv);
				tnsSet.remove(v);
			} else {
				oldSet.addAll(tnsSet);
				for (Entry<T> u: tnsSet)
					tempList.addAll(findNS(u, dir));
				tnsSet.addAll(tempList);
				tnsSet.remove(v);
				if (tnsSet.equals(oldSet))
					break;
			}
			i++;
		}
		return tnsSet;
	}
	
	public interface Entry<T> {
		T getElement();
		Rectangle getOldRect();
		Rectangle getNewRect();
		double offsetX();
		double offsetY();
	}
}
