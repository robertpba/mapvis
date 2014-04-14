package mapwiki.layout.overlapremoval;

import java.util.Comparator;

import mapwiki.layout.overlapremoval.FTAOverlapRemoval.CoordType;
import mapwiki.layout.overlapremoval.FTAOverlapRemoval.Entry;

class EntryComparator<T> implements Comparator<FTAOverlapRemoval.Entry<T>> {
	private CoordType coordType;

	@Override
	public int compare(Entry<T> obj1, Entry<T> obj2) {
		double d1 = coordType == CoordType.X ? obj1.getNewRect().x : obj1.getNewRect().y;
		double d2 = coordType == CoordType.X ? obj2.getNewRect().x : obj2.getNewRect().y;
		return Double.compare(d1, d2);
	}
	
	public EntryComparator(CoordType coordToCompare) {
		coordType = coordToCompare;
	}
}
