package mapwiki.layout;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Random;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mapwiki.common.AggregatedSim;
import mapwiki.common.PageDatabase;
import mapwiki.common.WikiSimilarity;
import mapwiki.common.WikiTree;
import mapwiki.layout.SpringLayoutForCategory.CenterNodeMode;
import mapwiki.layout.categoryprovider.MemoryCategoryProvider;

public final class LayoutGraphic {
	// A random generator with a specified seed for use of all classes.
	public static final Random rand = new Random(0x8d14c220);
	
	static String outputFile;
	static int affectRange;
	static int layoutDepth;
	static double areaFrac;
	static String exportFile;
	static String propertyFile;
	static String tmpDir;
	static boolean debug;
	static SpringLayoutForCategory.CenterNodeMode centeringMode = CenterNodeMode.LARGEST;
	
	// Properties read from the file.
	private static Properties props = new Properties();
	
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		
		OptionParser op = new OptionParser();
		op.accepts("tree").withRequiredArg().describedAs("tree_file").defaultsTo("cattree.txt");
		op.accepts("sim").withRequiredArg().describedAs("similarity_file").defaultsTo("cossim.txt");
		op.accepts("as").withRequiredArg().describedAs("agg_similarity_file").defaultsTo("aggsim.txt");
		op.accepts("city").withRequiredArg().describedAs("city_db_file");
		op.accepts("l").withRequiredArg().describedAs("deep_to_level").ofType(Integer.class).defaultsTo(2);
		op.accepts("tmp").withRequiredArg().describedAs("tmp_dir").defaultsTo("");
		op.accepts("o").withRequiredArg().describedAs("visualization_output").defaultsTo("mapwiki.png");
		op.accepts("exp").withRequiredArg().describedAs("export_output_file");
		op.accepts("p").withRequiredArg().describedAs("layout_properties");
		op.accepts("d", "debug");
		op.acceptsAll(Arrays.asList("?", "h", "help"), "Shows the help screen.");
		
		op.accepts("cl", "centering_largest" + (centeringMode == CenterNodeMode.LARGEST ? " (default)" : ""));
		op.accepts("cr", "centering_related" + (centeringMode == CenterNodeMode.MOST_RELATED ? " (default)" : ""));
		op.accepts("cs", "centering_smallest" + (centeringMode == CenterNodeMode.SMALLEST ? " (default)" : ""));
		
		// Check empty arguments.
		OptionSet options = op.parse(args);
		if (args.length == 0 || options.has("?") || options.has("h") || options.has("help")) {
			op.printHelpOn(System.err);
			return;
		}
		
		// Read from the properties file.
		debug = options.has("d");
		String propFile = (String)options.valueOf("p");
		if (propFile == null)
			throw new IllegalArgumentException("No drawing properties specified.");
		FileInputStream fis = new FileInputStream(propFile);
		try {
			props.load(fis);
		} finally {
			fis.close();
		}
		
		// Read wiki data.
		WikiTree wt = new WikiTree((String)options.valueOf("tree"));
		WikiSimilarity ws = new WikiSimilarity((String)options.valueOf("sim"));
		AggregatedSim as = new AggregatedSim((String)options.valueOf("as"));
		PageDatabase pdb = null;
		if (options.has("city"))
			pdb = new PageDatabase((String)options.valueOf("city"));
		
		tmpDir = (String)options.valueOf("tmp");
		affectRange = propertyAsInteger("AFFECT_RANGE");
		areaFrac = Double.parseDouble(propertyAsString("AREA_FRACTION"));
		layoutDepth = (Integer)options.valueOf("l");
		outputFile = (String)options.valueOf("o");
		exportFile = options.has("exp") ? (String)options.valueOf("exp") : null;
		
		if (options.has("cl"))
			centeringMode = CenterNodeMode.LARGEST;
		else if (options.has("cr"))
			centeringMode = CenterNodeMode.MOST_RELATED;
		else if (options.has("cs"))
			centeringMode = CenterNodeMode.SMALLEST;
		
		MemoryCategoryProvider mcp = new MemoryCategoryProvider(wt, ws, as);
		HexagonLayout hl = new HexagonLayout(mcp, pdb);
		hl.run();
		System.err.printf("Running time: %,dms%n", System.currentTimeMillis() - startTime);
	}

	public static int propertyAsInteger(String key) {
		try {
			String value = props.getProperty(key);
			if (value == null)
				throw new NoSuchElementException("Cannot find property: " + key);
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			throw new NumberFormatException("Cannot convert property value: " + key);
		}
	}
	
	public static String propertyAsString(String key) {
		String value = props.getProperty(key);
		if (value == null)
			throw new NoSuchElementException("Cannot find property: " + key);
		else
			return value;
	}
	
	public static Collection<String> propertiesToStrings() {
		ArrayList<String> list = new ArrayList<String>(props.size());
		for (Object key: props.keySet())
			list.add(String.format("%s-%s", key, props.getProperty((String)key)));
		Collections.sort(list);
		return list;
	}
}
