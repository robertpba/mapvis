package mapwiki.common;

public class Tick {
	private int n;
	
	public boolean nextIs(int testValue) {
		inc();
		if (is(testValue)) {
			reset();
			return true;
		} else {
			return false;
		}
	}
	
	public boolean is(int testValue) {
		return n >= testValue;
	}

	public void inc() {
		n++;
	}
	
	public void reset() {
		n = 0;
	}
	
	public int current() {
		return n;
	}

	@Override
	public String toString() {
		return Integer.toString(n);
	}
}
