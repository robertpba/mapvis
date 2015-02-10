package mapwiki.layout.categoryprovider;


import mapvis.common.datatype.Node;
import mapwiki.layout.Category;
import mapwiki.layout.CategoryProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NodeCategoryProvider implements CategoryProvider {
    NodeCategory root;
    List<NodeCategory> nodes = new ArrayList<>();

    public NodeCategoryProvider(Node root){
        this.root = add(root);
    }

    public NodeCategory add(Node root){
        NodeCategory cat = new NodeCategory();
        cat.setPageID(Integer.parseInt(root.getId()));
        cat.setPageTitle(root.getLabel());

        cat.children = root.getChildren().stream()
                .map(this::add)
                .collect(Collectors.toList());

        cat.children.forEach(c -> {
            c.parent = cat;
            nodes.add(c);
        });

        return cat;
    }


    @Override
    public Category findRoot() {
        return root;
    }

    @Override
    public Category findParent(Category c) {
        return ((NodeCategory)c).parent;
    }

    @Override
    public List<Category> findChildren(Category c) {
        return ((NodeCategory)c).children.stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> findNodesOnLevel(int level) {
        return nodes.stream()
                .filter(n->(int)n.node.getVal("level") == level)
                .collect(Collectors.toList());
    }

    @Override
    public double getSimilarity(Category c1, Category c2) {
        return 0.5;
    }

    @Override
    public int getCoCount(Category c1, Category c2) {
        return 0;
    }

    @Override
    public int getDirectCount(Category c) {
        return 0;
    }

    @Override
    public float getAltitude(Category c) {
        return 0;
    }

    @Override
    public int getLevel(Category c) {
        return (int)((NodeCategory) c).node.getVal("level");
    }

    @Override
    public Category findTopCategory(Category c) {
        return root;
    }

    @Override
    public Category findByID(int pageID) {
        return nodes.stream()
                .filter(n->n.node.getId().equals(Integer.toString(pageID)))
                .findFirst().get();
    }

    @Override
    public Category findByTitle(String pageTitle) {
        return nodes.stream()
                .filter(n->n.node.getLabel().equals(pageTitle))
                .findFirst().get();
    }
}
