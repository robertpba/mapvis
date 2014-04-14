package mapwiki.spectrum;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

public class SpectrumVisualization {
	private static String catTreeFile;
	private static String cosSimFile;
	private static String outputFile;
	private static String aggSimFile;
	private CatInfoProvider provider;
	
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.err.println("Usage: java SpectrumVisualization cattree.txt cossim.txt [aggsim.txt] output_file");
			return;
		}
		
		Date startTime = new Date();
		System.err.println("Starting at " + startTime);
		catTreeFile = args[0];
		cosSimFile = args[1];
		outputFile = args.length == 4 ? args[3] : args[2];
		aggSimFile = args.length == 4 ? args[2] : null;
		new SpectrumVisualization().run();
		System.err.printf("Done. Running time: %,dms.%n", System.currentTimeMillis() - startTime.getTime());
	}
	
	public SpectrumVisualization() throws IOException {
		provider = new CatInfoProviderImpl(catTreeFile, cosSimFile, aggSimFile);
	}
	
	private Spectrum<String> createTopSpectrum() {
		Spectrum<String> spectrum = new Spectrum<String>();
		SortedSet<CatLink> sortedLinks = new TreeSet<CatLink>(new ReverseCatLinkComparator());
		sortedLinks.addAll(provider.findLinksBetweenTopCategories());
		arrangeSpectrum(spectrum, sortedLinks);
		return spectrum;
	}
	
	private Spectrum<String> createSubCategorySpectrum(String category) {
		Spectrum<String> spectrum = new Spectrum<String>();
		SortedSet<CatLink> sortedLinks = new TreeSet<CatLink>(new ReverseCatLinkComparator());
		sortedLinks.addAll(provider.findLinksBetweenSubCategoriesOf(category));
		if (!sortedLinks.isEmpty())
			arrangeSpectrum(spectrum, sortedLinks);
		else
			spectrum.add(category);		// Handles no sub-categories exist.
		return spectrum;
	}
	
	private void arrangeSpectrum(Spectrum<String> sp, SortedSet<CatLink> links) {
		if (links.isEmpty())
			throw new IllegalArgumentException("Links are empty.");
		
		// Insert the categories with the highest similarity.
		CatLink entry = links.first();
		String currentLeft = entry.getCategory1();
		String currentRight = entry.getCategory2();
		sp.add(currentLeft);
		sp.add(currentRight);
		links.remove(entry);
		
		while (!links.isEmpty()) {
			// Find suitable category that matches either the left or right node.
			AddDirection dir = null;
			String current = null;
			for (CatLink cl: links) {
				entry = cl;
				if (currentLeft.equals(cl.getCategory1())) {
					current = cl.getCategory2();
					dir = AddDirection.LEFT;
					break;
				} else if (currentLeft.equals(cl.getCategory2())) {
					current = cl.getCategory1();
					dir = AddDirection.LEFT;
					break;
				} else if (currentRight.equals(cl.getCategory1())) {
					current = cl.getCategory2();
					dir = AddDirection.RIGHT;
					break;
				} else if (currentRight.equals(cl.getCategory2())) {
					current = cl.getCategory1();
					dir = AddDirection.RIGHT;
					break;
				}
			}
			
			// Either left or right can match the pair.
			if (current != null) {
				if (dir == AddDirection.LEFT) {
					sp.addLeft(current);
					currentLeft = current;
				} else {
					sp.addRight(current);
					currentRight = current;
				}
			} else {
				entry = links.first();
				if (sp.addLeft(entry.getCategory1()))
					currentLeft = entry.getCategory1();
				if (sp.addRight(entry.getCategory2()))
					currentRight = entry.getCategory2();
			}
			
			// Modify the spectrum.
			links.remove(entry);
		}
	}
	
	public void run() throws ClassNotFoundException, SQLException, IOException {
		DrawingPlane dp = new DrawingPlane(provider);
		dp.setOutputFilename(outputFile);
		Spectrum<String> sp = createTopSpectrum();
		dp.setTopSpectrum(sp);
		for (String subCategory: sp) {
			Spectrum<String> subSp = createSubCategorySpectrum(subCategory);
			dp.addSubCatSpectrum(subCategory, subSp);
		}
		dp.draw();
	}
	
	private class ReverseCatLinkComparator implements Comparator<CatLink> {
		@Override
		public int compare(CatLink link1, CatLink link2) {
			// Reverse the order.
			return link2 == null ? -1 : link2.compareTo(link1);
		}
	}
}
