package mapwiki.layout.overlapremoval;

import java.awt.Rectangle;

import mapwiki.layout.overlapremoval.FTAOverlapRemoval.Entry;

class FTAEntry<T> implements Entry<T> {
	private T element;
	private Rectangle oldRect;
	private Rectangle newRect;
	
	public FTAEntry(T element, Rectangle rect) {
		this.element = element;
		this.oldRect = rect;
		this.newRect = (Rectangle)rect.clone();
	}

	@Override
	public T getElement() {
		return element;
	}

	@Override
	public Rectangle getNewRect() {
		return newRect;
	}

	@Override
	public Rectangle getOldRect() {
		return oldRect;
	}

	@Override
	public String toString() {
		return element.toString();
	}

	@Override
	public double offsetX() {
		return newRect.x - oldRect.x;
	}

	@Override
	public double offsetY() {
		return newRect.y - oldRect.y;
	}
}
