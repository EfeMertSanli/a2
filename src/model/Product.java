package model;

/**
 * Represents a product node in the graph.
 * A product has a name and an ID.
 *
 * @author uuifx
 */
public class Product extends Node {
    private static final int MIN_PRODUCT_ID = 0;
    private static final String COLON = ":";

    private final int id;

    /**
     * Creates a new product with the given name and ID.
     *
     * @param name The name of the product
     * @param id The ID of the product
     */
    public Product(String name, int id) {
        super(name);
        if (id < MIN_PRODUCT_ID) {
            throw new IllegalArgumentException("Product ID must be a non-negative integer");
        }
        this.id = id;
    }

    /**
     * Gets the ID of the product.
     *
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Product other = (Product) obj;
        return this.getLowerCaseName().equals(other.getLowerCaseName());
    }

    @Override
    public String toString() {
        return super.toString() + COLON + id;
    }
}