package mapwiki.layout.categoryprovider;

import java.util.ArrayList;
import java.util.List;

import mapwiki.common.WikiTreeEntry;
import mapwiki.layout.Category;

public class MemoryCategory extends Category {
	WikiTreeEntry treeEntry;
	public List<Category> children = new ArrayList<Category>();
	Category topCategory;
	
	public MemoryCategory(WikiTreeEntry entry) {
		super(entry.getPageID(), entry.getPageTitle());
		this.treeEntry = entry;
	}
}
