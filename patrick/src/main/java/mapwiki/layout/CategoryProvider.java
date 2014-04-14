package mapwiki.layout;

import java.util.List;

public interface CategoryProvider {
	Category findRoot();
	Category findParent(Category c);
	List<Category> findChildren(Category c);
	List<Category> findNodesOnLevel(int level);
	double getSimilarity(Category c1, Category c2);
	int getCoCount(Category c1, Category c2);
	int getDirectCount(Category c);
	float getAltitude(Category c);
	int getLevel(Category c);
	Category findTopCategory(Category c);
	Category findByID(int pageID);
	Category findByTitle(String pageTitle);
}
