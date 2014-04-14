package mapwiki.preprocessor;

public final class BitwiseLong {
	public static long makeKey(int i1, int i2) {
		return i1 <= i2 ? makeLong(i1, i2) : makeLong(i2, i1);
	}
	
	public static long makeLong(int i1, int i2) {
		return (long)i1 << 32 | (long)i2;
	}
	
	public static int loPart(long value) {
		return (int)(value & 0xffffffff);
	}
	
	public static int hiPart(long value) {
		return (int)(value >> 32);
	}

	private BitwiseLong() {
	}
}
