package model;

/**
 * Represents a product node in the graph.
 * A product has a name and an ID.
 *
 * @author uuifx
 */
public class Product extends Node {
    private final int id;
    /**
     * Creates a new product with the given name and ID.
     * @param name The name of the product
     * @param id The ID of the product
     */
    public Product(String name, int id) {
        super(name);
        this.id = id;
    }
    /**
     * Gets the ID of the product.
     * @return The ID of the product
     */
    public int getId() {
        return id;
    }

    @Override
    public boolean isProduct() {
        return true;
    }

    @Override
    public boolean isCategory() {
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + ProductConstants.COLON + id;
    }
}