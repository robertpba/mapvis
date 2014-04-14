package mapwiki.preprocessor;

class CalcCosSim2Pair {
	private int count1;
	private int count2;
	private int coCount = 1;
	
	public CalcCosSim2Pair(int count1, int count2) {
		if (count1 == 0 || count2 == 0)
			throw new IllegalArgumentException(String.format("Zero is found in arguments (C1=%d, C2=%d).",
					count1, count2));
		this.count1 = count1;
		this.count2 = count2;
	}
	
	public double cosSim() {
		return (double)coCount / Math.sqrt((double)count1 * count2);
	}

	public int getCount1() {
		return count1;
	}

	public void setCount1(int count1) {
		this.count1 = count1;
	}

	public int getCount2() {
		return count2;
	}

	public void setCount2(int count2) {
		this.count2 = count2;
	}

	public int getCoCount() {
		return coCount;
	}

	public void setCoCount(int coCount) {
		this.coCount = coCount;
	}
	
	public void increaseCoCount() {
		this.coCount++;
	}
}
