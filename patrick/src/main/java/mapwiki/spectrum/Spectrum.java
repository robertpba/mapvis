package mapwiki.spectrum;

import java.util.AbstractCollection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Spectrum<E> extends AbstractCollection<E> {
	private int count = 0;
	private Entry<E> leftMost = null;
	private Entry<E> rightMost = null;
	private Set<E> internalSet = new HashSet<E>();

	public boolean addLeft(E element) {
		if (contains(element))
			return false;
		Entry<E> newEntry = new Entry<E>(element);
		if (isEmpty()) {
			leftMost = newEntry;
			rightMost = newEntry;
		} else {
			leftMost.left = newEntry;
			newEntry.right = leftMost;
			leftMost = newEntry;
		}
		internalSet.add(element);
		count++;
		return true;
	}
	
	public boolean addRight(E element) {
		if (contains(element))
			return false;
		Entry<E> newEntry = new Entry<E>(element);
		if (isEmpty()) {
			leftMost = newEntry;
			rightMost = newEntry;
		} else {
			rightMost.right = newEntry;
			newEntry.left = rightMost;
			rightMost = newEntry;
		}
		internalSet.add(element);
		count++;
		return true;
	}
	
	@Override
	public void clear() {
		leftMost = rightMost = null;
		count = 0;
		internalSet.clear();
	}

	@Override
	public int size() {
		return count;
	}
	
	@Override
	public boolean contains(Object o) {
		return internalSet.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return new SpectrumIterator();
	}

	@Override
	public boolean add(E e) {
		return addRight(e);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Spectrum [ ");
		for (E element: this)
			sb.append(element).append(" ");
		sb.append("]");
		return sb.toString();
	}

	private class Entry<T> {
		T element;
		@SuppressWarnings("unused")
		Entry<T> left = null;
		Entry<T> right = null;
		
		public Entry(T e) {
			this.element = e;
		}
	}
	
	private class SpectrumIterator implements Iterator<E> {
		private Entry<E> next = leftMost;
		private Entry<E> lastReturned;
		private int index = 0;

		@Override
		public boolean hasNext() {
			return index < size();
		}

		@Override
		public E next() {
			lastReturned = next;
			next = next.right;
			index++;
			return lastReturned.element;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove is not allowed so far.");
		}
	}
}
