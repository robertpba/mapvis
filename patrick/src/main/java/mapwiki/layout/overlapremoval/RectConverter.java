package mapwiki.layout.overlapremoval;

import java.awt.Rectangle;

public interface RectConverter<T> {
	Rectangle convert(T obj);
}
