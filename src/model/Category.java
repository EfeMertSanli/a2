package model;

/**
 * Represents a category node in the graph.
 * A category has only a name, no ID.
 *
 * @author uuifx
 */
public class Category extends Node {

    /**
     * Creates a new category with the given name.
     *
     * @param name The name of the category
     */
    public Category(String name) {
        super(name);
    }

    @Override
    public boolean isProduct() {
        return false;
    }

    @Override
    public boolean isCategory() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}