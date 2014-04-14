package mapwiki.spectrum;

import java.awt.Color;
import java.util.Collection;

public interface CatInfoProvider {
	int getCoAssignedArticleCount(String cat1, String cat2);
	Color getColour(String category);
	Collection<? extends CatLink> findLinksBetweenTopCategories();
	Collection<String> findTopCategories();
	Collection<? extends CatLink> findLinksBetweenSubCategoriesOf(String category);
}
