package model;

/**
 * Represents an edge (relationship) between two nodes in the graph.
 *
 * @author uuifx
 */
public class Edge {
    private final Node source;
    private final Node target;
    private final RelationshipTypes relationship;

    /**
     * Creates a new edge with the given source, target, and relationship.
     *
     * @param source The source node
     * @param target The target node
     * @param relationship The relationship type
     */
    public Edge(Node source, Node target, RelationshipTypes relationship) {
        this.source = source;
        this.target = target;
        this.relationship = relationship;
    }

    /**
     * Gets the source node of this edge.
     *
     * @return The source node
     */
    public Node getSource() {
        return source;
    }

    /**
     * Gets the target node of this edge.
     *
     * @return The target node
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Gets the relationship type of this edge.
     *
     * @return The relationship type
     */
    public RelationshipTypes getRelationship() {
        return relationship;
    }

    /**
     * Creates the inverse edge of this edge.
     *
     * @return The inverse edge
     */
    public Edge createInverse() {
        return new Edge(target, source, relationship.getInverse());
    }

    /**
     * Checks if the relationship represented by this edge is valid.
     * Different relationship types have restrictions on the types of nodes they can connect.
     *
     * @return true if the relationship is valid, false otherwise
     */
    public boolean isValidRelationship() {
        switch (relationship) {
            case CONTAINS:
                return source.isCategory();
            case CONTAINED_IN:
                return target.isCategory();
            case PART_OF:
            case HAS_PART:
            case SUCCESSOR_OF:
            case PREDECESSOR_OF:
                return source.isProduct() && target.isProduct();
            default:
                return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Edge other = (Edge) obj;
        return source.equals(other.source)
                && target.equals(other.target)
                && relationship == other.relationship;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + source.hashCode();
        result = prime * result + target.hashCode();
        result = prime * result + relationship.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return source.toString() + "-[" + relationship.toString() + "]->" + target.toString();
    }
}