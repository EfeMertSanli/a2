package model;

/**
 * Abstract class representing a node in the product graph.
 * A node can be either a product or a category.
 *
 * @author uuifx
 */
public abstract class Node {
    private final String name;

    /**
     * Creates a new node with the given name.
     *
     * @param name The name of the node
     */
    public Node(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the node.
     *
     * @return The name of the node
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the lowercase version of the node name for case-insensitive comparison.
     *
     * @return The lowercase node name
     */
    public String getLowerCaseName() {
        return name.toLowerCase();
    }

    /**
     * Checks if this node is a product.
     *
     * @return true if this node is a product, false otherwise
     */
    public abstract boolean isProduct();

    /**
     * Checks if this node is a category.
     *
     * @return true if this node is a category, false otherwise
     */
    public abstract boolean isCategory();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Node other = (Node) obj;
        return this.getLowerCaseName().equals(other.getLowerCaseName());
    }

    @Override
    public int hashCode() {
        return getLowerCaseName().hashCode();
    }

    @Override
    public String toString() {
        return name.toLowerCase();
    }
}